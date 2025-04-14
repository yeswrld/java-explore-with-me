FROM openjdk:21-jdk-slim
COPY target/main-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]