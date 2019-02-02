# showcase

b1c5eba070a6ab78175abe4891ad137cfb9d86cd


Run in Docker:
```
docker run -ti --name showcase  -v $(pwd)/conf/docker/service-config.json:/usr/verticles/showcase/service-config.json -p 8090:8090 -p 8443:8443 io.slixes/showcase:0.0.1-SNAPSHOT
```
Run locally:
```
mvn clean package vertx:run -Dvertx.config=conf/local/service-config.json
```
Create docker image:
```
mvn fabric8:build
```
Generate kubernetes resources:
```
mvn fabric8:resource
```
Apply resources:
```
mvn fabric8:apply
```

Combine the last three steps together:
```
mvn fabric8:deploy
```

Undeploy:
```
mvn fabric8:undeploy
```
