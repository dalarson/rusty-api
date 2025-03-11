# Stage 1: Build application
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /home/app
COPY . /home/app
RUN mvn -f /home/app/pom.xl clean package -DskipTests

# Stage 2: Run application
FROM openjdj:17-jdk-slim
COPY --from=buid /home/app/target/*.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]
