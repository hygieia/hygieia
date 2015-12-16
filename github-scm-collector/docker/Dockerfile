
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY git-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./git-properties-builder.sh && java -jar github-scm-collector*.jar --spring.config.location=/hygieia/hygieia-github-scm-collector.properties
