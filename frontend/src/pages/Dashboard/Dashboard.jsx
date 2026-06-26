import { useEffect, useState } from "react";
import API from "../../services/api";
import Navbar from "../../components/Navbar";

export default function Dashboard() {
  const [username, setUsername] = useState("");

  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem("token");

      const response = await API.get("/me", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 200) {
        setUsername(response.data);
      }
    };

    fetchUser();
  }, []);

  return (
    <div>
      <Navbar></Navbar>
      <h1>
          Hi {username}!
      </h1>
    </div>
  ) 
}
