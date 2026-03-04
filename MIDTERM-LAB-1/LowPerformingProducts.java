import java.io.*;
import java.util.*;

/**
 * LowPerformingProducts.java
 * Detects low-performing video game products from vgchartz-2024.csv.
 *
 * - Reads EVERY row (64 016 data rows, 14 columns each).
 * - Aggregates total_sales per game title across all platforms/regions.
 * - Games with no sales data in ANY row still appear (0.00 M).
 * - Computes dataset average over all unique titles.
 * - Flags titles whose aggregated sales are below the average.
 *
 * Compile : javac DataRecord.java LowPerformingProducts.java
 * Run     : java LowPerformingProducts
 */
public class LowPerformingProducts {

    // ── CSV row parser (handles quoted fields with embedded commas) ──────────
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb    = new StringBuilder();
        boolean inQuotes    = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    // ── Load every row from the CSV ─────────────────────────────────────────
    public static List<DataRecord> loadCSV(String filePath) throws IOException {
        List<DataRecord> records = new ArrayList<>();
        BufferedReader reader    = new BufferedReader(
            new InputStreamReader(new FileInputStream(filePath), "UTF-8"));

        String headerLine = reader.readLine();
        if (headerLine == null) { reader.close(); throw new IOException("CSV file is empty."); }

        // Map header names → column indices (case-insensitive, trimmed)
        String[] headers = parseCSVLine(headerLine);
        Map<String, Integer> colIdx = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            colIdx.put(headers[i].trim().toLowerCase(), i);
        }

        // Required columns
        String[] required = {"img","title","console","genre","publisher","developer",
                             "critic_score","total_sales","na_sales","jp_sales",
                             "pal_sales","other_sales","release_date","last_update"};
        for (String col : required) {
            if (!colIdx.containsKey(col))
                throw new IOException("Missing expected column: " + col);
        }

        int iImg    = colIdx.get("img");
        int iTitle  = colIdx.get("title");
        int iCon    = colIdx.get("console");
        int iGenre  = colIdx.get("genre");
        int iPub    = colIdx.get("publisher");
        int iDev    = colIdx.get("developer");
        int iCS     = colIdx.get("critic_score");
        int iTS     = colIdx.get("total_sales");
        int iNA     = colIdx.get("na_sales");
        int iJP     = colIdx.get("jp_sales");
        int iPAL    = colIdx.get("pal_sales");
        int iOther  = colIdx.get("other_sales");
        int iRel    = colIdx.get("release_date");
        int iUpd    = colIdx.get("last_update");

        String line;
        int lineNum = 1;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            if (line.trim().isEmpty()) continue;
            String[] c = parseCSVLine(line);

            // Safely get a column value (returns "" if index out of range)
            java.util.function.IntFunction<String> col =
                idx -> (idx < c.length) ? c[idx].trim() : "";

            records.add(new DataRecord(
                col.apply(iImg),
                col.apply(iTitle),
                col.apply(iCon),
                col.apply(iGenre),
                col.apply(iPub),
                col.apply(iDev),
                DataRecord.parseDouble(col.apply(iCS)),
                DataRecord.parseDouble(col.apply(iTS)),
                DataRecord.parseDouble(col.apply(iNA)),
                DataRecord.parseDouble(col.apply(iJP)),
                DataRecord.parseDouble(col.apply(iPAL)),
                DataRecord.parseDouble(col.apply(iOther)),
                col.apply(iRel),
                col.apply(iUpd)
            ));
        }
        reader.close();

        if (records.isEmpty())
            throw new IOException("No valid data rows found in CSV.");
        return records;
    }

    // ── Aggregate total_sales per title (sum over all platform/region rows) ──
    public static LinkedHashMap<String, Double> aggregateSales(List<DataRecord> records) {
        // Use insertion-order map; we'll sort by sales descending after
        Map<String, Double> totals = new LinkedHashMap<>();
        for (DataRecord r : records) {
            String key = r.getTitle();
            totals.merge(key, r.getTotalSales(), Double::sum);
        }

        // Sort descending by total sales
        List<Map.Entry<String, Double>> entries = new ArrayList<>(totals.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        LinkedHashMap<String, Double> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : entries) sorted.put(e.getKey(), e.getValue());
        return sorted;
    }

    // ── Average over all unique titles ──────────────────────────────────────
    public static double computeAverage(Map<String, Double> totals) {
        return totals.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // ── Separator helper ─────────────────────────────────────────────────────
    private static final int W = 78;
    private static void sep(char ch) {
        System.out.println(String.valueOf(ch).repeat(W));
    }

    // ── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        List<DataRecord> records = null;
        File file;

        // ── 1. File-path input loop ─────────────────────────────────────
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine();
            file = new File(path);

            if (file.exists() && file.isFile()) {
                try {
                    System.out.println("File found. Processing...");
                    records = loadCSV(path);
                    System.out.printf("  [OK] %,d rows loaded from CSV.%n", records.size());
                    break;
                } catch (IOException e) {
                    System.out.println("Invalid file path. Please try again.");
                }
            } else {
                System.out.println("Invalid file path. Please try again.");
            }
        }
        input.close();

        // ── 2. Aggregate ────────────────────────────────────────────────
        System.out.println("Aggregating sales per title...");
        LinkedHashMap<String, Double> totals = aggregateSales(records);
        double average = computeAverage(totals);
        long withSales = totals.values().stream().filter(v -> v > 0).count();

        // ── 3. Header summary ───────────────────────────────────────────
        System.out.println();
        sep('=');
        System.out.println("          VGChartz 2024 — TOTAL SALES PER PRODUCT REPORT");
        sep('=');
        System.out.printf("  Total CSV rows read        : %,d%n", records.size());
        System.out.printf("  Unique game titles         : %,d%n", totals.size());
        System.out.printf("  Titles with sales data     : %,d%n", withSales);
        System.out.printf("  Titles with no sales data  : %,d  (stored as 0.00 M)%n",
                          totals.size() - withSales);
        System.out.printf("  Dataset average (all titles): %.4f M units%n", average);
        sep('=');
        System.out.printf("  %-52s  %12s%n", "GAME TITLE", "TOTAL SALES");
        sep('-');

        // Print ALL unique titles
        int rank = 1;
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            System.out.printf("  %5d. %-52s  %8.2f M%n",
                              rank++, truncate(e.getKey(), 52), e.getValue());
        }
        sep('-');
        System.out.printf("  %-58s  %8.4f M%n", "DATASET AVERAGE", average);
        sep('=');

        // ── 4. Flag low performers ──────────────────────────────────────
        List<Map.Entry<String, Double>> flagged = new ArrayList<>();
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            if (e.getValue() < average) flagged.add(e);
        }

        System.out.println();
        sep('=');
        System.out.println("     ⚠  LOW-PERFORMING PRODUCTS (TOTAL SALES BELOW DATASET AVERAGE)");
        sep('=');
        System.out.printf("  Average threshold : %.4f M units%n", average);
        System.out.printf("  Flagged titles    : %,d of %,d unique games%n",
                          flagged.size(), totals.size());
        sep('-');
        System.out.printf("  %-52s  %12s%n", "GAME TITLE", "TOTAL SALES");
        sep('-');

        int fRank = 1;
        for (Map.Entry<String, Double> e : flagged) {
            System.out.printf("  %5d. %-52s  %8.2f M%n",
                              fRank++, truncate(e.getKey(), 52), e.getValue());
        }
        sep('=');
        System.out.println();
        System.out.println("  Recommendation: Review and consider phasing out flagged titles.");
        System.out.printf("  (%,d titles flagged out of %,d total unique games in dataset)%n",
                          flagged.size(), totals.size());
        sep('=');
        System.out.println();
    }

    // ── Truncate long strings for display ───────────────────────────────────
    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}