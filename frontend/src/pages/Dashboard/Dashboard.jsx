import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import Navbar from "../../components/Navbar";
import { getCurrentUser } from "../../services/user";

export default function Dashboard() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const { token, logout } = useAuth();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const user = await getCurrentUser(token);
        setName(user.name);
        setUsername(user.username);
      } catch (error) {
        if (error.response?.status === 401) {
          logout();
          navigate("/login");
        }
      }   
    };
    fetchUser();
  }, [navigate, token, logout]);

  return (
    <div>
      <Navbar/>
      <div className="page">
        <h1>
          Hi {name} 👋
        </h1>
        <div>
          @{username}
        </div>
      </div> 
    </div>
  ) 
}
