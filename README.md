# Spring Boot E-Commerce Application

## Description
A comprehensive e-commerce application built with Spring Boot, featuring category management and RESTful APIs.

## Technologies Used
- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (In-memory database for development)
- **Lombok**
- **Maven**

## Features
- Category Management (CRUD operations)
- RESTful API endpoints
- H2 Console for database management
- Global exception handling
- DTO pattern implementation

## Prerequisites
- Java 21 or higher
- Maven 3.6+

## Getting Started

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

Or run the JAR file:
```bash
java -jar target/sb-ecom-0.0.1-SNAPSHOT.jar
```

### Access the Application
- **Application URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:ecommerce`
  - Username: `sa`
  - Password: (leave empty)

## API Endpoints

### Category Management
- **GET** `/api/categories` - Get all categories
- **GET** `/api/categories/{id}` - Get category by ID
- **POST** `/api/categories` - Create a new category
- **PUT** `/api/categories/{id}` - Update category
- **DELETE** `/api/categories/{id}` - Delete category

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/ecommerce/project/
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST controllers
│   │       ├── exceptions/     # Exception handling
│   │       ├── model/          # Entity classes
│   │       ├── payload/        # DTOs
│   │       ├── repository/     # Data access layer
│   │       └── service/        # Business logic
│   └── resources/
│       └── application.properties
└── test/                       # Test classes
```

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is open source and available under the MIT License.

