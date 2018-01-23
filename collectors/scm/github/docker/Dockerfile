
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY *.jar /hygieia/
COPY github-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./github-properties-builder.sh && \
  java -jar github-scm-collector*.jar --spring.config.location=/hygieia/config/hygieia-github-scm-collector.properties

