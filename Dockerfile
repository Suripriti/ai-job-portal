# Step 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Only backend copy karo
COPY backend ./backend

WORKDIR /app/backend

RUN mvn clean install -DskipTests

# Step 2: Run
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]