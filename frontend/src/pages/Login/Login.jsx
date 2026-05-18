import { useState } from "react";
import API from "../../services/api";
import Button from "../../components/Button/Button";
import InputField from "../../components/InputField/InputField";
import "./Login.css";

export default function Login({ goToSignUp }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
        const response = await API.post("/login", {
            username,
            password,
        });

        console.log("Token: ", response.data);
        localStorage.setItem("token", response.data);

    } catch (err) {
        console.log("Login failed", err);
    }
  };

  return (
    <div>
      <h1>Log In</h1>
      <form onSubmit={handleLogin}>
        <div className="login-card">
          <InputField
            type="text"
            placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <InputField
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Button label="Log In" onClick={handleLogin} type="submit"/>
        </div>        
      </form>
      <button onClick={goToSignUp}>Sign Up</button>
    </div>
  );
}