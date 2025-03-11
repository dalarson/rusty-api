FROM openjdk:17-jdk-slim
WORKDIR /app
RUN find . -name '*.jar'
ADD target/rusty-api-0.0.1-SNAPSHOT.jar /app/rusty-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rusty-api.jar"]