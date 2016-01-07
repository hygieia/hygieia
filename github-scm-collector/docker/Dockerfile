
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY github-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./github-properties-builder.sh && \
  java -jar github-scm-collector*.jar --spring.config.location=/hygieia/hygieia-github-scm-collector.properties

