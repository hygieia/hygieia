FROM openjdk:8
EXPOSE 8080
VOLUME /var/app/config
ARG JAR_FILE
COPY ${JAR_FILE} /var/app/apiaudit.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/var/app/apiaudit.jar"]
CMD [ "--spring.config.location=/var/app/config/application.properties" ]