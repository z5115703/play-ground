import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/auth",
});

export default API;

export const getHello = async () => {
  const response = await fetch("http://localhost:8080/api/hello");
  return response.text();
};

export async function register(username, password) {
  const response = await fetch("http://localhost:8080/auth/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  });

  return response;
}

export async function login(username, password) {
  const response = await fetch("http://localhost:8080/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  });

  return response;
}
