// dashboard.js
const mainContent = document.getElementById("container");
const logoutButton = document.querySelector("button");

// Utility to get cookie value by name
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
}

// Fetch data with JWT token in Authorization header
async function getData(endpoint) {
  const token = getCookie("token");
  if (!token) {
    throw new Error("No token found, please login.");
  }

  const response = await fetch(`http://localhost:8080/${endpoint}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Request failed");
  }

  return response.json();
}

const renderUsers = (users) => {
  mainContent.innerHTML = "";
  users.forEach(user => {
    const userCard = document.createElement("div");
    userCard.classList.add("user-card");
    userCard.innerHTML = `
      <span>${user}</span> 
      <button class="ban-btn" data-username="${user}">Ban</button>
    `;
    mainContent.appendChild(userCard);
  });

  // Attach event listeners for ban buttons
  document.querySelectorAll(".ban-btn").forEach(btn => {
    btn.addEventListener("click", async (e) => {
      const username = e.target.dataset.username;
      try {
        await banUser(username);
        alert(`User ${username} banned successfully.`);
        // Refresh list after banning
        init();
      } catch (error) {
        alert(`Failed to ban user: ${error.message}`);
      }
    });
  });
};

// Ban user API call
async function banUser(username) {
  const token = getCookie("token");
  if (!token) throw new Error("No token found.");

  const response = await fetch(`http://localhost:8080/admin/banUser/${username}`, {
    method: "GET", // your backend uses GET here for banUser, ideally should be POST or DELETE
    headers: {
      "Authorization": `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Ban request failed");
  }

  return response.json();
}

const init = async () => {
  try {
    const users = await getData("admin/getUser");
    renderUsers(users);
  } catch (error) {
    mainContent.textContent = `Fetch users error: ${error.message}`;
    console.error("Fetch users error:", error);
  }
};

const logOutHandler = () => {
  document.cookie = "token=; max-age=0; path=/";
  location.assign("Login.html");
};

document.addEventListener("DOMContentLoaded", init);
logoutButton.addEventListener("click", logOutHandler);
