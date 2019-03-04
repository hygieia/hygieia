FROM openjdk:8
EXPOSE 8080
VOLUME /var/app/config
ARG JAR_FILE
COPY ${JAR_FILE} /var/app/api.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/var/app/api.jar"]
CMD [ "--spring.config.location=/var/app/config/application.properties" ]