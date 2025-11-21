📦 Inventory Management System (Full Stack)


A complete Inventory & Order Management System built with:

React + Material UI • Spring Boot • MySQL • JWT Auth • Excel/PDF Export • Analytics

🚀 Features

🔐 Authentication

JWT secure login

Protected routes

Automatic token management

📦 Product Management

Add / Edit / Delete Products

Auto SKU generation

Stock level monitoring

Search, Sort, Filters

Bulk delete

Export to PDF & Excel

🛒 Order Management

Create, edit, view, delete orders

Auto total calculation

Validations

Status updates

⚠️ Low Stock Monitoring

Critical / Low / Medium indicators

Suggested reorder quantity

Export low stock report

📊 Analytics Dashboard

Daily Sales Chart

Category Distribution Pie Chart

Top Products

Revenue Summary

📁 Project Structure
project-root/
│── backend/          # Spring Boot API + JWT + MySQL
│── frontend/         # React App (Material UI)
│── README.md         # Main documentation

🛠️ Backend (Spring Boot)
Requirements

Java 17

Maven

MySQL

Setup
CREATE DATABASE inventory_db;


Update credentials in:

backend/src/main/resources/application.properties

Run Backend
mvn spring-boot:run


Runs on → http://localhost:8080

Main API Endpoints
/api/products
/api/orders
/api/auth/login
/api/products/export/excel
/api/products/export/pdf


Swagger → http://localhost:8080/swagger-ui.html

🎨 Frontend (React + Material UI)
Setup
cd frontend
npm install
npm start


Runs on → http://localhost:3000

🔑 Default Login Credentials
Email: user@gmail.com
Password: user123

📤 Export Features
Excel Export

4 sheets:

Products

Orders

Low Stock

Analytics Summary

PDF Export

Products table

Orders table

Low stock

Analytics summary

Bar + Pie charts

🧑‍💻 Tech Stack
Frontend

React • Material UI • Axios • React Router

Backend

Spring Boot • Spring Security • JWT
JPA • Hibernate
Apache POI • iText • JFreeChart

Database

MySQL

📝 How to Run the Full Project

1️⃣ Start MySQL
2️⃣ Run backend

mvn spring-boot:run


3️⃣ Run frontend

npm start


4️⃣ Open browser → http://localhost:3000

🤝 Contributing

Pull requests are welcome!

📜 License

MIT License

