# Multi-stage Dockerfile for FX Deals Data Warehouse
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r fxdeals && useradd -r -g fxdeals fxdeals

# Copy the JAR file from the build stage
COPY --from=build /app/target/fxdeals-*.jar app.jar

# Change ownership of the app directory to the fxdeals user
RUN chown -R fxdeals:fxdeals /app

# Switch to the non-root user
USER fxdeals

# Expose the port the app runs on
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/fxdeals/api/deals/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 