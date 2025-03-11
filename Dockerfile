FROM openjdk:17-jdk-alpine
EXPOSE 8080
WORKDIR /app
COPY . .
RUN chmod a+x ./mvnw
RUN ./mvnw package -DskipTests
ENTRYPOINT [ “java”, “-jar”, “target/rusty-api-0.0.1-SNAPSHOT.jar”]