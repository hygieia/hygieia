FROM docker.io/java:openjdk-8-jdk

RUN \
  mkdir /hygieia

COPY *.jar /hygieia/
COPY collector-servers-config-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

EXPOSE 8888

CMD ./collector-config-server-properties-builder.sh && \ 
	java -jar collector-config-server.jar --spring.config.location=/hygieia/hygieia-collector-config-server.properties