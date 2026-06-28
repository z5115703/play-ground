import { logout } from "../services/auth";
import { useNavigate } from "react-router-dom";
import Button from "./Button/Button";

function Navbar() {
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar">
      <div>Play Ground</div>
      <Button label="Logout" onClick={handleLogout} />
    </nav>
  );
}

export default Navbar;
