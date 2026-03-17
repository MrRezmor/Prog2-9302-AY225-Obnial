/**
 * =====================================================
 * Student Name    : Marius Ryzer C. Obnial
 * Course          : BSIT GD 1 - Game Development
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 17, 2026
 * GitHub Repo     : https://github.com/MrRezmor/Prog2-9302-AY225-OBNIAL.git/uphsd-cs-obnial-mariusryzer
 * Runtime         : Node.js (run with: node determinant_solver.js)
 *
 * Description:
 *   JavaScript version of DeterminantSolver.java. This script computes the
 *   determinant of my assigned 3x3 matrix using cofactor expansion along the
 *   first row. Every step of the math is logged to the console so anyone
 *   reading the output can follow the solution from start to finish.
 * =====================================================
 */

// ── SECTION 1: Matrix Declaration ───────────────────────────────────
// My assigned matrix stored as a 2D JavaScript array (array of arrays).
// matrix[0] is Row 1, matrix[1] is Row 2, matrix[2] is Row 3.
// matrix[row][col] -- row and col both start at 0, not 1.
const matrix = [
    [3, 6, 2],   // Row 1
    [5, 1, 4],   // Row 2
    [2, 3, 5]    // Row 3
];

// ── SECTION 2: Matrix Printer ────────────────────────────────────────
// This function loops through each row and prints the three values
// with padding so the numbers line up nicely in columns.
// padStart(3) means: make the number at least 3 characters wide (adds spaces).
function printMatrix(m) {
    console.log(`  +               +`);
    m.forEach(row => {
        const fmt = row.map(v => v.toString().padStart(3)).join("  ");
        console.log(`  |  ${fmt}  |`);
    });
    console.log(`  +               +`);
}

// ── SECTION 3: 2x2 Determinant Helper ───────────────────────────────
// Takes four numbers representing a 2x2 grid and returns the determinant.
// Think of it as:
//   | a  b |
//   | c  d |
// determinant = (a * d) - (b * c)
// Arrow function syntax: (params) => expression
const computeMinor = (a, b, c, d) => (a * d) - (b * c);

// ── SECTION 4: Step-by-Step Determinant Solver ──────────────────────
// The main function. Walks through the cofactor expansion visibly:
//   1. Prints header and matrix
//   2. Computes each 2x2 minor with labeled steps
//   3. Computes the signed cofactor for each element in row 1
//   4. Sums cofactors for the final determinant
//   5. Checks for singular matrix (det = 0)
function solveDeterminant(m) {
    const line = "=".repeat(52);

    // Print the title header with my name and the matrix
    console.log(line);
    console.log("  3x3 MATRIX DETERMINANT SOLVER");
    console.log("  Student: Marius Ryzer C. Obnial");
    console.log("  Assigned Matrix:");
    console.log(line);
    printMatrix(m);
    console.log(line);
    console.log();
    console.log("  Expanding along Row 1 (cofactor expansion):");
    console.log();

    // ── Step 1: Minor M11 ──
    // Hide row 0 and column 0. The 4 remaining values are:
    // m[1][1], m[1][2] (second row, columns 1 and 2)
    // m[2][1], m[2][2] (third row, columns 1 and 2)
    const minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
    console.log(
        `  Step 1 - Minor M11: det([${m[1][1]},${m[1][2]}],[${m[2][1]},${m[2][2]}])` +
        ` = (${m[1][1]} x ${m[2][2]}) - (${m[1][2]} x ${m[2][1]})` +
        ` = ${m[1][1] * m[2][2]} - ${m[1][2] * m[2][1]} = ${minor11}`
    );

    // ── Step 2: Minor M12 ──
    // Hide row 0 and column 1. The 4 remaining values are:
    // m[1][0], m[1][2] (second row, columns 0 and 2)
    // m[2][0], m[2][2] (third row, columns 0 and 2)
    const minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
    console.log(
        `  Step 2 - Minor M12: det([${m[1][0]},${m[1][2]}],[${m[2][0]},${m[2][2]}])` +
        ` = (${m[1][0]} x ${m[2][2]}) - (${m[1][2]} x ${m[2][0]})` +
        ` = ${m[1][0] * m[2][2]} - ${m[1][2] * m[2][0]} = ${minor12}`
    );

    // ── Step 3: Minor M13 ──
    // Hide row 0 and column 2. The 4 remaining values are:
    // m[1][0], m[1][1] (second row, columns 0 and 1)
    // m[2][0], m[2][1] (third row, columns 0 and 1)
    const minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
    console.log(
        `  Step 3 - Minor M13: det([${m[1][0]},${m[1][1]}],[${m[2][0]},${m[2][1]}])` +
        ` = (${m[1][0]} x ${m[2][1]}) - (${m[1][1]} x ${m[2][0]})` +
        ` = ${m[1][0] * m[2][1]} - ${m[1][1] * m[2][0]} = ${minor13}`
    );

    // ── Cofactor Terms ──
    // Sign pattern for row 1: + - +
    // c11 gets a positive sign  -- multiply normally
    // c12 gets a negative sign  -- put a minus in front
    // c13 gets a positive sign  -- multiply normally
    const c11 =  m[0][0] * minor11;
    const c12 = -m[0][1] * minor12;
    const c13 =  m[0][2] * minor13;

    console.log();
    console.log(`  Cofactor C11 = (+1) x ${m[0][0]} x ${minor11} = ${c11}`);
    console.log(`  Cofactor C12 = (-1) x ${m[0][1]} x ${minor12} = ${c12}`);
    console.log(`  Cofactor C13 = (+1) x ${m[0][2]} x ${minor13} = ${c13}`);

    // ── Final Determinant ──
    // Just add all three cofactors together for the answer
    const det = c11 + c12 + c13;
    console.log();
    console.log(`  det(M) = ${c11} + (${c12}) + ${c13}`);
    console.log(line);
    console.log(`  DETERMINANT = ${det}`);

    // ── Singular Matrix Check ──
    // If the determinant is 0 the matrix has no inverse and is called singular.
    // My determinant is not 0, but I still need this check in the code.
    if (det === 0) {
        console.log("  The matrix is SINGULAR -- it has no inverse.");
    }
    console.log(line);
}

// ── SECTION 5: Program Entry Point ──────────────────────────────────
// This line actually runs everything. I call solveDeterminant with my matrix.
solveDeterminant(matrix);