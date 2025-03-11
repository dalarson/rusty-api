FROM openjdk:17-jdk-slim
RUN ls /home/runner/work/rusty-api/rusty-api/target/
ADD target/rusty-api-0.0.1-SNAPSHOT.jar /app/rusty-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rusty-api.jar"]
