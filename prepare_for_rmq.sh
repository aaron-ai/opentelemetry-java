#!/bin/bash

set -e

function install_dependency() {
  gradle clean publishToMavenLocal
}

ALL_SUB_PROJECTS='bom sdk exporters/otlp api proto context semconv'

BASE_DIR=$(
  cd "$(dirname "$0")" || exit 1
  pwd
)

for project in $ALL_SUB_PROJECTS; do
  echo "================== current module: $project =================="
  cd "${BASE_DIR}/${project}" || exit 1
  install_dependency
done
