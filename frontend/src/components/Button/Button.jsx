import "./Button.css";

function Button({ label, onClick, type = "button", className = ""}) {
  return (
    <button
      className={`button ${className}`}
      type={type}
      onClick={onClick}
    >
      {label}
    </button>
  );
}

export default Button;