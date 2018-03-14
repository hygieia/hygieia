
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/jenkins-cucumber-test-collector.jar
COPY jenkins-cucumber-test-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./jenkins-cucumber-test-properties-builder.sh && \
  java -jar jenkins-cucumber-test-collector.jar --spring.config.location=/hygieia/config/hygieia-jenkins-cucumber-test-collector.properties
