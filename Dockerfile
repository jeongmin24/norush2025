FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

RUN cp build/libs/norush2025-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
