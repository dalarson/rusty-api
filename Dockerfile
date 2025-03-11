FROM openjdk:17-jdk-alpine
EXPOSE 8080
WORKDIR /app
ENTRYPOINT [ “java”, “-jar”, “target/rusty-api-0.0.1-SNAPSHOT.jar”]