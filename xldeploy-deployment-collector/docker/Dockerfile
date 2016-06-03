
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-xldeploy-deployment-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY xldeploy-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./xldeploy-properties-builder.sh && \
  java -jar xldeploy-deployment-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

