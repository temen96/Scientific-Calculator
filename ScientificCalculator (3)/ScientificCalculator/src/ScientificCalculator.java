import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class ScientificCalculator extends JFrame {

    private final JTextField display = new JTextField();
    private final JLabel status = new JLabel("RAD");
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(historyModel);

    private double memory = 0.0;
    private boolean justEvaluated = false;

    private Theme currentTheme = Theme.LIGHT;

    public ScientificCalculator() {
        super("Scientific Calculator (Swing) — History + Converter + Themes");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 680));
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        display.setFont(new Font("Consolas", Font.PLAIN, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBorder(new EmptyBorder(12, 12, 12, 12));
        display.setText("0");

        status.setFont(new Font("SansSerif", Font.BOLD, 14));
        status.setHorizontalAlignment(SwingConstants.LEFT);
        status.setBorder(new EmptyBorder(6, 12, 6, 12));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setBorder(new EmptyBorder(12, 12, 6, 12));
        top.add(status, BorderLayout.WEST);
        top.add(display, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Calculator", buildCalculatorPanel());
        tabs.addTab("Unit Converter", new UnitConverterPanel());

        JPanel historyPanel = buildHistoryPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, historyPanel);
        split.setResizeWeight(0.75);
        split.setDividerLocation(680);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        setupKeyboardShortcuts();
        applyTheme(currentTheme);

        pack();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu theme = new JMenu("Theme");
        ButtonGroup g = new ButtonGroup();
        for (Theme t : Theme.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(t.label);
            if (t == currentTheme) item.setSelected(true);
            item.addActionListener(e -> { currentTheme = t; applyTheme(currentTheme); });
            g.add(item);
            theme.add(item);
        }

        JMenu history = new JMenu("History");
        JMenuItem copy = new JMenuItem("Copy Selected to Display");
        copy.addActionListener(e -> copyHistoryToDisplay());
        JMenuItem clear = new JMenuItem("Clear History");
        clear.addActionListener(e -> historyModel.clear());
        history.add(copy);
        history.add(clear);

        bar.add(theme);
        bar.add(history);
        return bar;
    }

    private JPanel buildCalculatorPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(6, 12, 12, 12));
        root.add(buildButtons(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("History");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFont(new Font("Consolas", Font.PLAIN, 14));
        historyList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) copyHistoryToDisplay();
            }
        });

        JScrollPane scroll = new JScrollPane(historyList);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCopy = new JButton("Use");
        JButton btnClear = new JButton("Clear");
        btnCopy.addActionListener(e -> copyHistoryToDisplay());
        btnClear.addActionListener(e -> historyModel.clear());
        btns.add(btnCopy);
        btns.add(btnClear);

        p.add(title, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    private void copyHistoryToDisplay() {
        int idx = historyList.getSelectedIndex();
        if (idx < 0) return;
        String line = historyModel.get(idx);
        int eq = line.indexOf(" = ");
        if (eq > 0) {
            display.setText(line.substring(0, eq));
            justEvaluated = false;
        }
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(6, 6, 6, 6);
        c.weightx = 1.0;
        c.weighty = 1.0;

        addBtn(panel, c, 0, 0, "DEG/RAD", e -> toggleAngleMode());
        addBtn(panel, c, 1, 0, "MC", e -> memoryClear());
        addBtn(panel, c, 2, 0, "MR", e -> memoryRecall());
        addBtn(panel, c, 3, 0, "M+", e -> memoryAdd());
        addBtn(panel, c, 4, 0, "M-", e -> memorySub());

        addBtn(panel, c, 0, 1, "sin", e -> insertFunc("sin("));
        addBtn(panel, c, 1, 1, "cos", e -> insertFunc("cos("));
        addBtn(panel, c, 2, 1, "tan", e -> insertFunc("tan("));
        addBtn(panel, c, 3, 1, "log", e -> insertFunc("log("));
        addBtn(panel, c, 4, 1, "ln",  e -> insertFunc("ln("));

        addBtn(panel, c, 0, 2, "asin", e -> insertFunc("asin("));
        addBtn(panel, c, 1, 2, "acos", e -> insertFunc("acos("));
        addBtn(panel, c, 2, 2, "atan", e -> insertFunc("atan("));
        addBtn(panel, c, 3, 2, "sqrt", e -> insertFunc("sqrt("));
        addBtn(panel, c, 4, 2, "x^y", e -> append("^"));

        addBtn(panel, c, 0, 3, "π", e -> append("pi"));
        addBtn(panel, c, 1, 3, "e", e -> append("e"));
        addBtn(panel, c, 2, 3, "(", e -> append("("));
        addBtn(panel, c, 3, 3, ")", e -> append(")"));
        addBtn(panel, c, 4, 3, "⌫", e -> backspace());

        addBtn(panel, c, 0, 4, "C", e -> clearAll());
        addBtn(panel, c, 1, 4, "CE", e -> clearEntry());
        addBtn(panel, c, 2, 4, "±", e -> toggleSign());
        addBtn(panel, c, 3, 4, "%", e -> append("%"));
        addBtn(panel, c, 4, 4, "÷", e -> append("/"));

        addBtn(panel, c, 0, 5, "7", e -> append("7"));
        addBtn(panel, c, 1, 5, "8", e -> append("8"));
        addBtn(panel, c, 2, 5, "9", e -> append("9"));
        addBtn(panel, c, 3, 5, "×", e -> append("*"));
        addBtn(panel, c, 4, 5, "inv", e -> insertFunc("inv("));

        addBtn(panel, c, 0, 6, "4", e -> append("4"));
        addBtn(panel, c, 1, 6, "5", e -> append("5"));
        addBtn(panel, c, 2, 6, "6", e -> append("6"));
        addBtn(panel, c, 3, 6, "−", e -> append("-"));
        addBtn(panel, c, 4, 6, "fact", e -> insertFunc("fact("));

        addBtn(panel, c, 0, 7, "1", e -> append("1"));
        addBtn(panel, c, 1, 7, "2", e -> append("2"));
        addBtn(panel, c, 2, 7, "3", e -> append("3"));
        addBtn(panel, c, 3, 7, "+", e -> append("+"));
        addBtn(panel, c, 4, 7, "exp", e -> insertFunc("exp("));

        addBtn(panel, c, 0, 8, "0", e -> append("0"));
        addBtn(panel, c, 1, 8, ".", e -> append("."));
        addBtn(panel, c, 2, 8, "cbrt", e -> insertFunc("cbrt("));
        addBtn(panel, c, 3, 8, "abs", e -> insertFunc("abs("));
        JButton eq = addBtn(panel, c, 4, 8, "=", e -> evaluate());
        eq.setFont(eq.getFont().deriveFont(Font.BOLD, 18f));

        return panel;
    }

    private JButton addBtn(JPanel panel, GridBagConstraints c, int x, int y, String text, ActionListener action) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 16));
        b.addActionListener(action);
        c.gridx = x;
        c.gridy = y;
        panel.add(b, c);
        return b;
    }

    private void toggleAngleMode() {
        if (evaluator.getAngleMode() == ExpressionEvaluator.AngleMode.RAD) {
            evaluator.setAngleMode(ExpressionEvaluator.AngleMode.DEG);
            status.setText("DEG");
        } else {
            evaluator.setAngleMode(ExpressionEvaluator.AngleMode.RAD);
            status.setText("RAD");
        }
    }

    private void append(String s) {
        String cur = display.getText();
        if (cur.equals("0") && isSimpleDigitOrDotOrPiE(s)) cur = "";
        if (justEvaluated && startsNewExpression(s)) cur = "";
        justEvaluated = false;
        display.setText(cur + s);
    }

    private void insertFunc(String s) {
        String cur = display.getText();
        if (cur.equals("0")) cur = "";
        if (justEvaluated) cur = "";
        justEvaluated = false;
        display.setText(cur + s);
    }

    private boolean isSimpleDigitOrDotOrPiE(String s) {
        return s.matches("[0-9]") || s.equals(".") || s.equals("pi") || s.equals("e");
    }

    private boolean startsNewExpression(String s) {
        return s.matches("[0-9]") || s.equals(".") || s.equals("pi") || s.equals("e") || s.equals("(");
    }

    private void backspace() {
        String cur = display.getText();
        if (justEvaluated) { clearAll(); return; }
        if (cur.length() <= 1) { display.setText("0"); return; }
        display.setText(cur.substring(0, cur.length() - 1));
    }

    private void clearAll() {
        display.setText("0");
        justEvaluated = false;
    }

    private void clearEntry() {
        if (justEvaluated) { clearAll(); return; }
        String cur = display.getText();
        if (cur.equals("0")) return;

        int i = cur.length() - 1;
        while (i >= 0 && Character.isWhitespace(cur.charAt(i))) i--;
        if (i < 0) { display.setText("0"); return; }

        if (Character.isLetterOrDigit(cur.charAt(i)) || cur.charAt(i) == '.') {
            while (i >= 0 && (Character.isLetterOrDigit(cur.charAt(i)) || cur.charAt(i) == '.')) i--;
        } else i--;

        String next = cur.substring(0, Math.max(0, i + 1)).trim();
        display.setText(next.isEmpty() ? "0" : next);
    }

    private void toggleSign() {
        String cur = display.getText();
        if (cur.equals("0")) return;

        if (justEvaluated) {
            if (!cur.startsWith("-")) display.setText("-" + cur);
            else display.setText(cur.substring(1));
            return;
        }

        if (cur.matches("-?\\d+(\\.\\d+)?") || cur.matches("-?\\.\\d+")) {
            if (cur.startsWith("-")) display.setText(cur.substring(1));
            else display.setText("-" + cur);
            return;
        }
        display.setText(cur + "*(-1)");
    }

    private void evaluate() {
        String expr = display.getText();
        try {
            double result = evaluator.eval(expr);
            String resStr = format(result);
            display.setText(resStr);
            historyModel.addElement(expr + " = " + resStr);
            historyList.ensureIndexIsVisible(historyModel.size() - 1);
            justEvaluated = true;
        } catch (Exception ex) {
            Toolkit.getDefaultToolkit().beep();
            display.setText("Error");
            justEvaluated = true;
        }
    }

    private String format(double x) {
        DecimalFormat df = new DecimalFormat("0.###############");
        String s = df.format(x);
        if (s.equals("-0")) s = "0";
        return s;
    }

    private double currentValueOrZero() {
        String t = display.getText().trim();
        if (t.equalsIgnoreCase("error")) return 0.0;
        try {
            if (t.matches("[-+]?\\d+(\\.\\d+)?") || t.matches("[-+]?\\.\\d+")) return Double.parseDouble(t);
            return evaluator.eval(t);
        } catch (Exception e) { return 0.0; }
    }

    private void memoryClear() {
        memory = 0.0;
        status.setText(evaluator.getAngleMode().name() + " | M=0");
    }

    private void memoryRecall() {
        display.setText(format(memory));
        justEvaluated = true;
    }

    private void memoryAdd() {
        memory += currentValueOrZero();
        status.setText(evaluator.getAngleMode().name() + " | M=" + format(memory));
    }

    private void memorySub() {
        memory -= currentValueOrZero();
        status.setText(evaluator.getAngleMode().name() + " | M=" + format(memory));
    }

    private void setupKeyboardShortcuts() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (Character.isDigit(ch)) append(Character.toString(ch));
                else if (ch == '.') append(".");
                else if (ch == '+') append("+");
                else if (ch == '-') append("-");
                else if (ch == '*') append("*");
                else if (ch == '/') append("/");
                else if (ch == '%') append("%");
                else if (ch == '^') append("^");
                else if (ch == '(') append("(");
                else if (ch == ')') append(")");
            }
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) evaluate();
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) backspace();
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) clearAll();
            }
        });

        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) { requestFocusInWindow(); }
        });
    }

    private void applyTheme(Theme t) {
        getContentPane().setBackground(t.windowBg);
        themeComponent(this, t);

        historyList.setBackground(t.inputBg);
        historyList.setForeground(t.textFg);
        historyList.setSelectionBackground(t.accent);
        historyList.setSelectionForeground(t.windowBg);

        display.setBackground(t.inputBg);
        display.setForeground(t.textFg);
        status.setForeground(t.textFg);

        repaint();
    }

    private void themeComponent(Component comp, Theme t) {
        if (comp instanceof JMenuBar || comp instanceof JMenu || comp instanceof JMenuItem) {
            comp.setForeground(t.textFg);
            return;
        }

        if (comp instanceof JPanel || comp instanceof JTabbedPane || comp instanceof JSplitPane) {
            comp.setBackground(t.panelBg);
            comp.setForeground(t.textFg);
        } else if (comp instanceof JLabel) {
            comp.setForeground(t.textFg);
        } else if (comp instanceof JTextField || comp instanceof JTextArea) {
            comp.setBackground(t.inputBg);
            comp.setForeground(t.textFg);
        } else if (comp instanceof JButton) {
            comp.setBackground(t.panelBg.darker());
            comp.setForeground(t.textFg);
        } else if (comp instanceof JScrollPane) {
            comp.setBackground(t.panelBg);
        } else {
            comp.setForeground(t.textFg);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                themeComponent(child, t);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new ScientificCalculator().setVisible(true);
        });
    }
}
