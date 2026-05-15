#!/usr/bin/env sh
set -eu

APP_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$APP_DIR"

COMPOSE_FILE=${COMPOSE_FILE:-docker-compose.deploy.yml}
MAVEN=${MAVEN:-mvn}
DOCKER_COMPOSE=${DOCKER_COMPOSE:-docker compose}

$MAVEN -DskipTests clean package
$DOCKER_COMPOSE -f "$COMPOSE_FILE" up -d --build
$DOCKER_COMPOSE -f "$COMPOSE_FILE" ps
