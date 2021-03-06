package de.neemann.digital.gui.components;

import de.neemann.digital.core.*;
import de.neemann.digital.core.memory.RAMInterface;
import de.neemann.digital.gui.sync.Sync;
import de.neemann.digital.lang.Lang;
import de.neemann.gui.ToolTipAction;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hneemann
 */
public class ProbeDialog extends JDialog implements ModelStateObserver {

    private final ModelEvent type;
    private final SignalTableModel tableModel;
    private boolean tableUpdateEnable = true;

    /**
     * Creates a new instance
     *
     * @param owner     the owner
     * @param model     the model to run
     * @param type      the event type which fires a dialog repaint
     * @param ordering  the names list used to order the measurement values
     * @param modelSync used to access the running model
     */
    public ProbeDialog(Frame owner, Model model, ModelEvent type, List<String> ordering, Sync modelSync) {
        super(owner, Lang.get("win_measures"), false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.type = type;

        ArrayList<Signal> signals = model.getSignalsCopy();
        new OrderMerger<String, Signal>(ordering) {
            @Override
            public boolean equals(Signal a, String b) {
                return a.getName().equals(b);
            }
        }.order(signals);

        tableModel = new SignalTableModel(signals);
        JTable list = new JTable(tableModel);
        list.setRowHeight(list.getFont().getSize() * 6 / 5);
        getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
        setAlwaysOnTop(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                modelSync.access(() -> model.addObserver(ProbeDialog.this));
            }

            @Override
            public void windowClosed(WindowEvent e) {
                modelSync.access(() -> model.removeObserver(ProbeDialog.this));
            }
        });

        List<Node> memoryList = model.findNode(n -> n instanceof RAMInterface);
        if (memoryList.size() > 0) {
            JMenuBar bar = new JMenuBar();
            final JMenu memory = new JMenu(Lang.get("menu_probe_memory"));
            memory.setToolTipText(Lang.get("menu_probe_memory_tt"));
            for (Node n : memoryList) {
                if (n instanceof RAMInterface) {
                    RAMInterface ram = (RAMInterface) n;
                    String name = ram.getLabel();
                    if (name == null || name.length() == 0)
                        name = ram.getClass().getSimpleName();
                    memory.add(new JMenuItem(new ToolTipAction(name) {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            new DataEditor(ProbeDialog.this,
                                    ram.getMemory(),
                                    ram.getSize(),
                                    ram.getBits(),
                                    true,
                                    modelSync).showDialog();
                        }
                    }));
                }
            }
            bar.add(memory);
            setJMenuBar(bar);
        }

        setPreferredSize(new Dimension(150, getPreferredSize().height));

        pack();
        setLocationRelativeTo(owner);
    }

    @Override
    public void handleEvent(ModelEvent event) {
        if (event == type || event == ModelEvent.MANUALCHANGE) {
            if (tableUpdateEnable)
                SwingUtilities.invokeLater(tableModel::fireChanged);
        }
        switch (event) {
            case FASTRUN:
                tableUpdateEnable = false;
                break;
            case BREAK:
            case STOPPED:
                tableUpdateEnable = true;
                SwingUtilities.invokeLater(tableModel::fireChanged);
                break;
        }
    }

    private static class SignalTableModel implements TableModel {
        private final ArrayList<Signal> signals;
        private ArrayList<TableModelListener> listeners = new ArrayList<>();

        SignalTableModel(ArrayList<Signal> signals) {
            this.signals = signals;
        }

        @Override
        public int getRowCount() {
            return signals.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) return Lang.get("key_Label");
            else return Lang.get("key_Value");
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) return signals.get(rowIndex).getName();
            else return signals.get(rowIndex).getValue().getValueString();
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        public void fireChanged() {
            TableModelEvent e = new TableModelEvent(this, 0, signals.size() - 1);
            for (TableModelListener l : listeners)
                l.tableChanged(e);
        }
    }
}
