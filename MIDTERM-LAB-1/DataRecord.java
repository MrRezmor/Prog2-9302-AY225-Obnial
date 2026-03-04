/**
 * DataRecord.java
 * Represents one row from the vgchartz-2024.csv dataset.
 * All 14 columns are stored; missing numeric fields default to 0.0.
 */
public class DataRecord {

    // ── All 14 CSV columns ───────────────────────────────────────────────────
    private String img;
    private String title;
    private String console;
    private String genre;
    private String publisher;
    private String developer;
    private double criticScore;   // empty → 0.0
    private double totalSales;    // millions; empty → 0.0
    private double naSales;
    private double jpSales;
    private double palSales;
    private double otherSales;
    private String releaseDate;
    private String lastUpdate;

    // ── Constructor ──────────────────────────────────────────────────────────
    public DataRecord(String img, String title, String console, String genre,
                      String publisher, String developer,
                      double criticScore, double totalSales,
                      double naSales, double jpSales,
                      double palSales, double otherSales,
                      String releaseDate, String lastUpdate) {
        this.img         = img;
        this.title       = title;
        this.console     = console;
        this.genre       = genre;
        this.publisher   = publisher;
        this.developer   = developer;
        this.criticScore = criticScore;
        this.totalSales  = totalSales;
        this.naSales     = naSales;
        this.jpSales     = jpSales;
        this.palSales    = palSales;
        this.otherSales  = otherSales;
        this.releaseDate = releaseDate;
        this.lastUpdate  = lastUpdate;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getImg()         { return img; }
    public String getTitle()       { return title; }
    public String getConsole()     { return console; }
    public String getGenre()       { return genre; }
    public String getPublisher()   { return publisher; }
    public String getDeveloper()   { return developer; }
    public double getCriticScore() { return criticScore; }
    public double getTotalSales()  { return totalSales; }
    public double getNaSales()     { return naSales; }
    public double getJpSales()     { return jpSales; }
    public double getPalSales()    { return palSales; }
    public double getOtherSales()  { return otherSales; }
    public String getReleaseDate() { return releaseDate; }
    public String getLastUpdate()  { return lastUpdate; }

    // ── Safe numeric parser ──────────────────────────────────────────────────
    public static double parseDouble(String s) {
        if (s == null) return 0.0;
        s = s.trim();
        if (s.isEmpty() || s.equalsIgnoreCase("N/A")) return 0.0;
        try   { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    @Override
    public String toString() {
        return String.format("%-52s | %-6s | %8.2f M", title, console, totalSales);
    }
}