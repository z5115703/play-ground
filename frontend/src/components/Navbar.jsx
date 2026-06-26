import { logout } from "../services/api";
import { useNavigate } from "react-router-dom";
import Button from "./Button/Button";

function Navbar() {
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return <Button label="Logout" onClick={handleLogout}/>;
}

export default Navbar;