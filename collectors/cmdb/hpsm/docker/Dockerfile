
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY hpsm-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./hpsm-properties-builder.sh && \
  java -jar hspm-cmdb-collector*.jar --spring.config.location=/hygieia/hpsm.properties

