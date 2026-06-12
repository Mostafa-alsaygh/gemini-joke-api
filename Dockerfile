# Build stage
FROM gradle:8.11.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-alpine
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/ktor-sample-all.jar /app/ktor-sample-all.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ktor-sample-all.jar"]
