
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-jenkins-build-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY jenkins-build-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./jenkins-build-properties-builder.sh && \
  java -jar jenkins-build-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

