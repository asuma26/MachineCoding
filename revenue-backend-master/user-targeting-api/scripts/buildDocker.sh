#!/usr/bin/env bash

cd ..

env=$1
env=${env:-local}

echo "################ Buidling user-targeting-api for environment : $env ###################"
mvn clean install -P ut-api

echo "################ Building user-targeting-api for environment : $env ###################"
$(aws ecr get-login --no-include-email --region ap-south-1)
docker build -f user-targeting-api/scripts/Dockerfile.local -t "user-targeting-api" --build-arg profile=${env} .
