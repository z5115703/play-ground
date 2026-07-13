import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../services/api";
import { useAuth } from "../../context/useAuth";
import Navbar from "../../components/Navbar";

export default function Dashboard() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const { token, logout } = useAuth();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await API.get("/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setName(response.data.name);
        setUsername(response.data.username);
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
      <h1>
          Hi {name}!
      </h1>
      <div>
        {username}
      </div>
    </div>
  ) 
}
