# E-commerce API

This is a Spring Boot RESTful API for managing customers and their orders in an e-commerce system.

##  Features

- Full CRUD operations for Customers and Orders
- One-to-many relationship: each Customer can have multiple Orders
- Pagination for listing resources
- Date-based filtering for orders
- DTOs for clean and secure data exposure
- Global exception handling with meaningful error messages
- Validation using annotations like `@NotBlank`, `@Email`, `@NotNull`

## Architecture

The project follows a layered architecture:

- **Controller**: Handles HTTP requests and responses
- **Service**: Contains business logic
- **Repository**: Interfaces with the database using Spring Data JPA
- **DTOs**: Used to expose only necessary data to clients
- **Entities**: Represent database tables
- **Exception**: Centralized error handling using `@RestControllerAdvice`

## Data Model

- `Customer` entity with fields: `id`, `name`, `email`, `address`, `phone`
- `Order` entity with fields: `orderId`, `orderDate`, `amount`, `status`, and a foreign key to `Customer`
- Relationship: `@OneToMany` in `Customer`, `@ManyToOne` in `Order`

## Endpoints

### Customers
- `GET /customers?page=0&size=5` — List customers with pagination
- `GET /customers/{id}` — Get customer by ID
- `POST /customers` — Create a new customer
- `DELETE /customers/{id}` — Delete a customer

### Orders
- `POST /orders/customer/{id}` — Create an order for a customer
- `GET /orders/customer/{id}?page=0&size=5` — Get orders for a customer
- `PUT /orders/{id}` — Update an order

##  Error Handling

- `CustomerNotFoundException` and `OrderNotFoundException` for missing resources
- Validation errors return structured messages
- Global exception handler using `@RestControllerAdvice`

##  Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- H2 in-memory database
- Maven

## Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/kirangurav9/ecommerce-api.git
