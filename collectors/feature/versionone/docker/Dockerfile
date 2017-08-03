
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY versionone-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./versionone-properties-builder.sh && \
  java -jar versionone-feature-collector*.jar --spring.config.location=/hygieia/hygieia-versionone-feature-collector.properties

