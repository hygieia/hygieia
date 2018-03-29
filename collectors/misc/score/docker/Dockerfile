
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY properties-builder.sh /hygieia/

ENV PROP_FILE /hygieia/config/application.properties

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./properties-builder.sh && \
  java -jar score-collector*.jar --spring.config.location=$PROP_FILE

