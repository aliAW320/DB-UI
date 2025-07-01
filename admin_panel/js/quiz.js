const API_BASE = 'http://localhost:8080/admin';

// Get token from cookie
function getTokenFromCookie() {
  const tokenMatch = document.cookie.match(/(^|;) ?token=([^;]*)/);
  return tokenMatch ? tokenMatch[2] : null;
}

const token = getTokenFromCookie();
console.log("JWT Token from cookie:", token);

if (!token) {
  alert("No token found. Please login first.");
  window.location.href = "../html/login.html"; // Redirect to login page
}

// Fetch questions from backend
async function fetchQuestions() {
  try {
    const res = await fetch(`${API_BASE}/notVerifiedQuestions`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error('Fetch error:', res.status, errorText);
      alert('Failed to load questions');
      return;
    }

    const questions = await res.json();
    displayQuestions(questions);
  } catch (err) {
    console.error("Network error:", err);
    alert("Could not connect to the server.");
  }
}

// Render question cards to the DOM
function displayQuestions(questions) {
  const container = document.getElementById('questions-container');
  container.innerHTML = '';

  questions.forEach(q => {
    const div = document.createElement('div');
    div.className = 'question-card';

    const options = [q.option1, q.option2, q.option3, q.option4];

    div.innerHTML = `
      <p><strong>Q${q.question_id}:</strong> ${q.question}</p>
      <ul>
        ${options.map((opt, i) => `
          <li class="${q.answer === i + 1 ? 'correct' : ''}">
            ${i + 1}. ${opt}
          </li>`).join('')}
      </ul>
      <p><small>Level: ${q.level}, Category: ${q.category_id}, Author: ${q.author_id}</small></p>
      <div class="buttons">
        <button class="accept" onclick="acceptQuestion(${q.question_id}, this)">Accept</button>
        <button class="reject" onclick="rejectQuestion(${q.question_id}, this)">Reject</button>
      </div>
    `;
    container.appendChild(div);
  });
}

// Send accept API call
async function acceptQuestion(id, button) {
  try {
    const res = await fetch(`${API_BASE}/acceptQuestion?questionID=${id}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    const text = await res.text();
    if (text === "true") {
      button.closest('.question-card').remove();
    } else {
      alert("Failed to accept question.");
    }
  } catch (err) {
    console.error("Accept error:", err);
    alert("Failed to accept question due to network error.");
  }
}

// Send reject API call
async function rejectQuestion(id, button) {
  try {
    const res = await fetch(`${API_BASE}/rejectQuestions/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    const text = await res.text();
    if (text === "true") {
      button.closest('.question-card').remove();
    } else {
      alert("Failed to reject question.");
    }
  } catch (err) {
    console.error("Reject error:", err);
    alert("Failed to reject question due to network error.");
  }
}

// Start everything
fetchQuestions();
