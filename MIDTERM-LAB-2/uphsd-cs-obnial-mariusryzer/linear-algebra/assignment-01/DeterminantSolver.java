/**
 * =====================================================
 * Student Name    : Marius Ryzer C. Obnial
 * Course          : BSIT GD 1 - Game Development
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 17, 2026
 * GitHub Repo     : https://github.com/MrRezmor/Prog2-9302-AY225-OBNIAL.git/uphsd-cs-obnial-mariusryzer
 *
 * Description:
 *   This program computes the determinant of a hardcoded 3x3 matrix assigned
 *   to Marius Ryzer C. Obnial for BSIT GD 1 - Game Development. The solution is computed using
 *   cofactor expansion along the first row. Each intermediate step — the 2x2
 *   minor, cofactor term, and running sum — is printed to the console so the
 *   solution is easy to follow step by step.
 * =====================================================
 */
public class DeterminantSolver {

    // ── SECTION 1: Matrix Declaration ───────────────────────────────────
    // This is my assigned 3x3 matrix from the assignment sheet (#25).
    // I store it as a 2D integer array, meaning an array of arrays.
    // The outer array holds the rows, and each inner array holds one row's values.
    // Row 1: [3, 6, 2]  |  Row 2: [5, 1, 4]  |  Row 3: [2, 3, 5]
    static int[][] matrix = {
        { 3, 6, 2 },   // Row 1 of assigned matrix
        { 5, 1, 4 },   // Row 2 of assigned matrix
        { 2, 3, 5 }    // Row 3 of assigned matrix
    };

    // ── SECTION 2: 2x2 Determinant Helper ───────────────────────────────
    // This method figures out the determinant of a small 2x2 matrix.
    // I pass in four numbers that represent a 2x2 grid:
    //   | a  b |
    //   | c  d |
    // The formula is just: (a times d) minus (b times c)
    // I call this method 3 times, once for each minor in the expansion.
    static int computeMinor(int a, int b, int c, int d) {
        // Multiply diagonally and subtract to get the 2x2 determinant
        return (a * d) - (b * c);
    }

    // ── SECTION 3: Matrix Printer ────────────────────────────────────────
    // This method just prints the 3x3 matrix in a nice box format.
    // I loop through each row of the matrix and print the three values
    // with spacing so it looks clean and readable.
    static void printMatrix(int[][] m) {
        System.out.println("  +               +");
        for (int[] row : m) {
            // %2d means: print the integer, and use at least 2 spaces wide
            System.out.printf("  |  %2d  %2d  %2d  |%n", row[0], row[1], row[2]);
        }
        System.out.println("  +               +");
    }

    // ── SECTION 4: Step-by-Step Determinant Solver ──────────────────────
    // This is the main solving method. It does everything in order:
    //   1. Prints a header and the matrix
    //   2. Calculates the three 2x2 minors (M11, M12, M13)
    //   3. Shows the arithmetic for each minor step by step
    //   4. Multiplies each minor by its sign and the first-row element (cofactors)
    //   5. Adds the three cofactors for the final determinant
    //   6. Checks if the matrix is singular (det = 0)
    static void solveDeterminant(int[][] m) {

        // Print the banner header with my name and the matrix
        System.out.println("====================================================");
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: Marius Ryzer C. Obnial");
        System.out.println("  Assigned Matrix:");
        System.out.println("====================================================");
        printMatrix(m);
        System.out.println("====================================================");
        System.out.println();
        System.out.println("  Expanding along Row 1 (cofactor expansion):");
        System.out.println();

        // ── Step 1: Compute minor M11 ──
        // I cover row 0 (first row) and column 0 (first column) with my finger.
        // What's left is: m[1][1], m[1][2], m[2][1], m[2][2]
        // Those are the four numbers I send to computeMinor().
        int minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
        System.out.printf("  Step 1 - Minor M11: det([%d,%d],[%d,%d]) = (%d x %d) - (%d x %d) = %d - %d = %d%n",
            m[1][1], m[1][2], m[2][1], m[2][2],
            m[1][1], m[2][2], m[1][2], m[2][1],
            (m[1][1] * m[2][2]), (m[1][2] * m[2][1]),
            minor11);

        // ── Step 2: Compute minor M12 ──
        // Cover row 0 and column 1 (second column).
        // Remaining elements: m[1][0], m[1][2], m[2][0], m[2][2]
        int minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
        System.out.printf("  Step 2 - Minor M12: det([%d,%d],[%d,%d]) = (%d x %d) - (%d x %d) = %d - %d = %d%n",
            m[1][0], m[1][2], m[2][0], m[2][2],
            m[1][0], m[2][2], m[1][2], m[2][0],
            (m[1][0] * m[2][2]), (m[1][2] * m[2][0]),
            minor12);

        // ── Step 3: Compute minor M13 ──
        // Cover row 0 and column 2 (third column).
        // Remaining elements: m[1][0], m[1][1], m[2][0], m[2][1]
        int minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
        System.out.printf("  Step 3 - Minor M13: det([%d,%d],[%d,%d]) = (%d x %d) - (%d x %d) = %d - %d = %d%n",
            m[1][0], m[1][1], m[2][0], m[2][1],
            m[1][0], m[2][1], m[1][1], m[2][0],
            (m[1][0] * m[2][1]), (m[1][1] * m[2][0]),
            minor13);

        // ── Cofactor Terms ──
        // Now I apply the alternating sign rule: + - + for the first row.
        // C11 = positive (+1) times m[0][0] times minor11
        // C12 = negative (-1) times m[0][1] times minor12
        // C13 = positive (+1) times m[0][2] times minor13
        int c11 =  m[0][0] * minor11;   // positive sign for column 1
        int c12 = -m[0][1] * minor12;   // negative sign for column 2
        int c13 =  m[0][2] * minor13;   // positive sign for column 3

        System.out.println();
        System.out.printf("  Cofactor C11 = (+1) x %d x %d = %d%n", m[0][0], minor11, c11);
        System.out.printf("  Cofactor C12 = (-1) x %d x %d = %d%n", m[0][1], minor12, c12);
        System.out.printf("  Cofactor C13 = (+1) x %d x %d = %d%n", m[0][2], minor13, c13);

        // ── Final Determinant ──
        // Add all three cofactor values together to get the final answer.
        int det = c11 + c12 + c13;
        System.out.printf("%n  det(M) = %d + (%d) + %d%n", c11, c12, c13);
        System.out.println("====================================================");
        System.out.printf("  DETERMINANT = %d%n", det);

        // ── Singular Matrix Check ──
        // A determinant of zero means no inverse exists -- the matrix is singular.
        // My matrix does NOT produce zero, but the check must still be here per requirements.
        if (det == 0) {
            System.out.println("  The matrix is SINGULAR -- it has no inverse.");
        }
        System.out.println("====================================================");
    }

    // ── SECTION 5: Entry Point ───────────────────────────────────────────
    // main() is where Java starts running my program.
    // I just call solveDeterminant() and pass in the matrix declared at the top.
    public static void main(String[] args) {
        solveDeterminant(matrix);
    }

}