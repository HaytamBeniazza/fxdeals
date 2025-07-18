# FX Deals Data Warehouse

A Spring Boot application for managing Foreign Exchange (FX) deals data warehouse. Built to demonstrate clean architecture, proper validation, and duplicate prevention for FX deal submission.

## 🎯 Core Features

- **Deal Submission**: Submit FX deals with comprehensive validation
- **Duplicate Prevention**: Ensures no duplicate deals based on unique identifiers  
- **Input Validation**: Validates currency codes, amounts, and data integrity
- **PostgreSQL Database**: Production-ready persistence layer
- **Clean Architecture**: Layered design with proper separation of concerns
- **Comprehensive Testing**: 23 tests with 72.5% coverage
- **Modern Java**: Uses Lombok for clean code
- **Error Handling**: Proper exception handling with meaningful responses

## 📋 Requirements

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose

## 🏗️ Architecture

```
src/
├── main/java/com/progressoft/fxdeals/
│   ├── model/
│   │   ├── entity/         # JPA entities (Deal)
│   │   └── dto/            # Request/Response DTOs  
│   ├── repository/         # Data access layer
│   ├── service/            # Business logic layer
│   ├── controller/         # REST API controller
│   ├── exception/          # Custom exceptions & global handler
│   └── config/             # Configuration classes
└── test/                   # Comprehensive test suite
```

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone and start:**
   ```bash
   git clone https://github.com/HaytamBeniazza/fxdeals.git
   cd fxdeals
   docker-compose up --build
   ```

2. **Application available at:**
   - API: http://localhost:8080
   - Health Check: http://localhost:8080/api/v1/deals/health

### Manual Setup

1. **Start PostgreSQL:**
   ```bash
   docker run --name fxdeals-postgres \
     -e POSTGRES_DB=fxdeals_db \
     -e POSTGRES_USER=fxdeals_user \
     -e POSTGRES_PASSWORD=fxdeals_password \
     -p 5432:5432 -d postgres:15-alpine
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

## 📝 API Documentation

### Deal Schema

```json
{
  "dealUniqueId": "string (required, unique identifier)",
  "fromCurrency": "string (required, 3-letter ISO code)",
  "toCurrency": "string (required, 3-letter ISO code)", 
  "dealTimestamp": "string (required, ISO 8601 format)",
  "dealAmount": "number (required, positive amount)"
}
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/deals` | Submit a new FX deal |
| GET | `/api/v1/deals/health` | Health check endpoint |

### Example Usage

#### Submit a Deal
```bash
curl -X POST http://localhost:8080/api/v1/deals \
  -H "Content-Type: application/json" \
  -d '{
    "dealUniqueId": "DEAL-USD-EUR-001",
    "fromCurrency": "USD",
    "toCurrency": "EUR", 
    "dealTimestamp": "2024-01-15T10:30:00",
    "dealAmount": 50000.00
  }'
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "dealUniqueId": "DEAL-USD-EUR-001",
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "dealTimestamp": "2024-01-15T10:30:00",
  "dealAmount": 50000.00,
  "createdAt": "2024-01-15T10:30:15"
}
```

#### Health Check
```bash
curl http://localhost:8080/api/v1/deals/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "FX Deals API"
}
```

## 🧪 Testing

### Run Tests with Coverage
```bash
./mvnw clean test jacoco:report
```

### Test Coverage Report
- **Overall Coverage**: 72.5% line coverage
- **Core Logic**: 100% coverage on controller and business logic
- **Test Count**: 23 tests, 0 failures
- **Coverage Report**: `target/site/jacoco/index.html`

### Test Structure
- **Service Tests**: Business logic validation (8 tests)
- **Repository Tests**: Data persistence layer (10 tests)
- **Controller Tests**: API endpoint testing (3 tests)
- **Integration Tests**: End-to-end scenarios (2 tests)

## ⚠️ Error Handling

The API returns appropriate HTTP status codes and error messages:

| Status | Description |
|--------|-------------|
| 201 | Deal successfully created |
| 400 | Invalid request data or validation error |
| 409 | Duplicate deal (deal with same ID already exists) |
| 500 | Internal server error |

**Error Response Format:**
```json
{
  "error": "DUPLICATE_DEAL",
  "message": "Deal with ID 'DEAL-001' already exists",
  "timestamp": "2024-01-15T10:30:00"
}
```

## 🔧 Development

### Build
```bash
./mvnw clean package
```

### Run Tests
```bash
./mvnw test
```

### Docker Build
```bash
docker build -t fxdeals .
```

## 🏆 Technical Highlights

- **Clean Architecture**: Proper separation of concerns
- **Modern Java**: Lombok for reduced boilerplate
- **Comprehensive Validation**: Currency codes, amounts, duplicates
- **Test-Driven**: High test coverage with meaningful assertions
- **Production Ready**: Docker support, proper error handling
- **Best Practices**: Structured logging, transaction management

## 📊 Performance

- **Duplicate Detection**: O(1) lookup using database constraints
- **Data Validation**: Comprehensive input validation before persistence
- **Transaction Management**: Proper ACID compliance
- **Connection Pooling**: HikariCP for optimal database performance

---

**Built with Spring Boot 3.5.3, PostgreSQL, and modern Java practices.** 