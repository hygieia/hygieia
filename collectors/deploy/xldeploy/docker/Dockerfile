
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY xldeploy-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./xldeploy-properties-builder.sh && \
  java -jar xldeploy-deployment-collector*.jar --spring.config.location=/hygieia/hygieia-xldeploy-deployment-collector.properties

