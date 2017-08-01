
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY chat-ops-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./chat-ops-properties-builder.sh && \
  java -jar chat-ops-collector*.jar --spring.config.location=/hygieia/hygieia-chat-ops-collector.properties

