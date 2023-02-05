#!/usr/bin/env bash
set -e
cd ..

env=$1
env=${env:-local}

echo "################ Buidling wcf-ut-batch-job for environment : $env ###################"
mvn clean -Denv=${env} install -P ut-base

echo "################ Building docker wcf-ut-batch-job for environment : $env ###################"
docker build -f user-targeting-batch/scripts/Dockerfile.local -t "wcf-ut-batch-job" --build-arg profile=${env} .