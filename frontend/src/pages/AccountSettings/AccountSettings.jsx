import { useEffect, useState } from "react";
import { useAuth } from "../../context/useAuth";
import { getCurrentUser, updateCurrentUser } from "../../services/user";
import Button from "../../components/Button/Button";
import InputField from "../../components/InputField/InputField";
import Navbar from "../../components/Navbar";

export default function AccountSettings() {
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
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
        }
      }   
    };
    fetchUser();
  }, [token, logout]);

  const handleSaveChanges = async (e) => {
    e.preventDefault();
    try {
      const user = await updateCurrentUser(token, { name, username });
      setName(user.name);
      setUsername(user.username);
      setSuccess("Profile updated successfully ✔")
    } catch (error) {
      if (error.response?.status === 409) {
        setError("Username already exists");
      } else {
        setError("Something went wrong");
      }
    }
  }

  return (
    <div>
      <Navbar/> 
      <div className="page">
        <h1>
          Account Settings
        </h1>
        <div className="settings-container">
          <form onSubmit={handleSaveChanges}>
          <InputField label="Name" type="text" value={name} onChange={(e) => setName(e.target.value)}/>
          <InputField label="Username" type="text" value={username} onChange={(e) => setUsername(e.target.value)}/>
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}
          <Button className="full-width" label="Save changes" type="submit"/>
        </form>
        </div>
      </div>
    </div>     
  ) 
}

