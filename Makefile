# FX Deals Data Warehouse Makefile
# This Makefile provides convenient commands for building, testing, and running the application

.PHONY: help clean compile test package run docker-build docker-up docker-down docker-logs setup-sample-data

# Default target
help:
	@echo "FX Deals Data Warehouse - Available Commands:"
	@echo ""
	@echo "Development Commands:"
	@echo "  make clean          - Clean build artifacts"
	@echo "  make compile        - Compile the application"
	@echo "  make test           - Run all tests"
	@echo "  make package        - Package the application"
	@echo "  make run            - Run the application locally"
	@echo ""
	@echo "Docker Commands:"
	@echo "  make docker-build   - Build Docker image"
	@echo "  make docker-up      - Start application with Docker Compose"
	@echo "  make docker-down    - Stop Docker Compose services"
	@echo "  make docker-logs    - View application logs"
	@echo "  make docker-clean   - Remove all Docker containers and volumes"
	@echo ""
	@echo "Data Commands:"
	@echo "  make sample-data    - Load sample data into the application"
	@echo "  make test-api       - Test API endpoints with sample data"
	@echo ""
	@echo "Utility Commands:"
	@echo "  make check-deps     - Check if required tools are installed"
	@echo "  make status         - Show application and database status"

# Check if required tools are installed
check-deps:
	@echo "Checking required dependencies..."
	@command -v java >/dev/null 2>&1 || { echo "Java is required but not installed. Aborting." >&2; exit 1; }
	@command -v mvn >/dev/null 2>&1 || { echo "Maven is required but not installed. Aborting." >&2; exit 1; }
	@command -v docker >/dev/null 2>&1 || { echo "Docker is required but not installed. Aborting." >&2; exit 1; }
	@command -v docker-compose >/dev/null 2>&1 || { echo "Docker Compose is required but not installed. Aborting." >&2; exit 1; }
	@echo "All dependencies are installed!"

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	./mvnw clean
	@echo "Clean completed!"

# Compile the application
compile: check-deps
	@echo "Compiling the application..."
	./mvnw compile
	@echo "Compilation completed!"

# Run tests
test: check-deps
	@echo "Running tests..."
	./mvnw test
	@echo "Tests completed!"

# Package the application
package: check-deps
	@echo "Packaging the application..."
	./mvnw clean package -DskipTests
	@echo "Packaging completed!"

# Run the application locally (requires PostgreSQL to be running)
run: package
	@echo "Starting the application locally..."
	@echo "Note: Make sure PostgreSQL is running on localhost:5432"
	java -jar target/fxdeals-*.jar

# Build Docker image
docker-build: check-deps
	@echo "Building Docker image..."
	docker build -t fxdeals-app .
	@echo "Docker image built successfully!"

# Start application with Docker Compose
docker-up: check-deps
	@echo "Starting application with Docker Compose..."
	docker-compose up -d
	@echo "Application started! Visit:"
	@echo "  - API: http://localhost:8080/fxdeals/swagger-ui.html"
	@echo "  - Health: http://localhost:8080/fxdeals/api/deals/health"
	@echo "  - pgAdmin: http://localhost:5050 (admin@fxdeals.com / admin123)"

# Stop Docker Compose services
docker-down:
	@echo "Stopping Docker Compose services..."
	docker-compose down
	@echo "Services stopped!"

# View application logs
docker-logs:
	@echo "Viewing application logs..."
	docker-compose logs -f fxdeals-app

# Clean Docker resources
docker-clean:
	@echo "Cleaning Docker resources..."
	docker-compose down -v
	docker system prune -f
	@echo "Docker cleanup completed!"

# Load sample data
sample-data:
	@echo "Loading sample data..."
	@echo "Make sure the application is running first!"
	@sleep 2
	@for deal in $$(cat scripts/sample-deals.json | jq -c '.[]'); do \
		echo "Loading deal: $$(echo $$deal | jq -r '.dealUniqueId')"; \
		curl -X POST http://localhost:8080/fxdeals/api/deals \
			-H "Content-Type: application/json" \
			-d "$$deal" \
			--silent --output /dev/null --show-error || echo "Failed to load deal"; \
		sleep 1; \
	done
	@echo "Sample data loading completed!"

# Test API endpoints
test-api:
	@echo "Testing API endpoints..."
	@echo "1. Health check:"
	@curl -s http://localhost:8080/fxdeals/api/deals/health | jq '.'
	@echo ""
	@echo "2. Get all deals:"
	@curl -s http://localhost:8080/fxdeals/api/deals | jq '.'
	@echo ""
	@echo "3. Get deals count:"
	@curl -s http://localhost:8080/fxdeals/api/deals/stats/count | jq '.'
	@echo "API testing completed!"

# Show application status
status:
	@echo "Application Status:"
	@echo "==================="
	@echo "Docker containers:"
	@docker-compose ps 2>/dev/null || echo "Docker Compose not running"
	@echo ""
	@echo "Application health:"
	@curl -s http://localhost:8080/fxdeals/api/deals/health 2>/dev/null | jq '.' || echo "Application not responding"

# Quick setup - build and start everything
setup: docker-build docker-up
	@echo "Waiting for application to start..."
	@sleep 30
	@make sample-data
	@echo ""
	@echo "Setup completed! The FX Deals application is ready to use."
	@echo "Visit http://localhost:8080/fxdeals/swagger-ui.html to explore the API."

# Development workflow - clean, test, package
dev: clean test package
	@echo "Development build completed successfully!" 