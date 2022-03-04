package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class LabelledCheckBox extends JComponent {

    private static final long serialVersionUID = 1L;
    private JLabel label;
    private JCheckBox box;

    public LabelledCheckBox(String text, boolean value) {
        this.label = new JLabel(text);
        this.box = new JCheckBox();
        this.box.setSelected(value);

        this.setLayout(new BorderLayout());
        this.add(label, BorderLayout.WEST);
        this.add(box, BorderLayout.EAST);
        this.box.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),new EtchedBorder()));
    }

    public boolean getValue() {
        boolean value = box.isSelected();
        return value;
    }
}