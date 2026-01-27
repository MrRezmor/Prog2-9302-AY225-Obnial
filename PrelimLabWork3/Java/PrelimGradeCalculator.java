import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class PrelimGradeCalculator extends JFrame {
    // Grading constants
    private static final double ATTENDANCE_WEIGHT = 0.40;
    private static final double LAB_WORK_WEIGHT = 0.60;
    private static final double CLASS_STANDING_WEIGHT = 0.70;
    private static final double PRELIM_EXAM_WEIGHT = 0.30;
    private static final double PASSING_GRADE = 75.0;
    private static final double EXCELLENT_GRADE = 100.0;
    private static final int TOTAL_CLASSES = 20;  // Attendance limit

    // Colors
    private static final Color PRIMARY_COLOR = new Color(25, 42, 86);
    private static final Color ACCENT_COLOR = new Color(188, 143, 143);
    private static final Color SUCCESS_COLOR = new Color(72, 126, 176);
    private static final Color TEXT_COLOR = new Color(45, 45, 45);
    private static final Color LIGHT_BG = new Color(252, 250, 247);

    // Inputs
    private JTextField attendanceField, labWork1Field, labWork2Field, labWork3Field;

    // Outputs
    private JTextArea resultArea;
    private JButton computeButton, clearButton;

    public PrelimGradeCalculator() {
        setTitle("Prelim Grade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(900, 800); // bigger window
        setMinimumSize(new Dimension(800, 700));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        // ===== HEADER =====
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(40, 60, 100));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 25));

        JLabel titleLabel = new JLabel("PRELIM GRADE CALCULATOR");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        JLabel subtitleLabel = new JLabel("Calculate Your Prelim Score");
        subtitleLabel.setFont(new Font("Georgia", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(200, 200, 220));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setOpaque(false);
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(Box.createVerticalStrut(5));
        titleTextPanel.add(subtitleLabel);

        headerPanel.add(titleTextPanel);

        // ===== CONTENT =====
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== INPUTS =====
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Attendance
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        JLabel attendanceLabel = new JLabel("Number of Attendances (0-20):");
        attendanceLabel.setFont(labelFont); attendanceLabel.setForeground(TEXT_COLOR);
        inputPanel.add(attendanceLabel, gbc);
        gbc.gridx = 1;
        attendanceField = createStyledTextField(inputFont);
        inputPanel.add(attendanceField, gbc);

        // Lab 1
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lab1Label = new JLabel("Lab Work 1 Grade (0-100):");
        lab1Label.setFont(labelFont); lab1Label.setForeground(TEXT_COLOR);
        inputPanel.add(lab1Label, gbc);
        gbc.gridx = 1;
        labWork1Field = createStyledTextField(inputFont);
        inputPanel.add(labWork1Field, gbc);

        // Lab 2
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lab2Label = new JLabel("Lab Work 2 Grade (0-100):");
        lab2Label.setFont(labelFont); lab2Label.setForeground(TEXT_COLOR);
        inputPanel.add(lab2Label, gbc);
        gbc.gridx = 1;
        labWork2Field = createStyledTextField(inputFont);
        inputPanel.add(labWork2Field, gbc);

        // Lab 3
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lab3Label = new JLabel("Lab Work 3 Grade (0-100):");
        lab3Label.setFont(labelFont); lab3Label.setForeground(TEXT_COLOR);
        inputPanel.add(lab3Label, gbc);
        gbc.gridx = 1;
        labWork3Field = createStyledTextField(inputFont);
        inputPanel.add(labWork3Field, gbc);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(LIGHT_BG);

        computeButton = new JButton("Compute Grades");
        computeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        computeButton.setBackground(SUCCESS_COLOR);
        computeButton.setForeground(Color.WHITE);
        computeButton.setFocusPainted(false);
        computeButton.setPreferredSize(new Dimension(220, 50));
        computeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButtonHoverEffect(computeButton, SUCCESS_COLOR, new Color(60, 110, 160));

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        clearButton.setBackground(ACCENT_COLOR);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setPreferredSize(new Dimension(140, 50));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButtonHoverEffect(clearButton, ACCENT_COLOR, new Color(170, 130, 130));

        buttonPanel.add(computeButton);
        buttonPanel.add(clearButton);

        JPanel topSection = new JPanel(new BorderLayout(0, 15));
        topSection.setBackground(LIGHT_BG);
        topSection.add(inputPanel, BorderLayout.NORTH);
        topSection.add(buttonPanel, BorderLayout.CENTER);

        contentPanel.add(topSection, BorderLayout.NORTH);

        // ===== RESULT PANEL =====
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(Color.WHITE);
        Border outerBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, ACCENT_COLOR);
        Border innerBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        JLabel resultTitleLabel = new JLabel("Computation Results", JLabel.CENTER);
        resultTitleLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        resultTitleLabel.setForeground(PRIMARY_COLOR);
        resultTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        resultPanel.add(resultTitleLabel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.BOLD, 18)); // bigger font
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 252));
        resultArea.setForeground(TEXT_COLOR);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        // Set preferred size so it expands and shows all content
        resultArea.setRows(20);
        resultArea.setColumns(60);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        resultPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(resultPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // ===== ACTIONS =====
        computeButton.addActionListener(e -> calculateGrade());
        clearButton.addActionListener(e -> clearFields());

        KeyAdapter enterKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) calculateGrade();
            }
        };
        attendanceField.addKeyListener(enterKeyListener);
        labWork1Field.addKeyListener(enterKeyListener);
        labWork2Field.addKeyListener(enterKeyListener);
        labWork3Field.addKeyListener(enterKeyListener);
    }

    private JTextField createStyledTextField(Font font) {
        JTextField tf = new JTextField(20);
        tf.setFont(font);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
                        BorderFactory.createEmptyBorder(7, 9, 7, 9)
                ));
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
        });
        return tf;
    }

    private void addButtonHoverEffect(JButton button, Color normal, Color hover) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            public void mouseExited(MouseEvent e) { button.setBackground(normal); }
        });
    }

    private void calculateGrade() {
        try {
            int attendances = Integer.parseInt(attendanceField.getText().trim());
            double lab1 = Double.parseDouble(labWork1Field.getText().trim());
            double lab2 = Double.parseDouble(labWork2Field.getText().trim());
            double lab3 = Double.parseDouble(labWork3Field.getText().trim());

            if (attendances < 0 || attendances > TOTAL_CLASSES) {
                showStyledError("Invalid Input", "Number of attendances must be 0-" + TOTAL_CLASSES);
                return;
            }
            if (lab1 < 0 || lab1 > 100 || lab2 < 0 || lab2 > 100 || lab3 < 0 || lab3 > 100) {
                showStyledError("Invalid Input", "Lab grades must be 0-100");
                return;
            }

            double labAvg = (lab1 + lab2 + lab3) / 3.0;
            double attendanceScore = (attendances / (double) TOTAL_CLASSES) * 100.0;
            double classStanding = (attendanceScore * ATTENDANCE_WEIGHT + labAvg * LAB_WORK_WEIGHT);

            double requiredPass = (PASSING_GRADE - (classStanding * CLASS_STANDING_WEIGHT)) / PRELIM_EXAM_WEIGHT;
            double requiredExcellent = (EXCELLENT_GRADE - (classStanding * CLASS_STANDING_WEIGHT)) / PRELIM_EXAM_WEIGHT;

            displayResults(attendanceScore, attendances, lab1, lab2, lab3, labAvg, classStanding, requiredPass, requiredExcellent);

        } catch (NumberFormatException e) {
            showStyledError("Invalid Input", "Please enter numeric values in all fields");
        }
    }

    private void displayResults(double attendanceScore, int attendances, double lab1, double lab2, double lab3,
                                double labAvg, double classStanding, double pass, double excellent) {
        StringBuilder result = new StringBuilder();
        result.append("═══════════════════════════════════════════════════════════════\n");
        result.append("                     CURRENT ACADEMIC STANDING\n");
        result.append("═══════════════════════════════════════════════════════════════\n\n");
        result.append(String.format("  Attendance Score        : %.2f%% (%d/%d classes)\n", attendanceScore, attendances, TOTAL_CLASSES));
        result.append(String.format("  Lab Work 1              : %.2f\n", lab1));
        result.append(String.format("  Lab Work 2              : %.2f\n", lab2));
        result.append(String.format("  Lab Work 3              : %.2f\n", lab3));
        result.append(String.format("  Lab Work Average        : %.2f\n", labAvg));
        result.append(String.format("  Class Standing          : %.2f\n\n", classStanding));
        result.append("═══════════════════════════════════════════════════════════════\n");
        result.append("                REQUIRED PRELIM EXAMINATION SCORES\n");
        result.append("═══════════════════════════════════════════════════════════════\n\n");

        result.append(String.format("  ▸ To PASS (75%%)         : %.2f\n\n", pass));
        if (pass < 0) result.append("    ✓ Already passed!\n");
        else if (pass <= 100) result.append(String.format("    → You need %.2f on the Prelim Exam\n", pass));
        else result.append("    ✗ Required score exceeds 100%\n");

        result.append(String.format("\n  ▸ To achieve EXCELLENT (100%%) : %.2f\n\n", excellent));
        if (excellent < 0) result.append("    ★ Already excellent!\n");
        else if (excellent <= 100) result.append(String.format("    → You need %.2f on the Prelim Exam\n", excellent));
        else result.append("    ✗ Impossible to achieve 100%\n");

        resultArea.setText(result.toString());
        resultArea.setCaretPosition(0);
    }

    private void clearFields() {
        attendanceField.setText("");
        labWork1Field.setText("");
        labWork2Field.setText("");
        labWork3Field.setText("");
        resultArea.setText("");
        attendanceField.requestFocus();
    }

    private void showStyledError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new PrelimGradeCalculator().setVisible(true));
    }
}
