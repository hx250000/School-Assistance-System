# ---------- Stage 1: build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .

COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -m -u 1001 appuser && chown -R appuser /app
USER appuser

COPY --from=build /workspace/target/*.jar ./app.jar

ENV JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
