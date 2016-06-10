
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-udeploy-deployment-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY udeploy-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./udeploy-properties-builder.sh && \
  java -jar udeploy-deployment-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

