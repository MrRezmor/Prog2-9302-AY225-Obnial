// Predefined credentials
const VALID_USERNAME = "admin";
const VALID_PASSWORD = "password123";

// Store attendance records
let attendanceRecords = [];

// ============================================
// BEEP SOUND SETUP
// ============================================
const beep = new Audio('WindowsErrorSound.mp3');

// Function to play beep sound
function playBeep() {
    beep.play();
}
// ============================================

// Format timestamp
function formatTimestamp(date) {
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${month}/${day}/${year} ${hours}:${minutes}:${seconds}`;
}

// Handle login form submission
document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const messageDiv = document.getElementById('message');
    
    // Validate credentials
    if (username === VALID_USERNAME && password === VALID_PASSWORD) {
        // Successful login
        const loginTime = new Date();
        const timestamp = formatTimestamp(loginTime);
        
        // Store attendance record
        attendanceRecords.push({
            username: username,
            timestamp: timestamp
        });
        
        // Hide login section and show welcome section
        document.getElementById('loginSection').classList.add('hidden');
        document.getElementById('welcomeSection').classList.add('active');
        
        // Display welcome message
        document.getElementById('welcomeMessage').textContent = 
            `Welcome, ${username}! You have successfully logged in.`;
        
        // Display timestamp
        document.getElementById('timestampDisplay').innerHTML = 
            `<strong>Login Time:</strong>${timestamp}`;
        
    } else {
        // Failed login - play beep and show error
        playBeep();
        
        messageDiv.className = 'message error';
        messageDiv.textContent = 'Invalid username or password. Please try again.';
        
        // Clear the error message after 3 seconds
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = '';
        }, 3000);
    }
    
    // Clear password field
    document.getElementById('password').value = '';
});

// Download attendance summary using Blob API
function downloadAttendance() {
    if (attendanceRecords.length === 0) {
        alert('No attendance records to download.');
        return;
    }
    
    // Get the latest attendance record
    const latestRecord = attendanceRecords[attendanceRecords.length - 1];
    const username = latestRecord.username;
    const timestamp = latestRecord.timestamp;
    
    // Generate attendance data as specified in instructions
    const attendanceData = "Username: " + username + "\nTimestamp: " + timestamp;
    
    // Create blob and trigger download
    const blob = new Blob([attendanceData], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = 'attendance_summary.txt';
    link.click();
}

// Logout function
function logout() {
    // Hide welcome section and show login section
    document.getElementById('welcomeSection').classList.remove('active');
    document.getElementById('loginSection').classList.remove('hidden');
    
    // Clear input fields
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    
    // Clear any messages
    document.getElementById('message').textContent = '';
    document.getElementById('message').className = '';
}