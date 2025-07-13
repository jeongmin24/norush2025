FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test
RUN cp build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
