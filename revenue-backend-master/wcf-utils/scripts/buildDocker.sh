#!/usr/bin/env bash

cd ..

env=$1
env=${env:-local}

echo "################ Buidling wcf-utils for environment : $env ###################"
mvn clean install -P wcf-utils

echo "################ Building wcf-utils for environment : $env ###################"
docker build -f wcf-utils/scripts/Dockerfile -t "wcf-utils" --build-arg profile=${env}

### $(aws ecr get-login --no-include-email --region ap-south-1)
###  docker build -f wcf-utils/scripts/Dockerfile.local -t "wcf-utils" --build-arg profile=${env} .
