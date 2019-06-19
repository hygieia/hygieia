FROM maven:3.6.0-jdk-8-alpine as BUILD
COPY . .
RUN mvn package -Dpmd.skip=true

FROM openjdk:8-jre

RUN mkdir -p /hygieia/config
RUN touch /hygieia/config/application.properties
RUN chmod 777 /hygieia/config/application.properties

ENV PROP_FILE /hygieia/config/application.properties

WORKDIR /hygieia

COPY --from=BUILD target/*.jar /hygieia
COPY docker/properties-builder.sh /hygieia/
COPY docker/certificates/ /hygieia/

RUN openssl x509 -in HHS-FPKI-Intermediate-CA-E1.pem -inform pem -out ca.der -outform der
RUN keytool -importcert -noprompt -alias cpi.cmscloud.local-CA -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -file ca.der 
#RUN keytool -import -trustcacerts -noprompt -alias cpi.cmscloud.local-CA -file cpi.cmscloud.local-CA.der -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit 

VOLUME ["/hygieia/logs"]

EXPOSE 8080

CMD ./properties-builder.sh &&\
  java -Djava.security.egd=file:/dev/./urandom -jar *.jar --spring.config.location=$PROP_FILE