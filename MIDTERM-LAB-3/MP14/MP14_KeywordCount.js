// ============================================================
// MP14 - Count Keyword Occurrences
// Programmer: OBNIAL, MARIUS RYZER C.
// Course: Programming 2 | BSIT GD - 1st Year
// Description: This program asks the user for a keyword,
//              then counts how many times it appears across
//              all rows and columns in the CSV dataset.
// ============================================================

// 'fs' = Node.js module for file operations (reading files)
const fs = require("fs");

// 'readline' = Node.js module for reading keyboard input
const readline = require("readline");

// Create the input/output interface for the terminal
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

// Step 1: Ask for the file path first
rl.question("Enter the CSV dataset file path: ", function (filePath) {

  // Step 2: Ask for the keyword to count
  rl.question("Enter keyword to count: ", function (keyword) {

    keyword = keyword.trim(); // Remove extra spaces from keyword

    // If nothing was typed, show error and stop
    if (keyword === "") {
      console.log("ERROR: No keyword entered.");
      rl.close();
      return;
    }

    // Call our main processing function
    processKeywordCount(filePath, keyword);

    rl.close(); // Close input interface after we're done asking
  });
});

// ============================================================
// FUNCTION: processKeywordCount
// PURPOSE : Reads the CSV file, counts how many times the
//           keyword appears, and displays the results.
// PARAMETERS:
//   filePath - path to the CSV file
//   keyword  - the word/phrase to search for
// ============================================================
function processKeywordCount(filePath, keyword) {

  // Check if the file actually exists at the given path
  if (!fs.existsSync(filePath)) {
    console.log("ERROR: File not found. Please check the file path.");
    return;
  }

  // Read the entire file as text
  const fileContent = fs.readFileSync(filePath, "utf8");

  // Split the text into an array of lines
  const lines = fileContent.split("\n");

  // Convert keyword to lowercase for case-insensitive search
  // e.g., searching "pass" will also match "PASS", "Pass", etc.
  const keywordLower = keyword.toLowerCase();

  // --- Counters and storage ---
  let totalOccurrences = 0;  // Grand total of all keyword matches
  let rowsContaining = 0;    // How many rows had at least one match
  let dataRowCount = 0;      // How many real data rows were checked
  let matchDetails = [];     // Stores info about each matched row
  let headerFound = false;   // Tracks if we've passed the header row

  // --- Process each line ---
  for (let i = 0; i < lines.length; i++) {
    let lineNumber = i + 1;        // Line number for display (starts at 1)
    let line = lines[i].trim();    // Current line, trimmed of whitespace

    // Skip blank lines
    if (line === "") continue;

    // Parse the line into individual column values
    let columns = parseCSVLine(line);

    // --- Find the real header row ---
    if (!headerFound && columns[0].trim().toLowerCase() === "candidate") {
      headerFound = true; // Mark that we found the header
      continue;           // Skip this line (it's a header, not data)
    }

    // --- Process data rows only (after header is found) ---
    if (headerFound) {
      dataRowCount++; // Count this row

      let countInThisRow = 0; // Matches found in just this row

      // Check each column value in the row
      for (let j = 0; j < columns.length; j++) {
        let cellLower = columns[j].trim().toLowerCase(); // Lowercase for comparison

        // Count how many times keyword appears in this cell
        let found = countOccurrences(cellLower, keywordLower);
        countInThisRow += found; // Add to this row's count
      }

      // If keyword appeared at least once in this row
      if (countInThisRow > 0) {
        totalOccurrences += countInThisRow; // Add to grand total
        rowsContaining++;                   // Count this row as a match

        let name = columns.length > 0 ? columns[0].trim() : "(unknown)";
        matchDetails.push(
          `Row ${lineNumber} | ${name} | Occurrences in row: ${countInThisRow}`
        );
      }
    }
  }

  // --- Display the results ---
  console.log("\n============================================================");
  console.log("           MP14 - KEYWORD OCCURRENCE COUNT");
  console.log("============================================================");
  console.log(`  Keyword Searched  : "${keyword}"`);
  console.log("  File              : " + filePath);
  console.log("  Total Data Rows   : " + dataRowCount);
  console.log("------------------------------------------------------------");
  console.log("  Total Occurrences : " + totalOccurrences);
  console.log("  Rows Containing   : " + rowsContaining);
  console.log("------------------------------------------------------------");

  if (matchDetails.length === 0) {
    console.log(`  No matches found for "${keyword}".`);
  } else {
    console.log("  Matched Rows:");
    matchDetails.forEach(function (detail) {
      console.log("    " + detail);
    });
  }

  console.log("============================================================");
}

// ============================================================
// FUNCTION: countOccurrences
// PURPOSE : Counts how many times 'keyword' appears in 'text'
// PARAMETERS:
//   text    - the string to search inside
//   keyword - the string to look for
// RETURNS: number (count of matches)
// ============================================================
function countOccurrences(text, keyword) {
  let count = 0;   // Start counting at zero
  let index = 0;   // Start searching from position 0 in the text

  // indexOf returns -1 when the keyword is not found anymore
  while ((index = text.indexOf(keyword, index)) !== -1) {
    count++;                   // Found a match!
    index += keyword.length;   // Move forward to search for the next one
  }

  return count; // Return total count
}

// ============================================================
// FUNCTION: parseCSVLine
// PURPOSE : Splits a CSV line into column values correctly,
//           handling quoted fields that may contain commas.
// PARAMETER: line - a single line from the CSV file
// RETURNS: array of string values
// ============================================================
function parseCSVLine(line) {
  let result = [];      // Stores all extracted values
  let current = "";     // Builds up the current field value
  let inQuotes = false; // Are we inside a quoted field?

  for (let i = 0; i < line.length; i++) {
    let char = line[i];

    if (char === '"') {
      inQuotes = !inQuotes; // Flip the flag when we see a quote
    } else if (char === "," && !inQuotes) {
      result.push(current); // End of field — save it
      current = "";          // Reset for next field
    } else {
      current += char; // Add character to current field
    }
  }

  result.push(current); // Save the last field
  return result;
}