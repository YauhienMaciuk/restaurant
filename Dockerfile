FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/restaurant.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
