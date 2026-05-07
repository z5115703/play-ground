export const getHello = async () => {
    const res = await fetch("http://localhost:8080/api/hello");
    return res.text();
};

export async function register(username, password) {
    const res = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    });

    return res.text();
}

export async function login(username, password) {
    const res = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    });

    return res.text();
}