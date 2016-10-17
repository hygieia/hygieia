
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY application.properties /hygieia/
WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD java -jar gitlab-git-collector*.jar --spring.config.location=/hygieia/application.properties

