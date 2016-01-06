
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

#GITHUB_CRON default: "0 0/5 * * * *"
ENV GITHUB_CRON=

#GITHUB_HOST default:github.com
ENV GITHUB_HOST=

#Maximum number of days to go back in time when fetching commits, default: 15
ENV GITHUB_COMMIT_THREASHOLD_DAYS=


RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY github-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./github-properties-builder.sh && java -jar github-scm-collector*.jar --spring.config.location=/hygieia/hygieia-github-scm-collector.properties
