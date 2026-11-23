# Fase 12: construcción en Maven + Java 17 y ejecución en JRE 17.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 10001 --create-home sillon
COPY --from=build /app/target/sillon-0.0.1-SNAPSHOT.jar /app/app.jar
USER sillon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
