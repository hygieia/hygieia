FROM openjdk:8-jre

VOLUME ["/hygieia/logs"]
VOLUME ["/hygieia/config"]

ENV PROP_FILE /hygieia/config/application.properties

WORKDIR /hygieia

COPY target/*.jar /hygieia
COPY docker/properties-builder.sh /hygieia/

CMD ./properties-builder.sh &&\
  java -Djava.security.egd=file:/dev/./urandom -jar *.jar --spring.config.location=$PROP_FILE