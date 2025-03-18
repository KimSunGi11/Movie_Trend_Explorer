FROM gradle:7.6.1-jdk17 AS builder

WORKDIR /app
COPY . .
RUN gradle wrapper
RUN ./gradlew clean build -x test

FROM openjdk:17-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 