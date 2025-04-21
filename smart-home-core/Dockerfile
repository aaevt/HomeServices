# Stage 1: Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/application.jar
EXPOSE 8080
CMD ["java", "-jar", "application.jar"]