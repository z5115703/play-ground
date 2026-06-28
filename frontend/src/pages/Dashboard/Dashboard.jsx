import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../services/api";
import Navbar from "../../components/Navbar";

export default function Dashboard() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");

  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem("token");
      try {
        const response = await API.get("/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUsername(response.data);
      } catch (error) {
        if (error.response?.status === 401) {
          navigate("/login");
        }
      }
    };

    fetchUser();
  }, [navigate]);

  return (
    <div>
      <Navbar/>
      <h1>
          Hi {username}!
      </h1>
    </div>
  ) 
}
