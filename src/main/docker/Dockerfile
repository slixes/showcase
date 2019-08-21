FROM adoptopenjdk/openjdk12:slim
ENV VERTX_HOME=/usr/local/vertx
ENV JAVA_ENABLE_DEBUG=true
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n"
EXPOSE @docker.http.port@
WORKDIR /usr/verticles/@project.artifactId@
COPY maven/ /
RUN apt update && apt install -y wget && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /usr/local
RUN wget -P /var/cache/vertx/ https://bintray.com/artifact/download/vertx/downloads/vert.x-@vertx.version@.tar.gz
RUN tar xvzf /var/cache/vertx/vert.x-@vertx.version@.tar.gz -C /usr/local
RUN chmod +x /usr/local/vertx/bin/vertx
RUN chmod -R 700 /usr/verticles
RUN chmod -R 700 /usr/verticles/*
RUN ln -s $VERTX_HOME/bin/vertx /usr/local/bin/
CMD ["vertx","run","@vertx.verticle@","-cp","/usr/verticles/@project.artifactId@/@project.artifactId@-@project.version@.jar", "--conf", "conf/config.json"]
