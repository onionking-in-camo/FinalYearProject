package main;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;


/**
 * @author Maria Chli
 * @version 08-11-2009
 * @author James J Kerr
 * @version 01-01-2022
 */
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

    public void addActionListener(ActionListener al) {
        box.addActionListener(al);
    }
}