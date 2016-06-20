
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY bitbucket-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./bitbucket-properties-builder.sh && \
  java -jar bitbucket-scm-collector*.jar --spring.config.location=/hygieia/hygieia-bitbucket-scm-collector.properties

