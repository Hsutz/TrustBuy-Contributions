# TrustBuy – Trusted Seller Commerce System

## 📖 Project Overview
Contributed to developing the **Trusted Seller Commerce System** for a mini e‑commerce mobile app.  
Built RESTful APIs with **Java Spring Boot** as a backend team member.  
The system supports multi‑seller shopping, structured order management, and moderated product reviews.
I participated in the full SDLC — from initial project planning and design, through development, testing with Postman, and documentation.

---

## 👨‍💻 My Contributions
I implemented three core modules in the backend:

### 🛒 Cart API
- Selective checkout (totals calculated only from chosen items).  
- Multi‑seller cart support (items from different sellers in one cart).  
- Stock validation and quantity checks (prevent out‑of‑stock, enforce limits).  

### 📦 Order API
- Checkout with stock validation and automatic order initialization (`PENDING`).  
- Multi‑seller order splitting with layered views (user, seller, admin).  
- Structured status flow (CONFIRMED → SHIPPED → DELIVERED).  
- Order confirmation notification system.  

### ⭐ Review API
- Single review per product (prevent spam).  
- Structured reviews: rating, comment, optional image.  
- Admin approval workflow before visibility.  
- Edit restrictions (only approved reviews can be updated).  
- Review metrics: average rating and total count per product/business.  

---

## 🛠️ Tech Stack
- Java Spring Boot (backend framework)  
- RESTful APIs (service design)  
- MySQL (database)  
- Postman (API testing)
- IntelliJ IDEA (development IDE)     

---

## 📌 Notes
This README highlights **only my modules (Cart, Order, Review)** from the TrustBuy group project. Other modules were developed by teammates and are not included here.
