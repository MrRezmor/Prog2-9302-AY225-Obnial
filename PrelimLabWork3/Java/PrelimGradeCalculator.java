import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class PrelimGradeCalculator extends JFrame {

    private static final int TOTAL_CLASSES = 5;
    private static final int MAX_ALLOWED_ABSENCES = 3;

    private static final double ATTENDANCE_WEIGHT = 0.40;
    private static final double LAB_WORK_WEIGHT = 0.60;
    private static final double CLASS_STANDING_WEIGHT = 0.70;
    private static final double PRELIM_EXAM_WEIGHT = 0.30;

    private JTextField attendanceField, excusedField, unexcusedField;
    private JTextField lab1Field, lab2Field, lab3Field;
    private JTextArea resultArea;

    private static final Color BG = new Color(18,18,18);
    private static final Color CARD = new Color(30,30,30);
    private static final Color TEXT = new Color(220,220,220);
    private static final Color BORDER = new Color(60,60,60);
    private static final Color PRIMARY = new Color(79,134,247);

    public PrelimGradeCalculator(){

        setTitle("Prelim Grade Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(0,50));

        JLabel title = new JLabel("PRELIM GRADE CALCULATOR");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,20));
        header.add(title);

        // INPUT PANEL
        JPanel input = new JPanel(new GridBagLayout());
        input.setBackground(CARD);
        input.setBorder(BorderFactory.createLineBorder(BORDER));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int y = 0;

        addRow(input,g,y++,"Classes Attended:", attendanceField = field(5));
        addRow(input,g,y++,"Excused Absences:", excusedField = field(5));
        addRow(input,g,y++,"Unexcused Absences:", unexcusedField = field(5));
        addRow(input,g,y++,"Lab Work 1:", lab1Field = field(100));
        addRow(input,g,y++,"Lab Work 2:", lab2Field = field(100));
        addRow(input,g,y++,"Lab Work 3:", lab3Field = field(100));

        JButton compute = new JButton("Compute");
        JButton clear = new JButton("Clear");

        JPanel buttons = new JPanel();
        buttons.setBackground(BG);
        buttons.add(compute);
        buttons.add(clear);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(BG);
        left.add(input,BorderLayout.CENTER);
        left.add(buttons,BorderLayout.SOUTH);

        // RESULT
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(25,25,25));
        resultArea.setForeground(TEXT);
        resultArea.setFont(new Font("Consolas",Font.PLAIN,14));
        resultArea.setMargin(new Insets(12,12,12,12));

        JScrollPane scroll = new JScrollPane(resultArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,scroll);
        split.setResizeWeight(0.4);
        split.setBorder(null);

        main.add(header,BorderLayout.NORTH);
        main.add(split,BorderLayout.CENTER);

        add(main);

        compute.addActionListener(e -> calculate());
        clear.addActionListener(e -> clear());

        pack();
        setMinimumSize(new Dimension(650,450));
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String text, JTextField field){
        g.gridx=0; g.gridy=y; g.weightx=0.4;
        p.add(label(text),g);

        g.gridx=1; g.weightx=0.6;
        p.add(field,g);
    }

    private JLabel label(String t){
        JLabel l = new JLabel(t);
        l.setForeground(TEXT);
        l.setFont(new Font("Segoe UI",Font.BOLD,13));
        return l;
    }

    private JTextField field(int max){
        JTextField tf = new JTextField();
        tf.setBackground(new Color(25,25,25));
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setBorder(BorderFactory.createLineBorder(BORDER));

        ((AbstractDocument)tf.getDocument()).setDocumentFilter(new DocumentFilter(){
            public void replace(FilterBypass fb,int o,int l,String s,AttributeSet a) throws BadLocationException{
                if(s.matches("\\d*")){
                    String n = fb.getDocument().getText(0,fb.getDocument().getLength());
                    n = n.substring(0,o)+s+n.substring(o+l);
                    if(n.isEmpty() || Integer.parseInt(n)<=max)
                        super.replace(fb,o,l,s,a);
                }
            }
        });
        return tf;
    }

    /**
     * Updated calculation logic incorporated from your request.
     */
    private void calculate(){
        try{
            int attended = Integer.parseInt(attendanceField.getText());
            int excused = Integer.parseInt(excusedField.getText());
            int unexcused = Integer.parseInt(unexcusedField.getText());

            int lab1 = Integer.parseInt(lab1Field.getText());
            int lab2 = Integer.parseInt(lab2Field.getText());
            int lab3 = Integer.parseInt(lab3Field.getText());

            // 1. Validate total meetings
            if(attended + excused + unexcused != TOTAL_CLASSES){
                error("Total meetings must equal " + TOTAL_CLASSES);
                return;
            }

            // 2. Check for automatic failure due to absences
            if(unexcused > MAX_ALLOWED_ABSENCES){
                resultArea.setText(
                    "AUTOMATIC FAIL\n" +
                    "Unexcused Absences: " + unexcused + "\n" +
                    "Limit: " + MAX_ALLOWED_ABSENCES
                );
                return;
            }

            // 3. Compute base scores
            int attendanceScore = attended * 20;
            int labAvg = (lab1 + lab2 + lab3) / 3;

            int classStanding = (int)Math.round(
                    attendanceScore * ATTENDANCE_WEIGHT +
                    labAvg * LAB_WORK_WEIGHT
            );

            // 4. Current grade so far (Weighting applied before the exam)
            double currentGrade = classStanding * CLASS_STANDING_WEIGHT;

            StringBuilder r = new StringBuilder();
            r.append("ATTENDANCE SCORE: ").append(attendanceScore).append("\n");
            r.append("LAB WORK AVERAGE: ").append(labAvg).append("\n\n");

            r.append("CLASS STANDING:   ").append(classStanding).append("\n");
            r.append("CURRENT GRADE SO FAR: ")
             .append(String.format("%.2f", currentGrade))
             .append("\n\n");

            // 5. Check if already passed without the exam
            if(currentGrade >= 75){
                r.append("STATUS: ALREADY PASSED 🎉\n");
                r.append("You already met the passing grade even before the prelim exam.");
                resultArea.setText(r.toString());
                return;
            }

            // 6. Compute needed prelim grade to pass
            double neededPass = (75 - currentGrade) / PRELIM_EXAM_WEIGHT;
            int neededPassRounded = (int)Math.ceil(neededPass);

            r.append("PRELIM EXAM REQUIRED\n");
            r.append("--------------------\n");

            if(neededPassRounded <= 100){
                // If needed grade is negative, it means they pass with a 0 on the exam
                int displayGrade = Math.max(0, neededPassRounded);
                r.append("Grade needed to PASS: ")
                 .append(displayGrade)
                 .append("\n\n");

                r.append("STATUS: CAN STILL PASS");
            } else {
                r.append("Grade needed to PASS: MORE THAN 100\n\n");
                r.append("STATUS: IMPOSSIBLE TO PASS");
            }

            resultArea.setText(r.toString());

        }catch(Exception e){
            error("Please complete all fields correctly");
        }
    }

    private void clear(){
        attendanceField.setText("");
        excusedField.setText("");
        unexcusedField.setText("");
        lab1Field.setText("");
        lab2Field.setText("");
        lab3Field.setText("");
        resultArea.setText("");
    }

    private void error(String msg){
        JOptionPane pane = new JOptionPane(msg,JOptionPane.ERROR_MESSAGE);
        JDialog d = pane.createDialog(this,"Error");
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new PrelimGradeCalculator().setVisible(true));
    }
}