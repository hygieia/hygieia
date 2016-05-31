
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY gerrit-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./gerrit-scm-properties-builder.sh && \
  java -jar gerrit-scm-collector*.jar --spring.config.location=/hygieia/hygieia-gerrit-scm-collector.properties

