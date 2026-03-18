// ============================================================
// MP15 - Export First 50 Rows to CSV
// Programmer: OBNIAL, MARIUS RYZER C.
// Course: Programming 2 | BSIT GD - 1st Year
// Description: This program reads the CSV dataset, takes the
//              first 50 data rows plus the header, and writes
//              them to a new CSV output file.
// ============================================================

// 'fs' = Node.js built-in module for file reading and writing
const fs = require("fs");

// 'readline' = Node.js module for reading keyboard input (terminal)
const readline = require("readline");

// Set up the terminal input/output interface
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

// Step 1: Ask the user for the input CSV file path
rl.question("Enter the CSV dataset file path: ", function (filePath) {

  // Step 2: Ask where to save the output file
  rl.question(
    "Enter output file name (e.g., output_mp15.csv): ",
    function (outputPath) {

      outputPath = outputPath.trim();

      // If no output name given, use a default
      if (outputPath === "") {
        outputPath = "output_mp15.csv";
      }

      // Call the main export function
      exportFirst50(filePath, outputPath);

      rl.close(); // Done with input, close the interface
    }
  );
});

// ============================================================
// FUNCTION: exportFirst50
// PURPOSE : Reads the input CSV, extracts the header + first
//           50 data rows, writes them to a new CSV file,
//           and shows a preview and summary.
// PARAMETERS:
//   filePath   - path to the input CSV file
//   outputPath - path/name for the output CSV file
// ============================================================
function exportFirst50(filePath, outputPath) {

  // --- Step 1: Check if the input file exists ---
  if (!fs.existsSync(filePath)) {
    console.log("ERROR: File not found. Please check the file path.");
    return;
  }

  // --- Step 2: Read the entire file content as text ---
  const fileContent = fs.readFileSync(filePath, "utf8");

  // --- Step 3: Split the text into individual lines ---
  const lines = fileContent.split("\n");

  // --- Variables ---
  let headerLine = null;       // Stores the header row text
  let exportedRows = [];       // Stores the 50 data rows to export
  let headerFound = false;     // Did we find the header row yet?
  const MAX_ROWS = 50;         // We want exactly 50 data rows

  // --- Step 4: Loop through each line ---
  for (let i = 0; i < lines.length; i++) {

    let line = lines[i].trim(); // Current line without leading/trailing spaces

    // Skip blank lines
    if (line === "") continue;

    // Parse the line into column values
    let columns = parseCSVLine(line);

    // --- Find the header row ---
    // It's the row where the first column says "Candidate"
    if (!headerFound && columns[0].trim().toLowerCase() === "candidate") {
      headerFound = true; // Header located!
      headerLine = line;  // Save it for the output file
      continue;           // Don't add to data rows
    }

    // --- Collect data rows after the header (up to 50) ---
    if (headerFound && exportedRows.length < MAX_ROWS) {
      exportedRows.push(line); // Add this row to our export list

      // Once we hit 50 rows, stop collecting
      if (exportedRows.length >= MAX_ROWS) break;
    }
  }

  // --- Step 5: Build the output content ---
  // We join header + data rows into one big string, each on its own line
  let outputLines = [];

  if (headerLine !== null) {
    outputLines.push(headerLine); // First line = the header
  }

  // Add all the collected data rows
  exportedRows.forEach(function (row) {
    outputLines.push(row);
  });

  // Join all lines with newline characters to make the final file content
  let outputContent = outputLines.join("\n");

  // --- Step 6: Write the output to a new file ---
  // 'fs.writeFileSync' creates/overwrites a file with the given content
  // 'utf8' means write it as normal readable text
  try {
    fs.writeFileSync(outputPath, outputContent, "utf8");
  } catch (err) {
    console.log("ERROR: Could not write the output file. " + err.message);
    return;
  }

  // --- Step 7: Display results and preview ---
  console.log("\n============================================================");
  console.log("          MP15 - EXPORT FIRST 50 ROWS TO CSV");
  console.log("============================================================");
  console.log("  Input File  : " + filePath);
  console.log("  Output File : " + outputPath);
  console.log("  Rows Exported (excluding header) : " + exportedRows.length);
  console.log("------------------------------------------------------------");

  // Show a preview of the first 5 exported rows
  console.log("  Preview (first 5 exported rows):");
  let previewCount = Math.min(5, exportedRows.length); // Show at most 5
  for (let i = 0; i < previewCount; i++) {
    console.log("    " + exportedRows[i]);
  }

  console.log("------------------------------------------------------------");
  console.log("  Export complete! File saved to: " + outputPath);
  console.log("============================================================");
}

// ============================================================
// FUNCTION: parseCSVLine
// PURPOSE : Splits a single CSV line into an array of values,
//           correctly handling quoted fields with commas inside.
// EXAMPLE : "Obnial, Marius",Student,,Python
//           → ["Obnial, Marius", "Student", "", "Python"]
// PARAMETER: line - one line of text from the CSV
// RETURNS: array of string values
// ============================================================
function parseCSVLine(line) {
  let result = [];      // Array to store parsed values
  let current = "";     // Current field being built
  let inQuotes = false; // Flag: are we inside a quoted field?

  for (let i = 0; i < line.length; i++) {
    let char = line[i];

    if (char === '"') {
      inQuotes = !inQuotes; // Toggle in/out of quoted section
    } else if (char === "," && !inQuotes) {
      result.push(current); // End of a field — save it
      current = "";          // Reset for next field
    } else {
      current += char; // Add character to current field
    }
  }

  result.push(current); // Push the last field (no trailing comma)
  return result;
}