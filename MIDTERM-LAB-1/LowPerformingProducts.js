/**
 * LowPerformingProducts.js
 * Detects low-performing video game products from vgchartz-2024.csv.
 *
 * - Reads EVERY row (64,016 data rows, 14 columns each).
 * - Aggregates total_sales per game title across all platforms/region rows.
 * - Games with no sales data in any row still appear (0.00 M).
 * - Computes dataset average over all unique titles.
 * - Flags titles whose aggregated sales fall below the average.
 *
 * Run: node LowPerformingProducts.js
 */

'use strict';

const fs       = require('fs');
const readline = require('readline');

// ════════════════════════════════════════════════════════════════════════════
// DataRecord — stores all 14 columns from one CSV row
// ════════════════════════════════════════════════════════════════════════════
function DataRecord(img, title, console_, genre, publisher, developer,
                    criticScore, totalSales, naSales, jpSales,
                    palSales, otherSales, releaseDate, lastUpdate) {
    this.img         = img;
    this.title       = title;
    this.console     = console_;
    this.genre       = genre;
    this.publisher   = publisher;
    this.developer   = developer;
    this.criticScore = criticScore;   // number (0 if missing)
    this.totalSales  = totalSales;    // number in millions (0 if missing)
    this.naSales     = naSales;
    this.jpSales     = jpSales;
    this.palSales    = palSales;
    this.otherSales  = otherSales;
    this.releaseDate = releaseDate;
    this.lastUpdate  = lastUpdate;
}

// ════════════════════════════════════════════════════════════════════════════
// Utility helpers
// ════════════════════════════════════════════════════════════════════════════

/** Safely parse a string to float; returns 0 for blank / N/A / non-numeric. */
function safeFloat(s) {
    if (!s) return 0;
    const t = s.trim();
    if (t === '' || t.toLowerCase() === 'n/a') return 0;
    const v = parseFloat(t);
    return isNaN(v) ? 0 : v;
}

/**
 * Parse one CSV line correctly, handling quoted fields that may contain commas.
 * Returns an array of field strings.
 */
function parseCSVLine(line) {
    const fields = [];
    let cur = '';
    let inQ = false;
    for (let i = 0; i < line.length; i++) {
        const ch = line[i];
        if (ch === '"') {
            inQ = !inQ;
        } else if (ch === ',' && !inQ) {
            fields.push(cur);
            cur = '';
        } else {
            cur += ch;
        }
    }
    fields.push(cur);
    return fields;
}

/** Truncate a string to max chars for aligned display. */
function trunc(s, max) {
    if (!s) return '';
    return s.length <= max ? s : s.slice(0, max - 3) + '...';
}

/** Right-pad / left-pad helpers. */
const rpad = (s, n) => String(s).padEnd(n);
const lpad = (s, n) => String(s).padStart(n);

/** Format a number as e.g. "19,700.00 M" */
function fmtM(n) {
    return n.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' M';
}

const W   = 80;
const SEP = (ch = '=') => console.log(ch.repeat(W));

// ════════════════════════════════════════════════════════════════════════════
// loadCSV — reads and parses the entire file; returns DataRecord[]
// ════════════════════════════════════════════════════════════════════════════
function loadCSV(filePath) {
    const raw     = fs.readFileSync(filePath, 'utf8');
    const lines   = raw.split(/\r?\n/);

    if (lines.length < 2) throw new Error('CSV file is empty or has only a header.');

    // Parse header
    const headers = parseCSVLine(lines[0]).map(h => h.trim().toLowerCase());

    const need = ['img','title','console','genre','publisher','developer',
                  'critic_score','total_sales','na_sales','jp_sales',
                  'pal_sales','other_sales','release_date','last_update'];

    const idx = {};
    for (const col of need) {
        const i = headers.indexOf(col);
        if (i === -1) throw new Error(`Missing expected column: "${col}"\nDetected headers: ${lines[0]}`);
        idx[col] = i;
    }

    const records = [];
    let skipped   = 0;

    for (let i = 1; i < lines.length; i++) {
        const line = lines[i];
        if (!line.trim()) continue;            // skip blank lines

        const c = parseCSVLine(line);
        const g = col => (c[idx[col]] !== undefined ? c[idx[col]].trim() : '');

        const title = g('title');
        if (!title) { skipped++; continue; }  // skip rows with no title at all

        records.push(new DataRecord(
            g('img'),
            title,
            g('console'),
            g('genre'),
            g('publisher'),
            g('developer'),
            safeFloat(g('critic_score')),
            safeFloat(g('total_sales')),
            safeFloat(g('na_sales')),
            safeFloat(g('jp_sales')),
            safeFloat(g('pal_sales')),
            safeFloat(g('other_sales')),
            g('release_date'),
            g('last_update')
        ));
    }

    if (records.length === 0) throw new Error('No valid data rows found in CSV.');
    if (skipped > 0) console.log(`  [Info] ${skipped} blank/title-less line(s) skipped.`);
    return records;
}

// ════════════════════════════════════════════════════════════════════════════
// aggregateSales — sums total_sales per unique game title, sorted desc
// ════════════════════════════════════════════════════════════════════════════
function aggregateSales(records) {
    const totals = {};
    for (const r of records) {
        totals[r.title] = (totals[r.title] || 0) + r.totalSales;
    }

    // Sort by sales descending
    return Object.entries(totals)
        .sort((a, b) => b[1] - a[1]);
        // returns array of [title, totalSales] pairs
}

// ════════════════════════════════════════════════════════════════════════════
// computeAverage — mean of all unique-title totals
// ════════════════════════════════════════════════════════════════════════════
function computeAverage(sorted) {
    const sum = sorted.reduce((acc, [, v]) => acc + v, 0);
    return sum / sorted.length;
}

// ════════════════════════════════════════════════════════════════════════════
// printFullTable — prints every unique title with its total sales
// ════════════════════════════════════════════════════════════════════════════
function printFullTable(sorted, average, totalRows) {
    const withSales  = sorted.filter(([, v]) => v > 0).length;
    const noSales    = sorted.length - withSales;

    SEP('=');
    console.log('        VGChartz 2024 — TOTAL SALES PER PRODUCT REPORT');
    SEP('=');
    console.log(`  Total CSV rows read         : ${totalRows.toLocaleString()}`);
    console.log(`  Unique game titles          : ${sorted.length.toLocaleString()}`);
    console.log(`  Titles with sales data      : ${withSales.toLocaleString()}`);
    console.log(`  Titles with no sales data   : ${noSales.toLocaleString()}  (stored as 0.00 M)`);
    console.log(`  Dataset average (all titles): ${average.toFixed(4)} M units`);
    SEP('=');
    console.log(`  ${rpad('RANK', 6)} ${rpad('GAME TITLE', 52)}  ${'TOTAL SALES'.padStart(14)}`);
    SEP('-');

    let rank = 1;
    for (const [title, sales] of sorted) {
        console.log(`  ${lpad(rank++, 5)}. ${rpad(trunc(title, 52), 52)}  ${lpad(fmtM(sales), 14)}`);
    }

    SEP('-');
    console.log(`  ${rpad('DATASET AVERAGE', 59)}  ${lpad(average.toFixed(4) + ' M', 14)}`);
    SEP('=');
}

// ════════════════════════════════════════════════════════════════════════════
// printFlagged — prints titles whose sales are below average
// ════════════════════════════════════════════════════════════════════════════
function printFlagged(sorted, average) {
    const flagged = sorted.filter(([, v]) => v < average);

    console.log();
    SEP('=');
    console.log('     ⚠  LOW-PERFORMING PRODUCTS (TOTAL SALES BELOW DATASET AVERAGE)');
    SEP('=');
    console.log(`  Average threshold  : ${average.toFixed(4)} M units`);
    console.log(`  Flagged titles     : ${flagged.length.toLocaleString()} of ${sorted.length.toLocaleString()} unique games`);
    SEP('-');
    console.log(`  ${rpad('RANK', 6)} ${rpad('GAME TITLE', 52)}  ${'TOTAL SALES'.padStart(14)}`);
    SEP('-');

    let rank = 1;
    for (const [title, sales] of flagged) {
        console.log(`  ${lpad(rank++, 5)}. ${rpad(trunc(title, 52), 52)}  ${lpad(fmtM(sales), 14)}`);
    }

    SEP('=');
    console.log();
    console.log('  Recommendation: Review and consider phasing out flagged titles.');
    console.log(`  (${flagged.length.toLocaleString()} titles flagged out of ${sorted.length.toLocaleString()} total unique games in dataset)`);
    SEP('=');
    console.log();
}

// ════════════════════════════════════════════════════════════════════════════
// processData — aggregate, print full table, print flagged
// ════════════════════════════════════════════════════════════════════════════
function processData(records) {
    console.log('Aggregating sales per title...');
    const sorted  = aggregateSales(records);
    const average = computeAverage(sorted);

    console.log();
    printFullTable(sorted, average, records.length);
    printFlagged(sorted, average);
}

// ════════════════════════════════════════════════════════════════════════════
// askFilePath — recursive callback loop (required pattern)
// ════════════════════════════════════════════════════════════════════════════
const rl = readline.createInterface({
    input:  process.stdin,
    output: process.stdout
});

function askFilePath() {
    rl.question('Enter dataset file path: ', function(filePath) {
        if (fs.existsSync(filePath)) {
            try {
                console.log('File found. Processing...');
                const records = loadCSV(filePath);
                console.log(`  [OK] ${records.length.toLocaleString()} rows loaded from CSV.`);
                rl.close();
                processData(records);
            } catch (err) {
                console.log('Invalid file path. Try again.');
                askFilePath();
            }
        } else {
            console.log('Invalid file path. Try again.');
            askFilePath();
        }
    });
}

askFilePath();