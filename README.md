📦 Inventory Management System (Full Stack)

A complete Inventory & Order Management System built with:

React + Material UI (Frontend)

Spring Boot (Java) (Backend)

MySQL (Database)

JWT Authentication

Excel + PDF Export

Analytics Dashboard

Low Stock Alerts

This project includes authentication, CRUD operations, real-time analytics, and downloadable reports.

🚀 Features
🔐 Authentication

JWT Secure Login

Protected API Routes

Auto Token Handling

📦 Product Management

Add / Edit / Delete Products

Auto SKU Generation

Live Stock Tracking

Reorder Level Alerts

Search + Sort + Filters

Bulk Delete

Export Products to Excel & PDF

🛒 Order Management

Create / Edit / View / Delete Orders

Quantity Validation

Auto Calculation of Total Amount

Order History

Order Status Handling

⚠️ Low Stock Monitoring

Real-time Stock Alerts

Severity Levels: Critical / Warning / Safe

Auto Suggested Reorder Quantity

Export Low Stock Report

📊 Analytics Dashboard

Total Products, Orders, Revenue

Daily Sales Line Chart

Category Distribution Pie Chart

Top Products Analytics

Low Stock Summary

📁 Project Structure
project-root/
│── backend/          # Spring Boot API + Authentication + Export
│── frontend/         # React + Material UI frontend
│── README.md         # Main documentation (this file)

🛠️ Backend (Spring Boot)
Requirements

Java 17

Maven

MySQL

Database Setup
CREATE DATABASE inventory_db;

Configure MySQL Credentials

Edit:

backend/src/main/resources/application.properties

Run Backend
mvn spring-boot:run


Backend runs on:

👉 http://localhost:8080

Main API Endpoints
/api/products
/api/orders
/api/auth/login
/api/products/export/excel
/api/products/export/pdf

Swagger Docs
http://localhost:8080/swagger-ui.html

🎨 Frontend (React + Material UI)
Requirements

Node.js

npm

Run Frontend
cd frontend
npm install
npm start


Frontend runs on:

👉 http://localhost:3000

🔑 Default Login Credentials
Email: user@gmail.com
Password: user123

🧪 API Testing (Postman)

Test the following:

Login (get JWT token)

Products CRUD

Orders CRUD

Export APIs

Analytics API

📤 Export Features
📄 PDF Export

Includes:

Products Table

Orders Table

Low Stock List

Analytics Summary

Charts (Pie + Bar)

📊 Excel Export

Contains 4 sheets:

Products

Orders

Low Stock

Analytics Summary

🧑‍💻 Tech Stack
Frontend

React

Material UI

Axios

React Router

Backend

Spring Boot

Spring Security + JWT

Hibernate + JPA

Apache POI (Excel)

iText / JFreeChart (PDF)

Database

MySQL

📝 How to Run the Full Project
1️⃣ Start MySQL
2️⃣ Run Backend
mvn spring-boot:run

3️⃣ Run Frontend
npm start

4️⃣ Open in Browser

👉 http://localhost:3000

🤝 Contributing

Pull requests are welcome!

📜 License

This project is licensed under the MIT License.

