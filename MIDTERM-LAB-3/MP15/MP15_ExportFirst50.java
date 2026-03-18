// ============================================================
// MP15 - Export First 50 Rows to CSV
// Programmer: OBNIAL, MARIUS RYZER C.
// Course: Programming 2 | BSIT GD - 1st Year
// Description: This program reads the CSV dataset, takes the
//              first 50 data rows (plus the header), and saves
//              them to a new CSV file called "output_mp15.csv".
// ============================================================

import java.io.*;        // For reading and writing files
import java.util.*;      // For ArrayList

public class MP15_ExportFirst50 {

    public static void main(String[] args) {

        // Scanner is used to read keyboard input
        Scanner scanner = new Scanner(System.in);

        // Step 1: Ask the user where the CSV file is located
        System.out.print("Enter the CSV dataset file path: ");
        String filePath = scanner.nextLine().trim();

        // Step 2: Ask the user where to save the output file
        System.out.print("Enter output file name (e.g., output_mp15.csv): ");
        String outputPath = scanner.nextLine().trim();

        // If no output name was given, use a default name
        if (outputPath.isEmpty()) {
            outputPath = "output_mp15.csv";
        }

        // 'headerLine' stores the header row to include at the top of the output
        String headerLine = null;

        // 'exportedRows' stores the data rows we will write to the output file
        ArrayList<String> exportedRows = new ArrayList<>();

        // 'dataRowCount' counts how many actual data rows we have collected
        int dataRowCount = 0;

        // 'headerFound' tracks whether we've found the real column header
        boolean headerFound = false;

        // Maximum number of rows we want to export
        final int MAX_ROWS = 50;

        // --- Step 3: Read the original CSV file ---
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line; // 'line' holds the current line being read

            while ((line = br.readLine()) != null) {

                // Skip completely blank lines
                if (line.trim().isEmpty()) continue;

                // Split the line into column values
                String[] columns = line.split(",", -1);

                // --- Find the header row ---
                // Look for the line that starts with "Candidate"
                if (!headerFound && columns[0].trim().equalsIgnoreCase("Candidate")) {
                    headerFound = true;   // Mark header as found
                    headerLine = line;    // Save the header line
                    continue;             // Don't add it to data yet
                }

                // --- Collect data rows (up to MAX_ROWS = 50) ---
                if (headerFound && dataRowCount < MAX_ROWS) {
                    exportedRows.add(line); // Add this row to our export list
                    dataRowCount++;         // Increase the data row counter

                    // Once we have 50 rows, stop reading
                    if (dataRowCount >= MAX_ROWS) break;
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Input file not found. Check the file path.");
            scanner.close();
            return;
        } catch (IOException e) {
            System.out.println("ERROR: Could not read the file. " + e.getMessage());
            scanner.close();
            return;
        }

        // --- Step 4: Write the output CSV file ---
        // PrintWriter creates/overwrites a file and lets us write text to it
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {

            // Write the header row first (if we found one)
            if (headerLine != null) {
                pw.println(headerLine);
            }

            // Write each of the collected data rows
            for (String row : exportedRows) {
                pw.println(row);
            }

            // If PrintWriter had any errors while writing:
            if (pw.checkError()) {
                System.out.println("ERROR: Something went wrong while writing the file.");
                scanner.close();
                return;
            }

        } catch (IOException e) {
            System.out.println("ERROR: Could not write output file. " + e.getMessage());
            scanner.close();
            return;
        }

        // --- Step 5: Display confirmation ---
        System.out.println("\n============================================================");
        System.out.println("          MP15 - EXPORT FIRST 50 ROWS TO CSV");
        System.out.println("============================================================");
        System.out.println("  Input File  : " + filePath);
        System.out.println("  Output File : " + outputPath);
        System.out.println("  Rows Exported (excluding header) : " + dataRowCount);
        System.out.println("------------------------------------------------------------");

        // Preview the first 5 exported rows as a sample
        System.out.println("  Preview (first 5 exported rows):");
        for (int i = 0; i < Math.min(5, exportedRows.size()); i++) {
            System.out.println("    " + exportedRows.get(i));
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("  Export complete! File saved to: " + outputPath);
        System.out.println("============================================================");

        scanner.close(); // Always close scanner at the end
    }
}