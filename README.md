# My play ground project to play

## Project Overview
This is a full-stack web application built using  using React (Vite) for the frontend and Spring Boot for the backend. 

The goal of this project is to learn and implement a complete authentication system, including user registration, login, and secure API communication.

---

## Tech Stack
- Frontend: React (Vite, JavaScript)
- Backend: Spring Boot (Java)
- Node.js for frontend tooling 

---

## Setup Notes

### Node.js
Installed Node.js v24.15.0 on Windows

### Frontend setup
Created with:
npm create vite@latest frontend
Variant: JavaScript + React

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

## API Endpoints

### Test Endpoint
GET/api/hello

Response:
"Backend is working!"