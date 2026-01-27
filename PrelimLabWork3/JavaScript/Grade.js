const ATTENDANCE_WEIGHT = 0.40;
const LAB_WORK_WEIGHT = 0.60;
const CLASS_STANDING_WEIGHT = 0.70;
const PRELIM_EXAM_WEIGHT = 0.30;
const PASSING_GRADE = 75.0;
const EXCELLENT_GRADE = 100.0;
const MAX_ATTENDANCE = 20;

document.getElementById('gradeForm').addEventListener('submit', function(e) {
    e.preventDefault();
    document.getElementById('error').classList.remove('show');

    let attendances = parseInt(document.getElementById('attendances').value);
    const labWork1 = parseFloat(document.getElementById('labWork1').value);
    const labWork2 = parseFloat(document.getElementById('labWork2').value);
    const labWork3 = parseFloat(document.getElementById('labWork3').value);

    if (labWork1 < 0 || labWork1 > 100 || labWork2 < 0 || labWork2 > 100 || labWork3 < 0 || labWork3 > 100) {
        showError("Lab work grades must be between 0 and 100.");
        return;
    }

    if (attendances < 0 || attendances > MAX_ATTENDANCE) {
        showError("Number of attendances must be between 0 and " + MAX_ATTENDANCE + ".");
        return;
    }

    // 1️⃣ Lab Work Average
    const labWorkAverage = (labWork1 + labWork2 + labWork3) / 3.0;

    // 2️⃣ Attendance Score
    const attendanceScore = (attendances / MAX_ATTENDANCE) * 100;

    // 3️⃣ Class Standing
    const classStanding = (attendanceScore * ATTENDANCE_WEIGHT) + (labWorkAverage * LAB_WORK_WEIGHT);

    // 4️⃣ Required Prelim Exam
    const requiredExamForPassing = (PASSING_GRADE - (classStanding * CLASS_STANDING_WEIGHT)) / PRELIM_EXAM_WEIGHT;
    const requiredExamForExcellent = (EXCELLENT_GRADE - (classStanding * CLASS_STANDING_WEIGHT)) / PRELIM_EXAM_WEIGHT;

    displayResultsAnimated(attendanceScore, attendances, labWork1, labWork2, labWork3,
                           labWorkAverage, classStanding, requiredExamForPassing, requiredExamForExcellent);
});

function displayResultsAnimated(attendanceScore, attendances, lab1, lab2, lab3, labAverage, classStanding, passingScore, excellentScore) {
    animateNumber('attendanceScore', attendanceScore.toFixed(2) + `% (${attendances}/20 classes)`);
    animateNumber('displayLab1', lab1.toFixed(2));
    animateNumber('displayLab2', lab2.toFixed(2));
    animateNumber('displayLab3', lab3.toFixed(2));
    animateNumber('labAverage', labAverage.toFixed(2));
    animateNumber('classStanding', classStanding.toFixed(2));
    animateNumber('passingScore', passingScore.toFixed(2));
    animateNumber('excellentScore', excellentScore.toFixed(2));

    const passingRemark = document.getElementById('passingRemark');
    if (passingScore < 0) {
        passingRemark.textContent = "Congratulations! Already passing!";
        passingRemark.className = "remark success";
    } else if (passingScore <= 100.0) {
        passingRemark.textContent = `Score at least ${passingScore.toFixed(2)} on the Prelim Exam to pass.`;
        passingRemark.className = "remark";
    } else {
        passingRemark.textContent = "Difficult to pass. Required score > 100%.";
        passingRemark.className = "remark danger";
    }

    const excellentRemark = document.getElementById('excellentRemark');
    if (excellentScore < 0) {
        excellentRemark.textContent = "Outstanding! Already excellent!";
        excellentRemark.className = "remark success";
    } else if (excellentScore <= 100.0) {
        excellentRemark.textContent = `Score at least ${excellentScore.toFixed(2)} for excellent grade.`;
        excellentRemark.className = "remark";
    } else {
        excellentRemark.textContent = "Impossible to achieve excellent. Required score > 100%.";
        excellentRemark.className = "remark warning";
    }
}

// Count-up animation for numeric results
function animateNumber(id, target) {
    const element = document.getElementById(id);
    if (target.includes('%') || target.includes('(')) {
        element.textContent = target;
        return;
    }

    const numericValue = parseFloat(target) || 0;
    let current = 0;
    const increment = numericValue / 50;
    const interval = setInterval(() => {
        current += increment;
        if (current >= numericValue) {
            current = numericValue;
            clearInterval(interval);
        }
        element.textContent = current.toFixed(2);
    }, 10);
}

function showError(message) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = "Error: " + message;
    errorDiv.classList.add('show');
}
