import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../services/api";
import Button from "../../components/Button/Button";
import InputField from "../../components/InputField/InputField";
import "../../styles/style.css";
import { useAuth } from "../../context/useAuth"

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      setError("");

      if (!username || !password) {
        setError("All fields are required.");
        return;
      }

      const response = await API.post("/login", {
          username,
          password,
      });

      login(response.data.token);
      navigate("/dashboard");

    } catch (err) {
      if (err.response?.status === 401) {
        console.log(err.response);
        setError("Invalid password");
      } else if (err.response?.status === 404) {
        setError("User not found")
      } else {
        setError("Something went wrong.");
      }
    }
  };

  return (
    <div className="page">
      <div className="card-container"> 
        <h1>Log In</h1>    
        <form onSubmit={handleLogin} className={error ? "error" : ""}>
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
          <Button label="Log In" type="submit"/>
        </form>    
        <button className="text-button" type="button" onClick={() => navigate("/signup")}>Create an account?</button>
      </div>
    </div>    
  );
}