# 📦 **Inventory Management System (Full Stack)**

A complete **Inventory & Order Management System** built with:

React + Material UI • Spring Boot • MySQL • JWT Auth • Excel/PDF Export • Analytics

---

## **🚀 Features**

### **🔐 Authentication**
- JWT secure login  
- Protected routes  
- Automatic token management  

### **📦 Product Management**
- Add / Edit / Delete Products  
- Auto SKU generation  
- Stock level monitoring  
- Search, Sort, Filters  
- Bulk delete  
- Export to **Excel** & **PDF**  

### **🛒 Order Management**
- Create, Edit, View, Delete orders  
- Auto total calculation  
- Quantity validations  
- Order status updates  

### **⚠️ Low Stock Monitoring**
- Real-time alerts  
- Severity levels (Critical / Low / Medium)  
- Suggested reorder quantity  
- Export low-stock report  

### **📊 Analytics Dashboard**
- Daily Sales Line Chart  
- Category Distribution Pie Chart  
- Top Products  
- Revenue Summary  
- Low Stock Summary  

---

## **📁 Project Structure**

project-root/
│── backend/ # Spring Boot API + JWT + Database
│── frontend/ # React + Material UI UI
│── README.md # Main documentation

yaml
Copy code

---

## **🛠️ Backend (Spring Boot)**

### **Requirements**
- Java 17  
- Maven  
- MySQL  

### **Database Setup**
```sql
CREATE DATABASE inventory_db;
Update credentials in:

css
Copy code
backend/src/main/resources/application.properties
Run Backend
arduino
Copy code
mvn spring-boot:run
Runs on → http://localhost:8080

API Endpoints
swift
Copy code
/api/products
/api/orders
/api/auth/login
/api/products/export/excel
/api/products/export/pdf
Swagger → http://localhost:8080/swagger-ui.html

🎨 Frontend (React + Material UI)
Run Frontend
powershell
Copy code
cd frontend
npm install
npm start
Runs on → http://localhost:3000

🔑 Default Login Credentials
makefile
Copy code
Email: user@gmail.com
Password: user123
📤 Export Features
📄 PDF Export
Products Table

Orders Table

Low Stock Items

Analytics Summary

Charts (Bar + Pie)

📊 Excel Export
Sheets:

Products

Orders

Low Stock

Analytics Summary

🧑‍💻 Tech Stack
Frontend
React • Material UI • Axios • React Router

Backend
Spring Boot • Spring Security • JWT
JPA • Hibernate
Apache POI • iText / JFreeChart

Database
MySQL

📝 How to Run the Project
1️⃣ Start MySQL
2️⃣ Run Backend

arduino
Copy code
mvn spring-boot:run
3️⃣ Run Frontend

powershell
Copy code
npm start
4️⃣ Open → http://localhost:3000

🤝 Contributing
Pull requests are welcome!

📜 License
MIT License

