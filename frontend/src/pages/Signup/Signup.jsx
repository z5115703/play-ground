import { useState } from "react";
import API from "../../services/api";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";

export default function Signup({ goToLogIn }) {
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      const res = await API.post("/register", {
          username,
          password,
      });

    } catch (err) {
      console.log("Register failed", err);
    }
    goToLogIn();
  }

  return (
    <div>
      <h1>Sign Up</h1>
      <form onSubmit={handleRegister}>
        <div className="login-card">
          <InputField
            type="text"
            placeholder="Enter your name"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
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
          <Button label="Sign Up" onClick={handleRegister} type="submit"/>
        </div>        
      </form>
      <button onClick={goToLogIn}>Log In</button>
    </div>  
  );
}