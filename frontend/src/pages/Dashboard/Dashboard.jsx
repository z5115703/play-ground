import { useEffect, useState } from "react";
import API from "../../services/api";

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
    <h1>
        Hi {username}!
    </h1>
  ) 
}
