import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * AttendanceTracker - Simple and clean design with Light/Dark mode
 * Features: Save/Load attendance records and minimal UI design
 * 
 * @author Your Name
 * @version 4.1 - FIXED
 */
public class AttendanceTracker {
    
    // Declare components
    private JFrame frame;
    private JTextField nameField;
    private JTextField courseField;
    private JTextField timeInField;
    private JTextField eSignatureField;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    // Store attendance records
    private ArrayList<AttendanceRecord> attendanceHistory;
    
    // File to save records
    private static final String SAVE_FILE = "attendance_records.dat";
    
    // Theme colors - Light Mode (default)
    private Color backgroundColor = new Color(250, 250, 250);
    private Color cardBackground = Color.WHITE;
    private Color textColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(100, 100, 100);
    private Color buttonTextColor = Color.WHITE;
    private Color accentColor = new Color(70, 130, 180);
    private Color borderColor = new Color(220, 220, 220);
    private boolean isDarkMode = false;
    
    /**
     * Inner class to store attendance records - FIXED: Made static for proper serialization
     */
    static class AttendanceRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        String course;
        String timeIn;
        String eSignature;
        
        AttendanceRecord(String name, String course, String timeIn, String eSignature) {
            this.name = name;
            this.course = course;
            this.timeIn = timeIn;
            this.eSignature = eSignature;
        }
    }
    
    /**
     * Constructor
     */
    public AttendanceTracker() {
        attendanceHistory = new ArrayList<>();
        loadRecordsFromFile();
        initializeGUI();
    }
    
    /**
     * Initializes the GUI
     */
    private void initializeGUI() {
        frame = new JFrame("Attendance Tracker");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        // Auto-save on close
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveRecordsToFile();
            }
        });
        
        // Create menu bar
        createMenuBar();
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Add tabs
        tabbedPane.addTab("Record Attendance", createFormPanel());
        tabbedPane.addTab("View History", createHistoryPanel());
        
        frame.add(tabbedPane);
        frame.setVisible(true);
        
        // Apply theme
        applyTheme();
        updateHistoryTable();
    }
    
    /**
     * Creates menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem saveItem = new JMenuItem("Save Records");
        saveItem.addActionListener(e -> {
            saveRecordsToFile();
            showMessage("Records saved successfully!", "Saved");
        });
        
        JMenuItem exportItem = new JMenuItem("Export to Text");
        exportItem.addActionListener(e -> exportHistory());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            saveRecordsToFile();
            System.exit(0);
        });
        
        fileMenu.add(saveItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu with theme toggle
        JMenu viewMenu = new JMenu("View");
        
        JMenuItem toggleTheme = new JMenuItem("Toggle Dark Mode");
        toggleTheme.addActionListener(e -> toggleDarkMode());
        
        viewMenu.add(toggleTheme);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
    /**
     * Creates the form panel
     */
    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Attendance Record", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        nameField = new JTextField(25);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(nameField, gbc);
        
        // Course
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel courseLabel = new JLabel("Course/Year");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        courseField = new JTextField(25);
        courseField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(courseField, gbc);
        
        // Time In
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel timeLabel = new JLabel("Time In");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(timeLabel, gbc);
        
        gbc.gridx = 1;
        timeInField = new JTextField(25);
        timeInField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeInField.setEditable(false);
        timeInField.setText(getCurrentDateTime());
        timeInField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(timeInField, gbc);
        
        // E-Signature
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel sigLabel = new JLabel("E-Signature");
        sigLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(sigLabel, gbc);
        
        gbc.gridx = 1;
        eSignatureField = new JTextField(25);
        eSignatureField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        eSignatureField.setEditable(false);
        eSignatureField.setText(generateESignature());
        eSignatureField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(eSignatureField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        
        JButton submitBtn = createStyledButton("Submit", true);
        submitBtn.addActionListener(e -> submitAttendance());
        
        JButton refreshBtn = createStyledButton("Refresh", false);
        refreshBtn.addActionListener(e -> refreshFields());
        
        JButton clearBtn = createStyledButton("Clear", false);
        clearBtn.addActionListener(e -> clearForm());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        
        // Add to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    /**
     * Creates the history panel - FIXED: Better dark mode support and locked columns
     */
    private JPanel createHistoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Attendance History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Table
        String[] columns = {"Name", "Course/Year", "Time In", "E-Signature"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(32);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setShowVerticalLines(false);
        historyTable.setIntercellSpacing(new Dimension(10, 1));
        
        // FIXED: Lock column resizing and set preferred widths
        historyTable.getTableHeader().setResizingAllowed(false);
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Set preferred column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150);  // Name
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Course
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Time
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(280);  // Signature
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JButton deleteBtn = createStyledButton("Delete Selected", false);
        deleteBtn.addActionListener(e -> deleteSelectedRecord());
        
        JButton clearBtn = createStyledButton("Clear All", false);
        clearBtn.addActionListener(e -> clearHistory());
        
        JButton saveBtn = createStyledButton("Save to File", true);
        saveBtn.addActionListener(e -> {
            saveRecordsToFile();
            showMessage("All records saved!", "Saved");
        });
        
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(saveBtn);
        
        // Add components
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    /**
     * Creates a styled button
     */
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(140, 38));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isPrimary) {
            button.setBackground(accentColor);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(buttonColor);
            button.setForeground(buttonTextColor);
        }
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(accentColor.brighter());
                } else {
                    button.setBackground(buttonColor.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(accentColor);
                } else {
                    button.setBackground(buttonColor);
                }
            }
        });
        
        return button;
    }
    
    /**
     * Gets current date and time
     */
    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    /**
     * Generates unique signature
     */
    private String generateESignature() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Refreshes time and signature
     */
    private void refreshFields() {
        timeInField.setText(getCurrentDateTime());
        eSignatureField.setText(generateESignature());
    }
    
    /**
     * Clears form
     */
    private void clearForm() {
        nameField.setText("");
        courseField.setText("");
        refreshFields();
    }
    
    /**
     * Submits attendance
     */
    private void submitAttendance() {
        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        
        if (name.isEmpty() || course.isEmpty()) {
            showError("Please fill in all required fields!");
            return;
        }
        
        AttendanceRecord record = new AttendanceRecord(
            name, course, timeInField.getText(), eSignatureField.getText()
        );
        
        attendanceHistory.add(record);
        saveRecordsToFile();
        updateHistoryTable();
        
        showMessage(
            String.format("Attendance recorded for %s\n\nTotal Records: %d", 
                name, attendanceHistory.size()),
            "Success"
        );
        
        clearForm();
    }
    
    /**
     * Updates history table
     */
    private void updateHistoryTable() {
        tableModel.setRowCount(0);
        for (AttendanceRecord record : attendanceHistory) {
            Object[] row = {record.name, record.course, record.timeIn, record.eSignature};
            tableModel.addRow(row);
        }
    }
    
    /**
     * Deletes selected record
     */
    private void deleteSelectedRecord() {
        int selectedRow = historyTable.getSelectedRow();
        
        if (selectedRow == -1) {
            showError("Please select a record to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(frame,
            "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            attendanceHistory.remove(selectedRow);
            saveRecordsToFile();
            updateHistoryTable();
            showMessage("Record deleted.", "Deleted");
        }
    }
    
    /**
     * Clears all history
     */
    private void clearHistory() {
        if (attendanceHistory.isEmpty()) {
            showMessage("History is already empty.", "Info");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(frame,
            "Delete all " + attendanceHistory.size() + " records?",
            "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            attendanceHistory.clear();
            saveRecordsToFile();
            updateHistoryTable();
            showMessage("All records deleted.", "Cleared");
        }
    }
    
    /**
     * Saves records to file - FIXED: Now works properly
     */
    private void saveRecordsToFile() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SAVE_FILE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(attendanceHistory);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            showError("Error saving records: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads records from file
     */
    @SuppressWarnings("unchecked")
    private void loadRecordsFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        
        try {
            FileInputStream fileIn = new FileInputStream(SAVE_FILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            attendanceHistory = (ArrayList<AttendanceRecord>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            showError("Error loading records: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Exports history
     */
    private void exportHistory() {
        if (attendanceHistory.isEmpty()) {
            showError("No records to export!");
            return;
        }
        
        StringBuilder export = new StringBuilder();
        export.append("ATTENDANCE HISTORY REPORT\n");
        export.append("=========================\n\n");
        export.append("Total Records: ").append(attendanceHistory.size()).append("\n");
        export.append("Export Date: ").append(getCurrentDateTime()).append("\n\n");
        
        for (int i = 0; i < attendanceHistory.size(); i++) {
            AttendanceRecord r = attendanceHistory.get(i);
            export.append(String.format("Record #%d\n", i + 1));
            export.append(String.format("Name: %s\n", r.name));
            export.append(String.format("Course: %s\n", r.course));
            export.append(String.format("Time: %s\n", r.timeIn));
            export.append(String.format("Signature: %s\n\n", r.eSignature));
            export.append("-------------------------\n\n");
        }
        
        JTextArea textArea = new JTextArea(export.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        
        JOptionPane.showMessageDialog(frame, scrollPane, "Export Preview", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Toggles between light and dark mode
     */
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        
        if (isDarkMode) {
            // Dark mode colors - FIXED: High contrast for readability
            backgroundColor = new Color(32, 32, 32);
            cardBackground = new Color(45, 45, 45);
            textColor = new Color(245, 245, 245);
            buttonColor = new Color(70, 70, 70);
            buttonTextColor = new Color(255, 255, 255);
            accentColor = new Color(100, 170, 220);
            borderColor = new Color(80, 80, 80);
        } else {
            // Light mode colors
            backgroundColor = new Color(250, 250, 250);
            cardBackground = Color.WHITE;
            textColor = new Color(50, 50, 50);
            buttonColor = new Color(100, 100, 100);
            buttonTextColor = Color.WHITE;
            accentColor = new Color(70, 130, 180);
            borderColor = new Color(220, 220, 220);
        }
        
        applyTheme();
        showMessage(isDarkMode ? "Dark mode enabled" : "Light mode enabled", "Theme Changed");
    }
    
    /**
     * Applies theme to all components - FIXED: Better table rendering
     */
    private void applyTheme() {
        frame.getContentPane().setBackground(backgroundColor);
        applyThemeToComponents(frame.getContentPane());
        
        // FIXED: Update table colors for better dark mode readability
        if (historyTable != null) {
            historyTable.setBackground(cardBackground);
            historyTable.setForeground(textColor);
            historyTable.setGridColor(borderColor);
            historyTable.setSelectionBackground(accentColor);
            historyTable.setSelectionForeground(Color.WHITE);
            
            // FIXED: Set table header with high contrast
            historyTable.getTableHeader().setBackground(isDarkMode ? new Color(55, 55, 55) : new Color(240, 240, 240));
            historyTable.getTableHeader().setForeground(textColor);
            historyTable.getTableHeader().setOpaque(true);
            
            // FIXED: Custom cell renderer for better text visibility
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        c.setBackground(cardBackground);
                        c.setForeground(textColor);
                    }
                    return c;
                }
            };
            
            for (int i = 0; i < historyTable.getColumnCount(); i++) {
                historyTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }
        
        // FIXED: Update menu bar with proper colors
        JMenuBar menuBar = frame.getJMenuBar();
        if (menuBar != null) {
            menuBar.setBackground(isDarkMode ? new Color(40, 40, 40) : new Color(245, 245, 245));
            menuBar.setOpaque(true);
            menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
            
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                JMenu menu = menuBar.getMenu(i);
                menu.setBackground(isDarkMode ? new Color(40, 40, 40) : new Color(245, 245, 245));
                menu.setForeground(textColor);
                menu.setOpaque(true);
                
                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null) {
                        item.setBackground(isDarkMode ? new Color(50, 50, 50) : Color.WHITE);
                        item.setForeground(textColor);
                        item.setOpaque(true);
                    }
                }
            }
        }
        
        frame.repaint();
    }
    
    /**
     * Applies theme recursively
     */
    private void applyThemeToComponents(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                // Buttons are styled separately
            } else if (comp instanceof JPanel) {
                comp.setBackground(backgroundColor);
                applyThemeToComponents((Container) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(textColor);
            } else if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setBackground(cardBackground);
                field.setForeground(textColor);
                field.setCaretColor(textColor);
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(cardBackground);
                ((JScrollPane) comp).getViewport().setBackground(cardBackground);
                ((JScrollPane) comp).setBorder(BorderFactory.createLineBorder(borderColor, 1));
            } else if (comp instanceof Container) {
                applyThemeToComponents((Container) comp);
            }
        }
    }
    
    /**
     * Shows info message
     */
    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows about dialog
     */
    private void showAbout() {
        JOptionPane.showMessageDialog(frame,
            "Attendance Tracker v4.1 - FIXED\n\n" +
            "Simple and clean attendance tracking\n" +
            "with Light/Dark mode support.\n\n" +
            "Features:\n" +
            "• Auto-save to file (FIXED)\n" +
            "• Light and Dark themes (IMPROVED)\n" +
            "• Clean, minimal design\n" +
            "• Locked table columns\n" +
            "• Better dark mode readability\n\n" +
            "File: " + SAVE_FILE,
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AttendanceTracker();
        });
    }
}