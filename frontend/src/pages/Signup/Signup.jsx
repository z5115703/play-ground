import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../services/api";
import InputField from "../../components/InputField/InputField";
import Button from "../../components/Button/Button";

export default function Signup() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      setError("");

      if (!name || !username || !password) {
        setError("All fields are required.");
        return;
      }

      const response = await API.post("/register", {
          username,
          password,
      });

      if (response.status === 200) {
        navigate("/login");
      }
    
    } catch (err) {
      if (err.response?.status === 409) {
        console.log(err.response);
        setError("Username already exists");
      } else {
        setError("Register failed");
      }
    }
  }

  return (
    <div className="page">
      <div className="card-container">
        <h1>Sign Up</h1>
        <form onSubmit={handleRegister} className={error ? "error" : ""}>
          <div>
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
            {error && <div className="error-message">{error}</div>}
            <Button label="Sign Up" onClick={handleRegister} type="submit"/>
          </div>        
        </form>
        <button className="text-button" onClick={() => navigate("/login")}>Already have an account?</button>
      </div>
    </div> 
  );
}