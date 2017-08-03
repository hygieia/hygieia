
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY artifactory-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./artifactory-properties-builder.sh && \
  java -jar artifactory-artifact-collector*.jar --spring.config.location=/hygieia/hygieia-artifactory-artifact-collector.properties

