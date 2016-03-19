package de.neemann.digital.gui.components;

import de.neemann.digital.core.part.AttributeKey;
import de.neemann.digital.core.part.PartAttributes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * @author hneemann
 */
public class AttributeDialog extends JDialog {

    public AttributeDialog(Point pos, ArrayList<AttributeKey> list, PartAttributes partAttributes) {
        super((Frame) null, "Attributes", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTable table = new JTable(new AttributeTableModel(list, partAttributes));
        getContentPane().add(table);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close(table);
            }
        });

        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "MyEnter");
        table.getActionMap().put("MyEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                close(table);
            }
        });

        pack();
        setLocation(pos.x, pos.y);
    }

    private void close(JTable table) {
        if (table.isEditing()) {
            if (table.getCellEditor().stopCellEditing()) {
                dispose();
            }
        } else
            dispose();
    }


    public void showDialog() {
        setVisible(true);
    }
}