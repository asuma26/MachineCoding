#!/usr/bin/env bash

cd ..

env=$1
env=${env:-local}

echo "################ Buidling thanks-segment-manager for environment : $env ###################"
mvn clean install -P thanks-segment

echo "################ Deploying thanks-segment-manager for environment : $env ###################"
$(aws ecr get-login --no-include-email --region ap-south-1)
docker build -f thanks-segment-manager/scripts/Dockerfile.local -t "thanks-segment" --build-arg profile=${env} .