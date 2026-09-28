
## Project Overview
Playground is a full-stack web application built using Spring Boot and React (Vite).

This project was started to gain hands-on experience with technologies that were new to me while building something that I can continue to expand over time. The current implementation includes JWT-based authentication, user profile management, and a React frontend that supports these features.

Rather than creating a series of small practice projects, I wanted a single application where I could add new features, try different approaches, and improve the overall design as I learn more about the technologies I'm using.

---

## Features

### Authentication
- User registration
- User login
- JWT-based authentication
- Proteced API endpoints

### User Management
- Retrieve the currently authenticated user
- Update profile information
- Change password
- Username uniqueness validation

### Security
- Password hashing using BCrypt
- JWT generation and validation
- Spring Security authentication filter
- Router protection using Spring Security

### Testing
- Unit testing using JUnit and Mockito
- Controller testing using MockMvc
- Service layer testing
- Authentication flow testing
- Business rule validation

---

## Technology Stack

### Backend
- Java 17
- Spring Boot  
- Spring Security  
- Spring Data JPA 
- JWT Authentication  

### Frontend
- JavaScript
- React    
- Vite
- React Router
- Axios

### Database
- H2

### Testing
- JUnit 5 
- Mockito    
- MockMvc   

### Build Tool
- Maven

---

## How to run

### Frontend
cd frontend
npm install
npm run dev

Frontend runs on:
http://localhost:5173

### Backend
cd backend
mvnw clean install
mvnw spring-boot:run

Bakcned runs on:
http://localhost:8080

---

## API Endpoints

| Method | Endpoint            | Description                     |
| ------ | ------------------- | ------------------------------- |
| POST   | `/auth/register`    | Register a new user             |
| POST   | `/auth/login`       | Authenticate a user             |
| GET    | `/auth/me`          | Retrieve the authenticated user |
| PATCH  | `/auth/me`          | Update user profile             |
| PATCH  | `/auth/me/password` | Change password                 |
