package main;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serial;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Maria Chli
 * @version 08-11-2009
 * @author James J Kerr
 * @version 01-01-2022
 */
public class LabelledTextArea extends JComponent {

    @Serial
    private static final long serialVersionUID = 1L;
    private JLabel label;
    private JTextField text;

    public LabelledTextArea(String text, String value) {
        this.label = new JLabel(text);
        this.text = new JTextField(value);
        this.setLayout(new BorderLayout());
        this.add(label, BorderLayout.WEST);
        this.add(this.text, BorderLayout.EAST);
        this.text.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),new EtchedBorder()));
    }

//    public double getValue() {
//        return Double.parseDouble(text.getText().trim());
//    }
    public String getValue() {
        return text.getText().trim();
    }
    public void setValue(String str) {
        text.setText(str);
    }
}