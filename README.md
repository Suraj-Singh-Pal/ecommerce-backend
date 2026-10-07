# E-Commerce Backend

A production-style E-Commerce Backend REST API built using **Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, and MySQL**.

The project provides secure APIs for authentication, products, categories, cart, orders, wishlist, pagination, sorting, filtering, validation, and role-based authorization.

---

## 🚀 Features

### Authentication & Authorization

* User registration and login
* JWT-based authentication
* USER and ADMIN roles
* Role-based endpoint authorization
* Password encryption using BCrypt
* Protected APIs using Spring Security

### Product Management

* Create product
* Get all products
* Get product by ID
* Update product
* Delete product
* Search products
* Pagination
* Sorting
* Price filtering
* Category filtering
* Stock filtering
* Product validation

### Category Management

* Create category
* Get all categories
* Get category by ID
* Update category
* Delete category
* ADMIN-only modification operations

### Cart Management

* Add product to cart
* View cart
* Update product quantity
* Remove product from cart
* Automatic cart total calculation
* Cart validation

### Order Management

* Create order from cart
* View user's orders
* View order by ID
* ADMIN view of all orders
* Filter orders by status
* Order cancellation
* Order status management
* Stock reduction after successful checkout
* Stock restoration after cancellation

### Checkout Price Protection

The system verifies product prices during checkout.

If a product price changes after it was added to the cart:

* Checkout is rejected
* Current price is returned
* User can review the updated price
* Checkout can be retried with confirmed prices

### Wishlist

* Add product to wishlist
* View wishlist
* Remove product from wishlist
* Duplicate wishlist protection

### Validation & Exception Handling

* Request validation
* Global exception handling
* Proper HTTP status codes
* Structured error responses
* Resource-not-found handling
* Access-denied handling

---

## 🛠️ Tech Stack

| Technology        | Usage                          |
| ----------------- | ------------------------------ |
| Java 25           | Programming Language           |
| Spring Boot 4.1.1 | Backend Framework              |
| Spring Security   | Authentication & Authorization |
| JWT               | Token-based Authentication     |
| Spring Data JPA   | Database Access                |
| Hibernate         | ORM                            |
| MySQL             | Database                       |
| Maven             | Build Tool                     |
| Swagger / OpenAPI | API Documentation & Testing    |
| BCrypt            | Password Encryption            |

---

## 📁 Project Structure

```text
src
└── main
    ├── java
    │   └── com.suraj.ecommerce
    │       ├── config
    │       ├── controller
    │       ├── dto
    │       ├── entity
    │       ├── exception
    │       ├── repository
    │       ├── security
    │       └── service
    │
    └── resources
        └── application.properties
```

---

## 🔐 Roles

### USER

A USER can:

* Register/login
* View products
* Search products
* Filter products
* Manage cart
* Create orders
* View own orders
* Cancel eligible orders
* Manage wishlist

### ADMIN

An ADMIN can additionally:

* Create products
* Update products
* Delete products
* Create categories
* Update categories
* Delete categories
* View all orders
* Filter orders by status
* Update order status

---

## 🔑 Authentication Flow

```text
User
  ↓
Register / Login
  ↓
JWT Token
  ↓
Authorization Header
  ↓
Spring Security
  ↓
JWT Filter
  ↓
Role Verification
  ↓
Protected API
```

For protected APIs, use:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

## 🌐 Main API Endpoints

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/create-admin
```

### Products

```text
GET    /api/products
GET    /api/products/{id}
GET    /api/products/search
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Categories

```text
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

### Cart

```text
POST   /api/cart/items
GET    /api/cart
PUT    /api/cart/items/{productId}
DELETE /api/cart/items/{productId}
```

### Orders

```text
POST  /api/orders
GET   /api/orders
GET   /api/orders/{orderId}
PATCH /api/orders/{orderId}/cancel
```

### Admin Orders

```text
GET   /api/orders/admin
GET   /api/orders/admin/status
PATCH /api/orders/{orderId}/status
```

### Wishlist

```text
POST   /api/wishlist/{productId}
GET    /api/wishlist
DELETE /api/wishlist/{productId}
```

---

## 🗄️ Database

Database:

```text
ecommerce_db
```

MySQL connection:

```text
jdbc:mysql://localhost:3306/ecommerce_db
```

Database credentials should be configured through environment variables instead of committing passwords to GitHub.

---

## ⚙️ Environment Variables

Example:

```text
DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
DB_USERNAME=root
DB_PASSWORD=your_password

JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000
```

**Never commit real database passwords or JWT secrets to GitHub.**

---

## ▶️ How to Run

### 1. Clone the repository

```bash
git clone <your-github-repository-url>
```

### 2. Open the project

```text
ecommerce-backend
```

### 3. Configure MySQL

Create the database:

```sql
CREATE DATABASE ecommerce_db;
```

### 4. Configure environment variables

Set:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
```

### 5. Run the application

From the Maven project directory:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

## 📖 Swagger API Documentation

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger can be used to:

* View all APIs
* Test APIs
* Authenticate using JWT
* Test USER/ADMIN authorization
* Test request validation
* Test responses

---

## 🧪 Testing

The major modules were manually tested through Swagger.

Verified functionality includes:

* Authentication
* JWT authorization
* Product CRUD
* Product search
* Pagination
* Sorting
* Price filtering
* Category filtering
* Stock filtering
* Category CRUD
* Cart operations
* Checkout
* Price-change protection
* Order lifecycle
* Order cancellation
* Stock restoration
* Wishlist operations
* Validation
* Exception handling
* USER/ADMIN authorization

Security tests also confirmed that USER accounts cannot access ADMIN-only product/category operations.

---

## 🔄 Order Lifecycle

```text
PENDING
   ↓
CONFIRMED
   ↓
SHIPPED
   ↓
DELIVERED
```

A pending order can also be cancelled:

```text
PENDING
   ↓
CANCELLED
```

Invalid status transitions are rejected by the backend.

---

## 🛡️ Security

The application uses:

* Spring Security
* JWT authentication
* BCrypt password hashing
* Role-based authorization
* Protected endpoints
* Custom authentication handling
* Custom access-denied handling

Example:

```text
USER → POST /api/products → 403 Forbidden

USER → POST /api/categories → 403 Forbidden
```

This confirms ADMIN-only authorization is enforced.

---

## 📌 Project Status

**Status: Completed ✅**

The core E-Commerce Backend functionality has been implemented and tested successfully.

---

## 🚀 Future Improvements

Possible future enhancements:

* Payment Gateway integration
* Order email notifications
* Product image upload
* Refresh token mechanism
* Redis caching
* Docker support
* CI/CD pipeline
* Automated unit tests
* Automated integration tests
* Cloud deployment
* Production monitoring and logging

---

## 👨‍💻 Author

**Suraj Singh**

Java | Spring Boot | Backend Development
