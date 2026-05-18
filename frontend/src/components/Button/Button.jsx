import "./Button.css";

function Button({ label, onClick, type = "button" }) {
  return (
    <button
      className="button"
      type={type}
      onClick={onClick}
    >
      {label}
    </button>
  );
}

export default Button;