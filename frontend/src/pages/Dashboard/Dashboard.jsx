import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../services/api";
import Navbar from "../../components/Navbar";
import Button from "../../components/Button/Button";

export default function Dashboard() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
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
        setName(response.data.name);
        setUsername(response.data.username);
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
          Hi {name}!
      </h1>
      <div>
        {username}
      </div>
    </div>
  ) 
}
