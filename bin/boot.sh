#!/usr/bin/env bash
docker run --name keycloak -p 8080:8080 -e KEYCLOAK_USER=keycloak -e KEYCLOAK_PASSWORD=keycloak -e DB_VENDOR=h2 jboss/keycloak
