import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class UnitConverterPanel extends JPanel {

    private final JComboBox<String> category = new JComboBox<>(new String[]{"Length", "Weight", "Temperature"});
    private final JComboBox<String> fromUnit = new JComboBox<>();
    private final JComboBox<String> toUnit = new JComboBox<>();
    private final JTextField input = new JTextField();
    private final JTextField output = new JTextField();
    private final JButton swap = new JButton("Swap");
    private final JButton convert = new JButton("Convert");

    private final DecimalFormat df = new DecimalFormat("0.###############");

    public UnitConverterPanel() {
        super(new GridBagLayout());
        setBorder(new EmptyBorder(14, 14, 14, 14));
        output.setEditable(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int row = 0;
        c.gridx = 0; c.gridy = row; add(new JLabel("Category"), c);
        c.gridx = 1; c.gridy = row; add(category, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("From"), c);
        c.gridx = 1; c.gridy = row; add(fromUnit, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("To"), c);
        c.gridx = 1; c.gridy = row; add(toUnit, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Value"), c);
        c.gridx = 1; c.gridy = row; add(input, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Result"), c);
        c.gridx = 1; c.gridy = row; add(output, c); row++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.add(swap);
        btns.add(convert);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(btns, c);

        category.addActionListener(e -> refreshUnits());
        swap.addActionListener(e -> swapUnits());
        convert.addActionListener(e -> doConvert());
        input.addActionListener(e -> doConvert());

        refreshUnits();
    }

    private void refreshUnits() {
        fromUnit.removeAllItems();
        toUnit.removeAllItems();
        for (String u : unitsForCategory(getCategory())) {
            fromUnit.addItem(u);
            toUnit.addItem(u);
        }
        if (toUnit.getItemCount() > 1) toUnit.setSelectedIndex(1);
    }

    private void swapUnits() {
        Object a = fromUnit.getSelectedItem();
        Object b = toUnit.getSelectedItem();
        fromUnit.setSelectedItem(b);
        toUnit.setSelectedItem(a);
        doConvert();
    }

    private String getCategory() {
        return (String) category.getSelectedItem();
    }

    private void doConvert() {
        try {
            double x = Double.parseDouble(input.getText().trim());
            String cat = getCategory();
            String from = (String) fromUnit.getSelectedItem();
            String to = (String) toUnit.getSelectedItem();
            double y = convert(cat, from, to, x);
            output.setText(df.format(y));
        } catch (Exception ex) {
            Toolkit.getDefaultToolkit().beep();
            output.setText("Error");
        }
    }

    private String[] unitsForCategory(String cat) {
        switch (cat) {
            case "Length": return new String[]{"m", "cm", "mm", "km", "inch", "ft", "yd", "mile"};
            case "Weight": return new String[]{"kg", "g", "mg", "lb", "oz"};
            case "Temperature": return new String[]{"C", "F", "K"};
            default: return new String[]{};
        }
    }

    private double convert(String cat, String from, String to, double value) {
        if (from.equals(to)) return value;
        switch (cat) {
            case "Length": return lengthConvert(from, to, value);
            case "Weight": return weightConvert(from, to, value);
            case "Temperature": return tempConvert(from, to, value);
            default: throw new IllegalArgumentException("Unknown category");
        }
    }

    private double lengthConvert(String from, String to, double v) {
        Map<String, Double> toMeters = new LinkedHashMap<>();
        toMeters.put("m", 1.0);
        toMeters.put("cm", 0.01);
        toMeters.put("mm", 0.001);
        toMeters.put("km", 1000.0);
        toMeters.put("inch", 0.0254);
        toMeters.put("ft", 0.3048);
        toMeters.put("yd", 0.9144);
        toMeters.put("mile", 1609.344);
        double meters = v * toMeters.get(from);
        return meters / toMeters.get(to);
    }

    private double weightConvert(String from, String to, double v) {
        Map<String, Double> toKg = new LinkedHashMap<>();
        toKg.put("kg", 1.0);
        toKg.put("g", 0.001);
        toKg.put("mg", 0.000001);
        toKg.put("lb", 0.45359237);
        toKg.put("oz", 0.028349523125);
        double kg = v * toKg.get(from);
        return kg / toKg.get(to);
    }

    private double tempConvert(String from, String to, double v) {
        double c;
        switch (from) {
            case "C": c = v; break;
            case "F": c = (v - 32.0) * 5.0 / 9.0; break;
            case "K": c = v - 273.15; break;
            default: throw new IllegalArgumentException("Bad unit");
        }
        switch (to) {
            case "C": return c;
            case "F": return c * 9.0 / 5.0 + 32.0;
            case "K": return c + 273.15;
            default: throw new IllegalArgumentException("Bad unit");
        }
    }
}
