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
  const [sessionDuration, setSessionDuration] = useState("00:00:00");

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

  useEffect(() => {
    const loginTime = Number(localStorage.getItem("loginTime"));
    if (!loginTime) return;

    const calculateSessionDuration = () => {
      const elapsedSeconds = Math.floor((Date.now() - loginTime) / 1000);
      const hours = String(Math.floor(elapsedSeconds / 3600)).padStart(2, "0");
      const minutes = String(Math.floor((elapsedSeconds % 3600) / 60)).padStart(2, "0");
      const seconds = String(elapsedSeconds % 60).padStart(2, "0");

      setSessionDuration(`${hours}:${minutes}:${seconds}`);
    };
    calculateSessionDuration();

    const interval = setInterval(calculateSessionDuration, 1000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div>
      <Navbar/>
      <div className="page">
        <div className="dashboard-header">
          <div>
            <h1>
              Hi {name} 👋
            </h1>
            <div>
              @{username}
            </div>
          </div> 
          <div className="session-info">
            <div>
              Session
            </div>
            <div className="session-time">            
              {sessionDuration}
            </div>
          </div>
        </div>    
      </div> 
    </div>
  ) 
}
