# Programming Assignment 1 — 3×3 Matrix Determinant Solver

## Student Information
- **Full Name:** Marius Ryzer C. Obnial
- **Section:** 1203L 9302-AY225
- **Course:** BSIT-GD 1 Game Develeopment, UPHSD Molino Campus
- **Assignment:** Programming Assignment 1 — 3x3 Matrix Determinant Solver
- **Academic Year:** 2025–2026

---

## Assigned Matrix

My assigned matrix (Student #25):

```
| 3  6  2 |
| 5  1  4 |
| 2  3  5 |
```

---

## How to Run the Java Program

**Step 1 — Compile the program:**
```bash
javac DeterminantSolver.java
```

**Step 2 — Run the compiled program:**
```bash
java DeterminantSolver
```

> Make sure you are inside the `linear-algebra/assignment-01/` folder when running these commands.

---

## How to Run the JavaScript Program

**Run with Node.js:**
```bash
node determinant_solver.js
```

> Requires Node.js to be installed. No additional packages needed.

---

## Sample Output

Both programs produce the same result. Below is the sample output:

```
====================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: Marius Ryzer C. Obnial
  Assigned Matrix:
====================================================
  +               +
  |    3    6    2  |
  |    5    1    4  |
  |    2    3    5  |
  +               +
====================================================

  Expanding along Row 1 (cofactor expansion):

  Step 1 - Minor M11: det([1,4],[3,5]) = (1 x 5) - (4 x 3) = 5 - 12 = -7
  Step 2 - Minor M12: det([5,4],[2,5]) = (5 x 5) - (4 x 2) = 25 - 8 = 17
  Step 3 - Minor M13: det([5,1],[2,3]) = (5 x 3) - (1 x 2) = 15 - 2 = 13

  Cofactor C11 = (+1) x 3 x -7 = -21
  Cofactor C12 = (-1) x 6 x 17 = -102
  Cofactor C13 = (+1) x 2 x 13 = 26

  det(M) = -21 + (-102) + 26
====================================================
  DETERMINANT = -97
====================================================
```

---

## Final Determinant Value

**det(M) = -97**

Since the determinant is not zero, the matrix is **non-singular** and has an inverse.

---

## Files in This Repository

| File | Description |
|------|-------------|
| `DeterminantSolver.java` | Java solution — computes the determinant with step-by-step console output |
| `determinant_solver.js` | JavaScript (Node.js) solution — identical logic and output |
| `README.md` | This documentation file |

---

## Repository

**GitHub:** `https://github.com/MrRezmor/Prog2-9302-AY225-OBNIAL.git`
