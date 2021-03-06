package de.neemann.digital.gui.components.graphics;

import de.neemann.digital.core.memory.DataField;
import de.neemann.digital.lang.Lang;

import javax.swing.*;

/**
 * The dialog used to show the graphics
 *
 * @author hneemann
 */
public class GraphicDialog extends JDialog {
    private final GraphicComponent graphicComponent;

    /**
     * Creates a new instance of the given size
     *
     * @param width  width in pixel
     * @param height height in pixel
     */
    public GraphicDialog(int width, int height) {
        super((JFrame) null, Lang.get("elem_GraphicCard"), false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        graphicComponent = new GraphicComponent(width, height);
        getContentPane().add(graphicComponent);
        pack();

        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Updates the graphics data
     *
     * @param memory the raw data to use
     * @param bank   the bank to show
     */
    public void updateGraphic(DataField memory, boolean bank) {
        graphicComponent.updateGraphic(memory.getData(), bank);
    }
}
