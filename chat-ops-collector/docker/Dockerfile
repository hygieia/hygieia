
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

ENV SPRING_CONFIG_LOCATION=/hygieia/hygieia-chat-ops-collector.properties

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY chat-ops-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./chat-ops-properties-builder.sh && \
  java -jar chat-ops-collector*.jar --spring.config.location="$SPRING_CONFIG_LOCATION"

