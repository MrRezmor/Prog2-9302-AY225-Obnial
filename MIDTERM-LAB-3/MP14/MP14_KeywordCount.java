// ============================================================
// MP14 - Count Keyword Occurrences
// Programmer: OBNIAL, MARIUS RYZER C.
// Course: Programming 2 | BSIT GD - 1st Year
// Description: This program asks the user for a keyword,
//              then searches the entire CSV dataset for how
//              many times that keyword appears across all rows
//              and columns.
// ============================================================

import java.io.*;        // For reading files
import java.util.*;      // For ArrayList, Scanner

public class MP14_KeywordCount {

    public static void main(String[] args) {

        // Scanner reads keyboard input from the user
        Scanner scanner = new Scanner(System.in);

        // Step 1: Ask for the file path
        System.out.print("Enter the CSV dataset file path: ");
        String filePath = scanner.nextLine(); // Store the path user typed

        // Step 2: Ask for the keyword to search
        System.out.print("Enter keyword to count: ");
        String keyword = scanner.nextLine().trim(); // Store and clean the keyword

        // If the user typed nothing, exit early
        if (keyword.isEmpty()) {
            System.out.println("ERROR: No keyword entered.");
            scanner.close();
            return;
        }

        // 'keywordLower' is the keyword in lowercase so we can do
        // case-insensitive comparison (e.g., "pass" matches "PASS", "Pass")
        String keywordLower = keyword.toLowerCase();

        // --- Counters ---
        int totalOccurrences = 0;  // How many total times keyword was found
        int rowsContaining = 0;    // How many rows had at least one match
        int dataRowCount = 0;      // How many real data rows were processed

        // This list stores descriptions of each row that had a match
        ArrayList<String> matchDetails = new ArrayList<>();

        // 'headerFound' tells us if we've already passed the header line
        boolean headerFound = false;

        // --- Start reading the file ---
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;       // Holds the current line
            int lineNumber = 0; // Tracks line number for display

            while ((line = br.readLine()) != null) {
                lineNumber++; // Count every line we read

                // Skip blank lines
                if (line.trim().isEmpty()) continue;

                // Split the line into column values
                // We use a simple split here; quoted commas are rare in this dataset
                String[] columns = line.split(",", -1);

                // --- Look for the header row ---
                if (!headerFound && columns[0].trim().equalsIgnoreCase("Candidate")) {
                    headerFound = true; // Found header, now look at data rows
                    continue;
                }

                // --- Only process real data rows (after the header) ---
                if (headerFound) {
                    dataRowCount++; // Count this as a data row

                    int countInThisRow = 0; // Keyword count just for this row

                    // Loop through each column value in this row
                    for (String col : columns) {
                        // Convert the cell value to lowercase for comparison
                        String cellLower = col.trim().toLowerCase();

                        // Count how many times the keyword appears in this cell
                        // We use countOccurrences() helper method below
                        int found = countOccurrences(cellLower, keywordLower);
                        countInThisRow += found; // Add to this row's count
                    }

                    // If we found the keyword at least once in this row
                    if (countInThisRow > 0) {
                        totalOccurrences += countInThisRow; // Add to grand total
                        rowsContaining++;                   // Mark this row as matched

                        // Get the candidate's name for display (first column)
                        String name = columns.length > 0 ? columns[0].trim() : "(unknown)";
                        matchDetails.add("Row " + lineNumber +
                                         " | " + name +
                                         " | Occurrences in row: " + countInThisRow);
                    }
                }
            }

            // --- Display the results ---
            System.out.println("\n============================================================");
            System.out.println("           MP14 - KEYWORD OCCURRENCE COUNT");
            System.out.println("============================================================");
            System.out.printf("  Keyword Searched  : \"%s\"%n", keyword);
            System.out.println("  File              : " + filePath);
            System.out.println("  Total Data Rows   : " + dataRowCount);
            System.out.println("------------------------------------------------------------");
            System.out.println("  Total Occurrences : " + totalOccurrences);
            System.out.println("  Rows Containing   : " + rowsContaining);
            System.out.println("------------------------------------------------------------");

            if (matchDetails.isEmpty()) {
                System.out.println("  No matches found for \"" + keyword + "\".");
            } else {
                System.out.println("  Matched Rows:");
                for (String detail : matchDetails) {
                    System.out.println("    " + detail);
                }
            }

            System.out.println("============================================================");

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found. Please check the file path.");
        } catch (IOException e) {
            System.out.println("ERROR: Problem reading the file. " + e.getMessage());
        }

        scanner.close(); // Close scanner when done
    }

    // ============================================================
    // METHOD: countOccurrences
    // PURPOSE: Counts how many times 'keyword' appears inside 'text'
    // PARAMETERS:
    //   text    - the string to search in
    //   keyword - the string to search for
    // RETURNS: integer count of occurrences
    // ============================================================
    public static int countOccurrences(String text, String keyword) {
        int count = 0;   // Start at zero
        int index = 0;   // Start searching from position 0

        // Keep searching until indexOf returns -1 (no more matches)
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;                   // Found one! Add to count
            index += keyword.length(); // Move past this match to find the next
        }

        return count; // Return the total count
    }
}