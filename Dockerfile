FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
# The cache mount keeps downloaded Gradle and dependencies between builds
RUN --mount=type=cache,target=/root/.gradle ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
RUN useradd --system --no-create-home app
WORKDIR /app
COPY --from=build /workspace/build/libs/restaurant.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
