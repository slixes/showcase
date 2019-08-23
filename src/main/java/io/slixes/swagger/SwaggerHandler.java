package io.slixes.swagger;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SwaggerHandler extends Handler<RoutingContext> {

  static SwaggerHandler create(Router router) {
    return new SwaggerHandlerImpl(router);
  }

  class SwaggerHandlerImpl implements SwaggerHandler {
    Router router;

    public SwaggerHandlerImpl(Router router) {
      this.router = router;
    }

    @Override
    public void handle(RoutingContext routingContext) {
      routingContext.response().setStatusCode(200).end(Json.pretty(openAPIDoc(router)));
    }

    private void mapParameters(Field field, Map<String, Object> map) {
      Class type = field.getType();
      Class componentType = field.getType().getComponentType();

      if (isPrimitiveOrWrapper(type)) {
        Schema primitiveSchema = new Schema();
        primitiveSchema.type(field.getType().getSimpleName());
        map.put(field.getName(), primitiveSchema);
      } else {
        HashMap<String, Object> subMap = new HashMap<>();

        if (isPrimitiveOrWrapper(componentType)) {
          HashMap<String, Object> arrayMap = new HashMap<>();
          arrayMap.put("type", componentType.getSimpleName() + "[]");
          subMap.put("type", arrayMap);
        } else {
          subMap.put("$ref", "#/components/schemas/" + componentType.getSimpleName());
        }

        map.put(field.getName(), subMap);
      }
    }

    private Boolean isPrimitiveOrWrapper(Type type) {
      return type.equals(Double.class) ||
        type.equals(Float.class) ||
        type.equals(Long.class) ||
        type.equals(Integer.class) ||
        type.equals(Short.class) ||
        type.equals(Character.class) ||
        type.equals(Byte.class) ||
        type.equals(Boolean.class) ||
        type.equals(String.class);
    }

    public ImmutableSet<ClassPath.ClassInfo> getClassesInPackage(String pckgname) throws IOException {
      ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
      ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(pckgname);
      return classes;
    }

    public OpenAPI openAPIDoc(Router router) {
      OpenAPI openAPIDoc = OpenApiRoutePublisher.publishOpenApiSpec(
        router,
        "spec",
        "Vertx Swagger Auto Generation",
        "1.0.0",
        "http://" + "localhost" + ":" + "8080" + "/"
      );

      /* Tagging section. This is where we can group end point operations; The tag name is then used in the end point annotation
       */
      openAPIDoc.addTagsItem(new io.swagger.v3.oas.models.tags.Tag().name("Product").description("Product operations"));


      // Generate the SCHEMA section of Swagger, using the definitions in the Model folder
      try {
        ImmutableSet<ClassPath.ClassInfo> modelClasses = getClassesInPackage("io.slixes.showcase.model");
        Map<String, Object> map = new HashMap<>();

        for (ClassPath.ClassInfo modelClass : modelClasses) {

          Field[] fields = FieldUtils.getFieldsListWithAnnotation(modelClass.load(), Required.class).toArray(new
            Field[0]);
          List<String> requiredParameters = new ArrayList<>();

          for (Field requiredField : fields) {
            requiredParameters.add(requiredField.getName());
          }

          fields = modelClass.load().getDeclaredFields();

          for (Field field : fields) {
            mapParameters(field, map);
          }

          openAPIDoc.schema(modelClass.getSimpleName(),
            new Schema()
              .title(modelClass.getSimpleName())
              .type("object")
              .required(requiredParameters)
              .properties(map)
          );
          map = new HashMap<String, Object>();

        }
        //
        return openAPIDoc;
      } catch (IOException ex) {
        //TODO:Log a warning that no models were found. It would be probably better to scan the classes instead of specifying a package to scan
        ex.printStackTrace();
        return null;
      }
    }
  }
}
