FROM adoptopenjdk/openjdk12:alpine
ENV VERTX_HOME=/usr/local/vertx
EXPOSE @docker.http.port@
WORKDIR /usr/verticles/@project.artifactId@
COPY maven/ /
RUN apk add --update bash wget tar && rm -rf /var/cache/apk/*
RUN mkdir -p /usr/local
RUN wget -P /var/cache/vertx/ https://bintray.com/artifact/download/vertx/downloads/vert.x-@vertx.version@.tar.gz
RUN tar xvzf /var/cache/vertx/vert.x-@vertx.version@.tar.gz -C /usr/local
RUN chmod +x /usr/local/vertx/bin/vertx
RUN chmod -R 700 /usr/verticles
RUN chmod -R 700 /usr/verticles/*
RUN ln -s $VERTX_HOME/bin/vertx /usr/local/bin/
CMD ["vertx","run","@vertx.verticle@","-cp","/usr/verticles/@project.artifactId@/@project.artifactId@-@project.version@.jar", "--conf", "conf/config.json"]
