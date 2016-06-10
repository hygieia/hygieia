
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-stash-scm-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY stash-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./stash-properties-builder.sh && \
  java -jar stash-scm-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

