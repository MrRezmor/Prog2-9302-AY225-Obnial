// ============================================================
// MP13 - Identify Rows with Missing Data
// Programmer: OBNIAL, MARIUS RYZER C.
// Course: Programming 2 | BSIT GD - 1st Year
// Description: This program reads a CSV file and finds all
//              rows where one or more fields are empty/blank.
// ============================================================

// 'fs' is the Node.js built-in module for reading/writing files
const fs = require("fs");

// 'readline' lets us read user input from the keyboard (terminal)
const readline = require("readline");

// Create an interface that connects keyboard input to our program
// 'process.stdin'  = keyboard input
// 'process.stdout' = screen output
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

// Ask the user to type the file path, then run our main function
rl.question("Enter the CSV dataset file path: ", function (filePath) {
  // Call our main processing function with the path the user gave
  processMissingData(filePath);

  // Close the input interface since we no longer need keyboard input
  rl.close();
});

// ============================================================
// FUNCTION: processMissingData
// PURPOSE : Reads the CSV, finds rows with empty fields,
//           and displays them on the screen.
// PARAMETER: filePath - the location of the CSV file
// ============================================================
function processMissingData(filePath) {

  // --- Step 1: Check if the file actually exists ---
  if (!fs.existsSync(filePath)) {
    console.log("ERROR: File not found. Please check the file path.");
    return; // Stop the function, no point continuing
  }

  // --- Step 2: Read the entire file as one big text string ---
  // 'utf8' means read it as normal readable text (not binary data)
  const fileContent = fs.readFileSync(filePath, "utf8");

  // --- Step 3: Split the big text into individual lines ---
  // '\n' is the newline character — splits wherever there's a new line
  const lines = fileContent.split("\n");

  // --- Variables we'll use while processing ---
  let headerColumns = null;   // Will store the column header names
  let dataRowCount = 0;       // Counts the real data rows
  let rowsWithMissing = [];   // List of rows that have missing fields

  // --- Step 4: Loop through every line one by one ---
  for (let i = 0; i < lines.length; i++) {

    // 'lineNumber' is for display purposes (human-readable, starts at 1)
    let lineNumber = i + 1;

    // Remove extra spaces or hidden characters from both ends of the line
    let line = lines[i].trim();

    // Skip completely empty lines
    if (line === "") continue;

    // --- Step 5: Parse the line into individual column values ---
    // We use our custom parseCSVLine function to handle
    // values that have commas inside quotes (like "Obnial, Marius")
    let columns = parseCSVLine(line);

    // --- Step 6: Find the real header row ---
    // The header row starts with "Candidate" (case-insensitive check)
    if (headerColumns === null && columns[0].trim().toLowerCase() === "candidate") {
      headerColumns = columns; // Save the headers for reference
      continue;                // Move to the next line
    }

    // --- Step 7: Process data rows (only after we found the header) ---
    if (headerColumns !== null) {
      dataRowCount++; // Count this as a real data row

      let hasMissing = false;      // Flag: does this row have a missing field?
      let missingFields = [];      // List of missing column names for this row

      // Check each column in the header
      for (let j = 0; j < headerColumns.length; j++) {

        // Only check columns that have a real header name
        let columnHasName = headerColumns[j].trim() !== "";

        // Check if the value in this column is empty or doesn't exist
        let valueIsMissing = j >= columns.length || columns[j].trim() === "";

        // If the column is named and the value is missing, flag it
        if (columnHasName && valueIsMissing) {
          hasMissing = true;
          missingFields.push(headerColumns[j].trim()); // Record the column name
        }
      }

      // If this row had at least one missing field, save it
      if (hasMissing) {
        let candidateName = columns.length > 0 ? columns[0].trim() : "(unknown)";
        rowsWithMissing.push(
          `Row ${lineNumber} | Candidate: ${candidateName} | Missing Fields: [${missingFields.join(", ")}]`
        );
      }
    }
  }

  // --- Step 8: Display the results ---
  console.log("\n============================================================");
  console.log("            MP13 - ROWS WITH MISSING DATA");
  console.log("============================================================");
  console.log("File      : " + filePath);
  console.log("Total Data Rows Checked : " + dataRowCount);
  console.log("Rows With Missing Data  : " + rowsWithMissing.length);
  console.log("------------------------------------------------------------");

  if (rowsWithMissing.length === 0) {
    // No missing data found
    console.log("  No rows with missing data found. Dataset is complete!");
  } else {
    // Print every row that had missing data
    rowsWithMissing.forEach(function (row) {
      console.log("  " + row);
    });
  }

  console.log("============================================================");
}

// ============================================================
// FUNCTION: parseCSVLine
// PURPOSE : Splits one CSV line into an array of values,
//           while correctly handling quoted fields that may
//           contain commas inside them.
// EXAMPLE : "Obnial, Marius",Student,,Python
//           → ["Obnial, Marius", "Student", "", "Python"]
// PARAMETER: line - one line of text from the CSV file
// ============================================================
function parseCSVLine(line) {
  let result = [];   // This will hold all the values we extract
  let current = "";  // Builds up the current field character by character
  let inQuotes = false; // Tracks if we're currently inside "quoted text"

  for (let i = 0; i < line.length; i++) {
    let char = line[i]; // The current character we are looking at

    if (char === '"') {
      // Toggle the inQuotes flag when we see a quote mark
      inQuotes = !inQuotes;
    } else if (char === "," && !inQuotes) {
      // A comma outside of quotes = end of a field
      result.push(current); // Save the completed field
      current = "";          // Reset for the next field
    } else {
      // Any other character: just add it to the current field
      current += char;
    }
  }

  // Don't forget to save the last field (no comma after it)
  result.push(current);

  return result; // Return the full array of values
}