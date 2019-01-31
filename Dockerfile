FROM       adoptopenjdk/openjdk11:alpine

MAINTAINER Fady Matar <fady.matar@gmail.com>

#COPY ./ /usr/local/
RUN apk add --update bash wget tar && rm -rf /var/cache/apk/*
# Download vertx
RUN wget -P /var/cache/vertx/ https://bintray.com/artifact/download/vertx/downloads/vert.x-3.6.2.tar.gz
# Extract vertx
RUN mkdir -p /usr/local
RUN tar xvzf /var/cache/vertx/vert.x-3.6.2.tar.gz -C /usr/local
# Make vertx binary executable
RUN chmod +x /usr/local/vertx/bin/vertx
## Set path
ENV VERTX_HOME /usr/local/vertx
## Export PATH
ENV PATH $VERTX_HOME/bin:$PATH
#
#
## Deploy verticle jar and configuration
#RUN mkdir -p /usr/verticles
#COPY target/showcase-0.0.1-SNAPSHOT.jar /usr/verticles/
#COPY conf/local/service-config.json /usr/verticles/service-config.json
#
#RUN chmod -R 777 /usr/verticles
#RUN chmod -R 777 /usr/verticles/*
#
#CMD ["vertx","run","io.slixes.showcase.ShowcaseService","-cp","/usr/verticles/showcase-0.0.1-SNAPSHOT.jar","--conf","/usr/verticles/service-config.json"]
#
