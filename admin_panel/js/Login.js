// Login.js

// POST helper for login
export async function postData(endpoint, data) {
  const response = await fetch(`http://localhost:8080/${endpoint}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Request failed");
  }

  return response.json();
}

// Elements
const username = document.getElementById("username");
const password = document.getElementById("password");
const loginBtn = document.querySelector(".login-btn");
const message = document.getElementById("message");

// Login function
async function login(event) {
  event.preventDefault();

  if (!username.value || !password.value) {
    message.textContent = "Please fill in all fields.";
    message.style.color = "#ff6b6b";
    return;
  }

  const data = {
    userName: username.value,
    password: password.value,
  };

  try {
    const response = await postData("api/auth/login", data);

    if (response.userRole === "admin") {
      // Save JWT token in cookie
      document.cookie = `token=${response.token}; max-age=${24 * 60 * 60}; path=/`;

      message.style.color = "#4cd137";
      message.textContent = "Login successful! Redirecting...";

      setTimeout(() => {
        window.location.href = "../html/main.html";
      }, 2000);
    } else {
      message.style.color = "#ff6b6b";
      message.textContent = "Access denied: Only admins can login.";
    }
  } catch (error) {
    console.error("Login error:", error);
    message.style.color = "#ff6b6b";
    message.textContent = "Login failed. Please check your credentials.";
  }
}

loginBtn.addEventListener("click", login);
