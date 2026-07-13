import { useAuth } from "../context/useAuth";
import { NavLink, useNavigate } from "react-router-dom";
import Button from "./Button/Button";

function Navbar() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar">
      <div>Play Ground</div>
      <ul className="navbar-links">
        <li>
          <NavLink to="/dashboard">Dashboard</NavLink>
        </li>
        <li>
          <NavLink to="/account">Account Settings</NavLink>
        </li>       
      </ul>     
      <Button label="Logout" onClick={handleLogout} />
    </nav>
  );
}

export default Navbar;
