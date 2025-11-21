# Backend - Inventory Management (Spring Boot)

## Requirements
- JDK 17
- Maven
- MySQL running with a database named `inventory_db`
- Update `src/main/resources/application.properties` with your MySQL credentials (replace `your_mysql_password`)

## Run
1. Create DB: `CREATE DATABASE inventory_db;`
2. Update application.properties with username/password.
3. From backend folder run:
   `mvn spring-boot:run`
4. APIs available at http://localhost:8080/api/products and /api/orders

Open API docs (Swagger UI) at: http://localhost:8080/swagger-ui.html
