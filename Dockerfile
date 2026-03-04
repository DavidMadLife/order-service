# =========================
# BUILD STAGE
# =========================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy source and build
COPY src src
RUN mvn clean package -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# run as non-root
RUN useradd -m appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

# order-service port (the one your app uses)
EXPOSE 8083

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]