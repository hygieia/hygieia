
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY jenkins-cucumber-test-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./jenkins-cucumber-test-properties-builder.sh && \
  java -jar jenkins-cucumber-test-collector*.jar --spring.config.location=/hygieia/hygieia-jenkins-cucumber-test-collector.properties

