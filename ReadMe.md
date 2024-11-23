Plain old Java programm


export PROJECT_ARTIFACTID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
export PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
export TEMURIN_IMAGE_VERSION=$(mvn help:evaluate -Dexpression=temurin.image.version -q -DforceStdout)
export OPENTELEMETRY_VERSION=$(mvn help:evaluate -Dexpression=opentelemetry.version -q -DforceStdout)

docker rmi -f ${PROJECT_ARTIFACTID}:${PROJECT_VERSION}

docker buildx build \
    --load \
    --build-arg PROJECT_ARTIFACTID=${PROJECT_ARTIFACTID} \
    --build-arg PROJECT_VERSION=${PROJECT_VERSION} \
    --build-arg TEMURIN_IMAGE_VERSION=${TEMURIN_IMAGE_VERSION} \
    --build-arg OPENTELEMETRY_VERSION=${OPENTELEMETRY_VERSION} \
    -f src/main/docker/Dockerfile \
    -t ${PROJECT_ARTIFACTID}:${PROJECT_VERSION} \
    .

export PROJECT_ARTIFACTID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
export PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
export TEMURIN_IMAGE_VERSION=$(mvn help:evaluate -Dexpression=temurin.image.version -q -DforceStdout)
export OPENTELEMETRY_VERSION=$(mvn help:evaluate -Dexpression=opentelemetry.version -q -DforceStdout)
export CONTAINER_REGISTRY=$(mvn help:evaluate -Dexpression=container.registry -q -DforceStdout)

docker buildx build \
    --platform linux/amd64,linux/arm/v7,linux/arm64/v8 \
    --push \
    --progress=plain \
    --build-arg PROJECT_ARTIFACTID=${PROJECT_ARTIFACTID} \
    --build-arg PROJECT_VERSION=${PROJECT_VERSION} \
    --build-arg TEMURIN_IMAGE_VERSION=${TEMURIN_IMAGE_VERSION} \
    --build-arg OPENTELEMETRY_VERSION=${OPENTELEMETRY_VERSION} \
    -f src/main/docker/Dockerfile \
    -t ${CONTAINER_REGISTRY}/${PROJECT_ARTIFACTID}:${PROJECT_VERSION} \
    .

mkdir -p /home/${USER}/apps/simlogger/data/logs/
mkdir -p /home/${USER}/apps/simlogger/config/input/

sudo chown 1000:1000 /home/${USER}/apps/simlogger/data/logs/
sudo chown 1000:1000 /home/${USER}/apps/simlogger/config/input/

docker run -d --rm --net primenet \
    -e SLOG_TIMESTAMP_FIELD=TIMESTAMP \
    -e SLOG_TIMESTAMP_PATTERN=yyyyMMDDHHmmss.SSS \
    -e SLOG_TIMEZONE=UTC \
    -v /home/${USER}/apps/simlogger/config/input/replay.log:/data/replay.log \
    -v /home/${USER}/apps/simlogger/data/logs/:/deployments/logs \
    --name simlogger alainpham/simlogger:1.0-SNAPSHOT


docker run -d --name promtail --volume "$PWD/promtail:/etc/promtail" -v /home/alain_pham_grafana_com/apps/simlogger/data/logs/:/logs grafan
a/promtail:main -config.file=/etc/promtail/config.yaml

```
server:
  http_listen_port: 0
  grpc_listen_port: 0

positions:
  filename: /tmp/positions.yaml

client:
  url: https://784539:xxx@logs-prod-012.grafana.net/api/prom/push

scrape_configs:
- job_name: system
  static_configs:
  - targets:
      - localhost
    labels:
      job: varlogs
      __path__: /logs/*.log
  pipeline_stages:
    - json:
        expressions:
          pts: SLOGTS
    - timestamp:
        source: pts
        format: unix
```
