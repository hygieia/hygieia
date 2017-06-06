
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY ca-apm-properties-builder.sh /hygieia/
WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./ca-apm-properties-builder.sh && \
	java -jar ca-apm-collector-*.jar --spring.config.location=/hygieia/hygieia-ca-apm-collector.properties

