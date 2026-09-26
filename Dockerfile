# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cache dependencies first (only re-runs when pom/wrapper change).
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B -DskipTests dependency:go-offline

# Build the application.
COPY src ./src
RUN ./mvnw -q -B -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Run as a non-root user (least privilege).
RUN useradd -r -u 1001 -m appuser
COPY --from=build /app/target/iconsi-store-0.0.1-SNAPSHOT.jar app.jar
USER appuser

# Hosts (Railway/Render) inject $PORT; Spring binds to it. Defaults to 8080 locally.
ENV SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
