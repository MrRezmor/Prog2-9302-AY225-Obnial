// Programmer: Marius Ryzer C. Obnial
// Student ID: 25-2483-305
// Student Record System - JavaScript Web Implementation

const csvData = `StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE
073900438,Osbourne,Wakenshaw,69,5,52,12,5
114924014,Albie,Gierardi,58,92,16,57,5
111901632,Eleen,Pentony,43,81,34,36,2
084000084,Arie,Okenden,31,5,14,39,5
272471551,Alica,Muckley,49,66,97,3,5
104900721,Jo,Burleton,98,94,33,13,3
111924392,Cam,Akram,44,84,17,16,3
292970744,Celine,Brosoli,3,15,71,83,4
107004352,Alan,Belfit,31,51,36,70,4
071108313,Jeanette,Gilvear,4,78,15,69,5
042204932,Ethelin,MacCathay,48,36,23,1,1
111914218,Kakalina,Finnick,69,5,65,10,1
074906059,Mayer,Lorenzetti,36,30,100,41,5
091000080,Selia,Rosenstengel,15,42,85,68,3
055002480,Dalia,Tadd,84,86,13,91,2
063101111,Darryl,Doogood,36,3,78,13,5
071908827,Brier,Wace,69,92,23,75,4
322285668,Bucky,Udall,97,63,19,46,3
103006406,Haslett,Beaford,41,32,85,60,5
104913048,Shelley,Spring,84,73,63,59,0
051403517,Marius,Southway,28,75,29,88,5
021301869,Katharina,Storch,6,61,6,49,5
063115178,Hester,Menendez,70,46,73,40,5
084202442,Shaylynn,Scorthorne,50,80,81,96,5
275079882,Madonna,Willatt,23,12,17,83,1`;

let students = [];

function parseCSV(csvString) {
    const lines = csvString.trim().split('\n');
    const data = [];
    
    for (let i = 1; i < lines.length; i++) {
        const values = lines[i].split(',');
        const student = {
            studentID: values[0],
            firstName: values[1],
            lastName: values[2],
            labWork1: parseInt(values[3]),
            labWork2: parseInt(values[4]),
            labWork3: parseInt(values[5]),
            prelimExam: parseInt(values[6]),
            attendance: parseInt(values[7])
        };
        data.push(student);
    }
    
    return data;
}

function computePrelimGrade(lab1, lab2, lab3, prelim, attendance) {
    const labAverage = (lab1 + lab2 + lab3) / 3;
    const attendanceScore = (attendance / 5) * 100;
    const classStanding = (attendanceScore * 0.40) + (labAverage * 0.60);
    const prelimGrade = (prelim * 0.30) + (classStanding * 0.70);
    return prelimGrade.toFixed(2);
}

function initializeData() {
    students = parseCSV(csvData);
    render();
    showSuccess('Mock data loaded successfully! Total records: ' + students.length);
}

function render() {
    const tableBody = document.getElementById('tableBody');
    tableBody.innerHTML = '';
    
    students.forEach((student, index) => {
        const prelimGrade = computePrelimGrade(
            student.labWork1, 
            student.labWork2, 
            student.labWork3, 
            student.prelimExam, 
            student.attendance
        );
        
        const row = `
            <tr>
                <td>${student.studentID}</td>
                <td>${student.firstName}</td>
                <td>${student.lastName}</td>
                <td>${student.labWork1}</td>
                <td>${student.labWork2}</td>
                <td>${student.labWork3}</td>
                <td>${student.prelimExam}</td>
                <td>${student.attendance}</td>
                <td><strong>${prelimGrade}</strong></td>
                <td>
                    <button class="btn-delete" onclick="deleteStudent(${index})">Delete</button>
                </td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
    
    updateStats();
}

function addStudent() {
    const studentID = document.getElementById('studentID').value.trim();
    const firstName = document.getElementById('firstName').value.trim();
    const lastName = document.getElementById('lastName').value.trim();
    
    let lab1 = parseInt(document.getElementById('lab1').value) || 0;
    let lab2 = parseInt(document.getElementById('lab2').value) || 0;
    let lab3 = parseInt(document.getElementById('lab3').value) || 0;
    let prelim = parseInt(document.getElementById('prelim').value) || 0;
    let attendance = parseInt(document.getElementById('attendance').value) || 0;
    
    if (!studentID || !firstName || !lastName) {
        showError('Student ID, First Name, and Last Name are required!');
        return;
    }
    
    const exists = students.some(student => student.studentID === studentID);
    if (exists) {
        showError('Student ID already exists!');
        return;
    }
    
    if (attendance > 5) {
        showError('Attendance cannot exceed 5!');
        return;
    }
    
    if (lab1 > 100 || lab2 > 100 || lab3 > 100 || prelim > 100) {
        showError('Grades cannot exceed 100!');
        return;
    }
    
    const newStudent = {
        studentID: studentID,
        firstName: firstName,
        lastName: lastName,
        labWork1: lab1,
        labWork2: lab2,
        labWork3: lab3,
        prelimExam: prelim,
        attendance: attendance
    };
    
    const prelimGrade = computePrelimGrade(lab1, lab2, lab3, prelim, attendance);
    
    students.push(newStudent);
    render();
    clearFields();
    showSuccess(`Student ${firstName} ${lastName} added successfully! Prelim Grade: ${prelimGrade}`);
}

function deleteStudent(index) {
    const student = students[index];
    const confirmDelete = confirm(`Delete student: ${student.firstName} ${student.lastName}?`);
    
    if (confirmDelete) {
        students.splice(index, 1);
        render();
        showSuccess('Student deleted successfully!');
    }
}

function clearFields() {
    document.getElementById('studentID').value = '';
    document.getElementById('firstName').value = '';
    document.getElementById('lastName').value = '';
    document.getElementById('lab1').value = '';
    document.getElementById('lab2').value = '';
    document.getElementById('lab3').value = '';
    document.getElementById('prelim').value = '';
    document.getElementById('attendance').value = '';
    document.getElementById('studentID').focus();
}

function saveToCSV() {
    let csvContent = 'StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE,PRELIM GRADE\n';
    
    students.forEach(student => {
        const prelimGrade = computePrelimGrade(
            student.labWork1, 
            student.labWork2, 
            student.labWork3, 
            student.prelimExam, 
            student.attendance
        );
        csvContent += `${student.studentID},${student.firstName},${student.lastName},${student.labWork1},${student.labWork2},${student.labWork3},${student.prelimExam},${student.attendance},${prelimGrade}\n`;
    });
    
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'student_records.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
    
    showSuccess('Data saved successfully to student_records.csv!');
}

function updateStats() {
    const totalStudents = students.length;
    document.getElementById('stats').textContent = `Total Students: ${totalStudents}`;
}

function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 5000);
    
    hideSuccess();
}

function showSuccess(message) {
    const successDiv = document.getElementById('successMessage');
    successDiv.textContent = message;
    successDiv.style.display = 'block';
    
    setTimeout(() => {
        successDiv.style.display = 'none';
    }, 3000);
    
    hideError();
}

function hideError() {
    document.getElementById('errorMessage').style.display = 'none';
}

function hideSuccess() {
    document.getElementById('successMessage').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() {
    initializeData();
    
    const attendanceInput = document.getElementById('attendance');
    attendanceInput.addEventListener('input', function() {
        if (this.value.includes('.')) {
            this.value = this.value.split('.')[0];
        }
        if (parseInt(this.value) > 5) {
            this.value = 5;
        }
    });
    
    const gradeInputs = ['lab1', 'lab2', 'lab3', 'prelim'];
    gradeInputs.forEach(id => {
        const input = document.getElementById(id);
        input.addEventListener('input', function() {
            if (this.value.includes('.')) {
                this.value = this.value.split('.')[0];
            }
            if (parseInt(this.value) > 100) {
                this.value = 100;
            }
        });
    });
    
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addStudent();
            }
        });
    });
});