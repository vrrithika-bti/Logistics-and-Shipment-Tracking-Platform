const USER_KEY = "loggedInUser";

export function loginUser(customerId) {
  localStorage.setItem(
    USER_KEY,
    JSON.stringify({
      role: "USER",
      customerId: customerId,
    })
  );
}

export function loginAdmin() {
  localStorage.setItem(
    USER_KEY,
    JSON.stringify({
      role: "ADMIN",
    })
  );
}

export function getLoggedInUser() {
  const user = localStorage.getItem(USER_KEY);

  if (!user) {
    return null;
  }

  return JSON.parse(user);
}

export function getCustomerId() {
  const user = getLoggedInUser();

  return user?.customerId || null;
}

export function logout() {
  localStorage.removeItem(USER_KEY);
}