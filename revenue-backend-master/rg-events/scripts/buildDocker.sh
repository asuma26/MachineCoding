#!/usr/bin/env bash

cd ..

env=$1
env=${env:-local}

echo "################ Buidling user-targeting-api for environment : $env ###################"
mvn clean install -P events -Denv=${env}

echo "################ Building user-targeting-api for environment : $env ###################"
$(aws ecr get-login --no-include-email --region ap-south-1)
docker build -f rg-events/scripts/Dockerfile.local -t "rg-events" --build-arg profile=${env} .
