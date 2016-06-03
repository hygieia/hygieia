
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-subversion-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY subversion-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./subversion-properties-builder.sh && \
  java -jar subversion-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

