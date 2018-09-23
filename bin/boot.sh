#!/usr/bin/env bash
docker run --name showcase-db -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:5.7
docker cp $PWD/bin/db/schema.sql showcase-db:/docker-entrypoint-initdb.d/
