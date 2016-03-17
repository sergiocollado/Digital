package de.neemann.digital.core.basic;

import de.neemann.digital.core.*;
import de.neemann.digital.gui.draw.shapes.GenericShape;

/**
 * @author hneemann
 */
public class Not extends Node implements Part {

    private final ObservableValue output;
    private ObservableValue input;
    private long value;

    public Not(int bits) {
        output = new ObservableValue("out", bits);
    }

    public static PartDescription createFactory(int bits) {
        return new PartDescription(new GenericShape("", 1).invert(true), new PartFactory() {
            @Override
            public Part create() {
                return new Not(bits);
            }
        }, "in");
    }

    @Override
    public void readInputs() throws NodeException {
        value = input.getValue();
    }

    @Override
    public void writeOutputs() throws NodeException {
        output.setValue(~value);
    }

    public ObservableValue getOutput() {
        return output;
    }

    @Override
    public void setInputs(ObservableValue... inputs) throws NodeException {
        input = inputs[0];
        input.addListener(this);
    }

    @Override
    public ObservableValue[] getOutputs() {
        return new ObservableValue[]{output};
    }

    @Override
    public void registerNodes(Model model) {
        model.add(this);
    }
}