# Restaurant

Small Spring Boot service that scores how suitable a dish is for a time of day.

## API

`POST /dish-suitability`

```bash
curl -X POST localhost:8080/dish-suitability \
  -H 'Content-Type: application/json' \
  -d '{"time":"09:30","dish":"Pancakes"}'
# {"score":10}
```

- `dish`: `Pancakes`, `Chicken`, `IceCream` (case-insensitive)
- `time`: `HH:mm` (24h)
- `score`: integer 0-10

Invalid input returns `400` with a body like
`{"code":"VALIDATION_FAILED","message":"...","errors":[{"field":"time","code":"INVALID_TIME_FORMAT","message":"..."}]}`.
A missing or blank dish returns `DISH_REQUIRED`; an unknown dish returns `DISH_NOT_ALLOWED`. Both list the allowed dishes.
A body that is not JSON returns `MALFORMED_REQUEST`. Other errors (wrong method, content type or path, unexpected server errors) use Spring Boot's default error response.

`GET /health` returns `{"status":"UP"}`.

The OpenAPI spec is generated from the code and served at `/v3/api-docs`, with Swagger UI at http://localhost:8080/swagger-ui.html for trying the API in a browser.

## Build and run

Requires Docker, or JDK 17 for a local run.

```bash
docker-compose up --build        # service on http://localhost:8080
docker-compose down              # stop and remove the container (from another terminal)
./gradlew test                   # unit and integration tests (*IT)
./gradlew bootRun                # run locally without Docker
```

To stop the service, press `Ctrl+C` in the terminal where it runs, or run `docker-compose down` from the project folder. If it was started in the background (`docker-compose up -d`), use `docker-compose down`. `Ctrl+C` stops the container but keeps it, and `down` also removes it.

The image is a two-stage build (JDK to build, JRE to run) and runs as a non-root user. Compose checks `/health` every 10 seconds, so `docker compose ps` shows `healthy`, and `docker compose up --wait` returns once the service is ready.

## Project structure

Packages under `com.ingroup.restaurant`:

- `api` - request, response and error contract (`DishSuitabilityRequest`, `DishSuitabilityResponse`, `ApiError`, `ErrorCode`).
- `controller` - HTTP layer and error mapping (`ApiExceptionHandler`).
- `service` - scoring logic (`DishSuitabilityService`).
- `domain` - the `Dish` enum.
- `config` - typed binding and validation of the score configuration.

## Scoring configuration

Each dish has its own file in `src/main/resources/dishes/` with a score (0-10) for every hour of the day. An excerpt of `pancakes.yml`:

```yaml
restaurant:
  scores:
    Pancakes:
      6: 0
      7: 8
      9: 10
```

An hour or dish that is not listed scores 0. Invalid values (hour outside 0-23, score outside 0-10, unknown dish) fail the startup.

To add a dish:

1. Add a value to the `Dish` enum.
2. Add `src/main/resources/dishes/<dish>.yml` with a score for every hour.
3. Import the file in `application.yml` (`spring.config.import`).

`DishScorePropertiesIT` fails if a dish has no score for any hour.

## CI/CD outline

1. **Pull request:** `./gradlew test` (unit and integration tests), then build the image with `docker compose up --build --wait` and smoke-test it. This step is implemented in `.github/workflows/ci.yml`. A dependency vulnerability scan would be added here.
2. **Merge to main:** build the Docker image, tag it with the commit SHA, push it to a container registry.
3. **Deploy to staging:** roll out the new image, run a smoke test against `/health` and `/dish-suitability`.
4. **Production:** promote the same image after approval, with a rolling update and a health-check gate. Roll back to the previous image tag if it fails.

## Design choices and assumptions

- Layers are kept thin: controller (HTTP and validation), service (scoring), configuration (data).
- Scores are data, not code, so tuning them or adding an hour or a period needs no logic change. Adding a dish means a new `Dish` value and a new file.
- Scores are read at startup, so changing them needs a rebuild and restart. Live reload was left out to keep it simple.
- The hour is what matters: minutes within the same hour score the same.
- Error responses use stable codes, so clients do not depend on message text.
- The API contract is published as an OpenAPI spec rather than a shared Java jar, so clients in any language can read it or generate code from it, and they don't depend on server code.
- Validation of the config at startup makes a bad score fail early rather than return wrong results.
- `gradle.properties` raises the log4j, tomcat and jackson versions above the ones Spring Boot manages, to pick up security fixes. They can be removed when Spring Boot ships those versions.
- Health is a plain endpoint, not Spring Boot Actuator, to match the required `{"status":"UP"}` with no extra dependency. In production I would use Actuator with separate liveness and readiness checks.
