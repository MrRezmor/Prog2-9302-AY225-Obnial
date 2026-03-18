// ============================================================
// MP13 - Identify Rows with Missing Data
// Programmer: OBNIAL, MARIUS RYZER C.
// Course: Programming 2 | BSIT GD - 1st Year
// Description: This program reads a CSV file and finds all
//              rows where one or more fields are empty/blank.
// ============================================================

import java.io.*;        // For reading files
import java.util.*;      // For ArrayList

public class MP13_MissingData {

    public static void main(String[] args) {

        // Scanner is used to get keyboard input from the user
        Scanner scanner = new Scanner(System.in);

        // Ask the user to type the path to the CSV file
        System.out.print("Enter the CSV dataset file path: ");
        String filePath = scanner.nextLine(); // Store what the user typed

        // This list will hold all the rows that have missing (empty) data
        ArrayList<String> rowsWithMissing = new ArrayList<>();

        // 'rowNumber' tracks which line number we are currently reading
        int rowNumber = 0;

        // 'dataRowCount' counts only the actual data rows (not headers or metadata)
        int dataRowCount = 0;

        // 'headerColumns' will store the column names from the header row
        String[] headerColumns = null;

        // 'headerLineNumber' stores the line number where the real header is found
        int headerLineNumber = -1;

        // --- Start reading the file ---
        // BufferedReader reads the file line by line efficiently
        // FileReader opens the actual file from the path the user gave
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line; // 'line' holds the current line being read

            // Keep reading lines until there are no more (null means end of file)
            while ((line = br.readLine()) != null) {
                rowNumber++; // Increase line counter each time we read a new line

                // Skip completely blank lines (lines with no content at all)
                if (line.trim().isEmpty()) continue;

                // Split the line into individual values using comma as separator
                // -1 means "keep empty values at the end of the line"
                String[] columns = line.split(",", -1);

                // --- Find the real header row ---
                // We look for the row that starts with "Candidate"
                // That's the actual column header row in this dataset
                if (headerColumns == null && columns[0].trim().equalsIgnoreCase("Candidate")) {
                    headerColumns = columns;       // Save the header
                    headerLineNumber = rowNumber;  // Remember which line it was on
                    continue;                      // Skip to the next line
                }

                // --- Process only rows that come AFTER the header ---
                if (headerColumns != null) {
                    dataRowCount++; // Count this as a real data row

                    // 'hasMissing' will become true if we find any empty field
                    boolean hasMissing = false;

                    // 'missingFields' will list which fields are missing
                    StringBuilder missingFields = new StringBuilder();

                    // Loop through each column in the current row
                    for (int i = 0; i < headerColumns.length; i++) {

                        // Check if the current column value is empty or just spaces
                        // Also check that the column itself has a real header name
                        boolean columnHasName = i < headerColumns.length &&
                                                !headerColumns[i].trim().isEmpty();
                        boolean valueIsMissing = i >= columns.length ||
                                                 columns[i].trim().isEmpty();

                        // Only flag it if the column is a named/important column
                        if (columnHasName && valueIsMissing) {
                            hasMissing = true; // Mark this row as having missing data

                            // Add the missing column name to our list
                            if (missingFields.length() > 0) missingFields.append(", ");
                            missingFields.append(headerColumns[i].trim());
                        }
                    }

                    // If we found any missing fields in this row, record it
                    if (hasMissing) {
                        // Format: "Row 8 | Candidate: [value] | Missing: [columns]"
                        String candidateName = columns.length > 0 ? columns[0].trim() : "(unknown)";
                        rowsWithMissing.add("Row " + rowNumber +
                                            " | Candidate: " + candidateName +
                                            " | Missing Fields: [" + missingFields + "]");
                    }
                }
            }

            // --- Display the results ---
            System.out.println("\n============================================================");
            System.out.println("            MP13 - ROWS WITH MISSING DATA");
            System.out.println("============================================================");
            System.out.println("File      : " + filePath);
            System.out.println("Total Data Rows Checked : " + dataRowCount);
            System.out.println("Rows With Missing Data  : " + rowsWithMissing.size());
            System.out.println("------------------------------------------------------------");

            // If no rows with missing data were found
            if (rowsWithMissing.isEmpty()) {
                System.out.println("  No rows with missing data found. Dataset is complete!");
            } else {
                // Print each row that had missing data
                for (String row : rowsWithMissing) {
                    System.out.println("  " + row);
                }
            }

            System.out.println("============================================================");

        } catch (FileNotFoundException e) {
            // This error happens if the file path is wrong or file doesn't exist
            System.out.println("ERROR: File not found. Please check the file path.");
        } catch (IOException e) {
            // This catches any other file reading errors
            System.out.println("ERROR: Could not read the file. " + e.getMessage());
        }

        scanner.close(); // Always close the scanner when done
    }
}