import { useEffect, useState } from "react";
import { useAuth } from "../../context/useAuth";
import { getCurrentUser, updateCurrentUser, changePassword } from "../../services/user";
import Button from "../../components/Button/Button";
import InputField from "../../components/InputField/InputField";
import Navbar from "../../components/Navbar";

export default function AccountSettings() {
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [profileError, setProfileError] = useState("");
  const [profileSuccess, setProfileSuccess] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [passwordSuccess, setPasswordSuccess] = useState("");
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
      setProfileError("");
      setProfileSuccess("Profile updated successfully ✔")
    } catch (error) {
      setProfileSuccess("");
      if (error.response?.status === 409) {
        setProfileError("Username already exists");
      } else {
        setProfileError("Something went wrong");
      }
    }
  }

  const handleChangePassword = async (e) => {
    e.preventDefault();
    try {
      await changePassword(token, { currentPassword, newPassword });
      setPasswordError("");
      setPasswordSuccess("Password updated successfully ✔");
      setCurrentPassword("");
      setNewPassword("");
    } catch (error) {
      setPasswordSuccess("");
      if (error.response?.status === 400) {
        setPasswordError("Current password is incorrect");
      } else {
        setPasswordError("Something went wrong");
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
          <h2>
            Edit Profile
          </h2>
          <form style={{marginBottom: "30px"}} onSubmit={handleSaveChanges}>
            <InputField label="Name" type="text" value={name} onChange={(e) => setName(e.target.value)}/>
            <InputField label="Username" type="text" value={username} onChange={(e) => setUsername(e.target.value)}/>
            {profileError && <div className="error-message">{profileError}</div>}
            {profileSuccess && <div className="success-message">{profileSuccess}</div>}
            <Button className="full-width" label="Save changes" type="submit"/>
          </form>
          <h2>
            Change Password
          </h2>
          <form onSubmit={handleChangePassword}>
            <InputField label="Current Password" type="password" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)}/>
            <InputField label="New Password" type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)}/>
            {passwordError && <div className="error-message">{passwordError}</div>}
            {passwordSuccess && <div className="success-message">{passwordSuccess}</div>}
            <Button className="full-width" label="Change Password" type="submit"/>
          </form>
        </div>
      </div>
    </div>     
  ) 
}

