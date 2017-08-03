
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY sonar-codequality-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./sonar-codequality-properties-builder.sh && \
  java -jar sonar-codequality-collector*.jar --spring.config.location=/hygieia/hygieia-sonar-codequality-collector.properties

