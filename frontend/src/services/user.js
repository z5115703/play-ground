import API from "./api";

export async function getCurrentUser(token) {
  const response = await API.get("/me", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
}

export async function updateCurrentUser(token, user) {
  const response = await API.patch("/me", user, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
}

export async function changePassword(token, password) {
  const response = await API.patch("/me/password", password, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
}