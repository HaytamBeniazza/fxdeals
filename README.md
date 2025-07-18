# FX Deals Data Warehouse

A Spring Boot application for managing Foreign Exchange (FX) deals data warehouse, built for Bloomberg to analyze FX deals with proper validation, duplicate prevention, and comprehensive API endpoints.

## 🚀 Features

- **Deal Management**: Submit and retrieve FX deals with comprehensive validation
- **Duplicate Prevention**: Ensures no duplicate deals based on unique identifiers
- **Data Validation**: Comprehensive validation for currency codes, amounts, and timestamps
- **PostgreSQL Database**: Production-ready database with proper indexing
- **RESTful API**: Complete CRUD operations with OpenAPI documentation
- **Docker Support**: Full containerization with Docker Compose
- **Comprehensive Testing**: Unit tests, integration tests with high coverage
- **Error Handling**: Proper exception handling with meaningful error messages
- **Logging**: Structured logging for monitoring and debugging

## 📋 Requirements

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL (for local development)

## 🏗️ Architecture

```
src/
├── main/
│   ├── java/com/progressoft/fxdeals/
│   │   ├── model/
│   │   │   ├── entity/         # JPA entities
│   │   │   └── dto/            # Data Transfer Objects
│   │   ├── repository/         # Data access layer
│   │   ├── service/            # Business logic layer
│   │   ├── controller/         # REST API controllers
│   │   ├── exception/          # Custom exceptions & global handler
│   │   ├── validation/         # Custom validators
│   │   └── config/             # Configuration classes
│   └── resources/
│       ├── application.properties        # Main configuration
│       ├── application-test.properties   # Test configuration
│       └── application-docker.properties # Docker configuration
└── test/                       # Comprehensive test suite
```

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd fxdeals
   ```

2. **Start the application:**
   ```bash
   make setup
   ```
   This command will:
   - Build the Docker image
   - Start PostgreSQL and the application
   - Load sample data
   - Make the application available at http://localhost:8080

3. **Access the application:**
   - API Documentation: http://localhost:8080/fxdeals/swagger-ui.html
   - Health Check: http://localhost:8080/fxdeals/api/deals/health
   - pgAdmin (Database UI): http://localhost:5050

### Manual Setup

1. **Start PostgreSQL:**
   ```bash
   # Using Docker
   docker run --name fxdeals-postgres \
     -e POSTGRES_DB=fxdeals_db \
     -e POSTGRES_USER=fxdeals_user \
     -e POSTGRES_PASSWORD=fxdeals_password \
     -p 5432:5432 -d postgres:15-alpine
   ```

2. **Run the application:**
   ```bash
   make run
   ```

## 📝 API Documentation

### Deal Schema

```json
{
  "dealUniqueId": "string (required, 1-100 chars)",
  "fromCurrency": "string (required, 3-letter ISO code)",
  "toCurrency": "string (required, 3-letter ISO code)", 
  "dealTimestamp": "string (required, format: yyyy-MM-dd HH:mm:ss)",
  "dealAmount": "number (required, positive, max 15 digits + 4 decimals)"
}
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/deals` | Submit a new FX deal |
| GET | `/api/deals` | Get all deals |
| GET | `/api/deals/{id}` | Get deal by unique ID |
| GET | `/api/deals/search/time-range` | Get deals by time range |
| GET | `/api/deals/search/currency-pair` | Get deals by currency pair |
| GET | `/api/deals/recent` | Get recent deals (limited) |
| GET | `/api/deals/stats/count` | Get total deals count |
| GET | `/api/deals/exists/{id}` | Check if deal exists |
| GET | `/api/deals/health` | Health check endpoint |

### Example API Calls

#### Submit a Deal
```bash
curl -X POST http://localhost:8080/fxdeals/api/deals \
  -H "Content-Type: application/json" \
  -d '{
    "dealUniqueId": "DEAL-USD-EUR-001",
    "fromCurrency": "USD",
    "toCurrency": "EUR", 
    "dealTimestamp": "2024-01-15 10:30:00",
    "dealAmount": 50000.00
  }'
```

#### Get All Deals
```bash
curl http://localhost:8080/fxdeals/api/deals
```

#### Search by Currency Pair
```bash
curl "http://localhost:8080/fxdeals/api/deals/search/currency-pair?fromCurrency=USD&toCurrency=EUR"
```

## 🧪 Testing

### Run All Tests
```bash
make test
```

### Test Coverage
The application includes comprehensive test coverage:
- **Repository Tests**: Data access layer testing with H2 database
- **Service Tests**: Business logic testing with Mockito
- **Controller Tests**: API endpoint testing with MockMvc
- **Integration Tests**: Full application context testing

### Load Sample Data
```bash
make sample-data
```

## 🐳 Docker Commands

| Command | Description |
|---------|-------------|
| `make docker-build` | Build Docker image |
| `make docker-up` | Start services with Docker Compose |
| `make docker-down` | Stop Docker Compose services |
| `make docker-logs` | View application logs |
| `make docker-clean` | Clean Docker resources |

## 📊 Database Schema

### Deals Table
```sql
CREATE TABLE deals (
    id BIGSERIAL PRIMARY KEY,
    deal_unique_id VARCHAR(100) NOT NULL UNIQUE,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    deal_timestamp TIMESTAMP NOT NULL,
    deal_amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_deal_unique_id ON deals(deal_unique_id);
CREATE INDEX idx_deal_timestamp ON deals(deal_timestamp);
```

## ✅ Validation Rules

1. **Deal Unique ID**: Required, 1-100 characters, must be unique
2. **From/To Currency**: Required, exactly 3 letters, valid ISO currency codes
3. **Deal Timestamp**: Required, cannot be in the future
4. **Deal Amount**: Required, positive number, max 15 digits + 4 decimals
5. **Currency Pair**: From and To currencies must be different
6. **Duplicate Prevention**: Same unique ID cannot be submitted twice

## 🔧 Configuration

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/fxdeals_db
SPRING_DATASOURCE_USERNAME=fxdeals_user
SPRING_DATASOURCE_PASSWORD=fxdeals_password

# Application
SPRING_PROFILES_ACTIVE=docker
LOGGING_LEVEL_COM_PROGRESSOFT_FXDEALS=INFO
```

### Profiles
- **default**: Local development with PostgreSQL
- **test**: Testing with H2 in-memory database
- **docker**: Production-like environment with containers

## 🚨 Error Handling

The application provides comprehensive error handling:

### Error Response Format
```json
{
  "code": "ERROR_CODE",
  "message": "Human readable error message",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Error Codes
- `DUPLICATE_DEAL`: Deal with the same unique ID already exists
- `VALIDATION_ERROR`: Business logic validation failed
- `FIELD_VALIDATION_ERROR`: Request field validation failed
- `INTERNAL_ERROR`: Unexpected server error

## 📈 Monitoring

### Health Check
```bash
curl http://localhost:8080/fxdeals/api/deals/health
```

### Metrics (via Actuator)
- Application health: `/actuator/health`
- Application info: `/actuator/info`
- Metrics: `/actuator/metrics`

## 🔒 Security Considerations

- Input validation and sanitization
- SQL injection prevention through JPA/Hibernate
- Non-root user in Docker containers
- Environment-specific configurations
- Proper error handling without sensitive information exposure

## 🏃‍♂️ Performance Features

- Database connection pooling (HikariCP)
- Optimized database indexes
- Batch processing for bulk operations
- Proper JPA configuration for performance
- Container resource limits

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [OpenAPI Specification](https://swagger.io/specification/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is created as a technical assessment for ProgressSoft Corporation.

## 📞 Support

For questions or support, please contact the development team.

---

**Built with ❤️ using Spring Boot, PostgreSQL, and Docker** 