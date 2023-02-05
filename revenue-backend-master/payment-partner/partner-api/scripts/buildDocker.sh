#!/usr/bin/env bash

cd ..

env=$1
env=${env:-local}

echo "################ Buidling partner-api for environment : $env ###################"
mvn clean install -P payment-partner

echo "################ Building partner-api for environment : $env ###################"
$(aws ecr get-login --no-include-email --region ap-south-1)
docker build -f payment-partner/partner-api/scripts/Dockerfile.local -t "partner-api" --build-arg profile=${env} .
