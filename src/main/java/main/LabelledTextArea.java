package main;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class LabelledTextArea extends JComponent {

    private static final long serialVersionUID = 1L;
    private JLabel label;
    private JTextArea text;

    public LabelledTextArea(String text, String value) {
        this.label = new JLabel(text);
        this.text = new JTextArea(value);
        this.setLayout(new BorderLayout());
        this.add(label, BorderLayout.WEST);
        this.add(this.text, BorderLayout.EAST);
        this.text.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),new EtchedBorder()));
    }

    public double getValue() {
        double d = Double.parseDouble(text.getText().trim());
        return d;
    }

    public void setValue(String str) {
        text.setText(str);
    }
}