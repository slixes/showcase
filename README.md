# showcase

##Local
Run locally (by default reads from `conf/config.json`)
```
mvn clean package vertx:run  
```

##Docker

- Create docker image:
  ```
  mvn fabric8:build
  ```
- Run in Docker:
  ```
  docker run -ti --name showcase  -v $(pwd)/conf/config.json:/usr/verticles/showcase/conf/config.json -p 8090:8090 -p 8443:8443 io.slixes/showcase:0.0.1-SNAPSHOT
  ```
##Kubernetes 
- Generate kubernetes resources:
  ```
  mvn fabric8:resource
  ```
- Apply resources:
  ```
  mvn fabric8:apply
  ```
- Undeploy:
  ```
  mvn fabric8:undeploy
  ```
