# ============================================================
# Stage 1: Build
# ============================================================
FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy Maven configuration first for Docker layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests


# ============================================================
# Stage 2: Run
# ============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Render provides PORT automatically.
# 10000 is Render's default port.
ENV PORT=10000

# Spring Boot will use PORT through server.port configuration
COPY --from=builder /app/target/personal-finance-manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]