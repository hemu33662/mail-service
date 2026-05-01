# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Distroless Runtime (The most secure option)
# This image contains NO busybox, NO shell, and NO package manager.
FROM gcr.io/distroless/java17-debian12:nonroot

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render uses the $PORT environment variable
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
