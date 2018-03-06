
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia

COPY target/${JAR_FILE} /hygieia/github-graphql-scm-collector.jar
COPY github-graphql-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./github-graphql-properties-builder.sh && \
  java -jar github-graphql-scm-collector.jar --spring.config.location=/hygieia/hygieia-github-graphql-scm-collector.properties
