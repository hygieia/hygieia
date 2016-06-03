
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-versionone-feature-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY versionone-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./versionone-properties-builder.sh && \
  java -jar versionone-feature-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

