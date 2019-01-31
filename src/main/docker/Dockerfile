FROM vertx/vertx3
#
#ENV VERTICLE_NAME @vertx.verticle@
#ENV VERTICLE_FILE ./target/showcase-0.0.1-SNAPSHOT.jar
#
## Set the location of the verticles
#ENV VERTICLE_HOME /usr/verticles
#
#EXPOSE 8080
#
## Copy your verticle to the container
#COPY ${project.build.directory}/showcase-0.0.1-SNAPSHOT.jar $VERTICLE_HOME/
#
## Launch the verticle
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]


COPY /Users/fmatar/workspace/src/slixes/showcase/target/showcase-0.0.1-SNAPSHOT.jar /usr/verticles/
RUN chmod -R 777 /usr/verticles
RUN chmod -R 777 /usr/verticles/*
