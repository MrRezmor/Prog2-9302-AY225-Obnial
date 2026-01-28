const TOTAL_CLASSES = 5;
const MAX_ABSENCES = 3;
const ATTENDANCE_WEIGHT = 0.40;
const LAB_WEIGHT = 0.60;
const CLASS_STANDING_WEIGHT = 0.70;
const PRELIM_WEIGHT = 0.30;

const form = document.getElementById('gradeForm');
const inputs = document.querySelectorAll('input[type="number"]');
const statusRemark = document.getElementById('statusRemark');

// 1. Strict input control: No decimals & Auto-trimming
inputs.forEach(input => {
    input.addEventListener('keypress', (e) => {
        if (e.which < 48 || e.which > 57) e.preventDefault();
    });

    input.addEventListener('input', () => {
        let val = parseInt(input.value);
        if (input.id === 'attended' || input.id === 'excused' || input.id === 'unexcused') {
            if (val > 5) input.value = 5;
        } else if (val > 100) {
            input.value = 100;
        }
    });
});

// 2. Clear Button Logic
document.getElementById('btnClear').addEventListener('click', () => {
    form.reset();
    updateStats(0, 0, 0, 0);
    statusRemark.className = "remark normal";
    statusRemark.textContent = "Ready to calculate...";
});

// 3. Calculation Logic
form.addEventListener('submit', function(e) {
    e.preventDefault();

    const att = parseInt(document.getElementById('attended').value || 0);
    const exc = parseInt(document.getElementById('excused').value || 0);
    const unx = parseInt(document.getElementById('unexcused').value || 0);
    const l1 = parseInt(document.getElementById('lab1').value || 0);
    const l2 = parseInt(document.getElementById('lab2').value || 0);
    const l3 = parseInt(document.getElementById('lab3').value || 0);

    // Meeting validation
    if (att + exc + unx !== TOTAL_CLASSES) {
        alert("Total meetings must equal 5");
        return;
    }

    // Automatic Fail Check
    if (unx > MAX_ABSENCES) {
        statusRemark.className = "remark danger";
        statusRemark.textContent = `AUTOMATIC FAIL\nUnexcused Absences: ${unx}\nLimit: 3`;
        updateStats(0, 0, 0, 0);
        return;
    }

    const attScore = att * 20;
    const labAvg = Math.floor((l1 + l2 + l3) / 3);
    const classStanding = Math.round((attScore * ATTENDANCE_WEIGHT) + (labAvg * LAB_WEIGHT));
    const currentGrade = classStanding * CLASS_STANDING_WEIGHT;
    const needed = Math.ceil((75 - currentGrade) / PRELIM_WEIGHT);

    updateStats(attScore, labAvg, classStanding, currentGrade);

    if (currentGrade >= 75) {
        statusRemark.className = "remark success";
        statusRemark.textContent = "STATUS: ALREADY PASSED 🎉\nYou already met the passing grade even before the prelim exam.";
    } else {
        let msg = `PRELIM EXAM REQUIRED\n--------------------\n`;
        if (needed <= 100) {
            statusRemark.className = "remark normal";
            statusRemark.textContent = msg + `Grade needed to PASS: ${needed}\n\nSTATUS: CAN STILL PASS`;
        } else {
            statusRemark.className = "remark danger";
            statusRemark.textContent = msg + `Grade needed to PASS: MORE THAN 100\n\nSTATUS: IMPOSSIBLE TO PASS`;
        }
    }
});

function updateStats(attS, labA, stand, grad) {
    document.getElementById('resAtt').textContent = attS;
    document.getElementById('resLab').textContent = labA;
    document.getElementById('resStand').textContent = stand;
    document.getElementById('resGrade').textContent = grad === 0 ? "0.00" : grad.toFixed(2);
}