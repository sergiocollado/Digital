package de.neemann.digital.core.switching;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.draw.elements.PinException;
import de.neemann.digital.lang.Lang;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * Transmission Gate
 * Created by hneemann on 17.05.17.
 */
public class TransGate extends Node implements Element {
    /**
     * The transmission gate description
     */
    public static final ElementTypeDescription DESCRIPTION = new ElementTypeDescription(TransGate.class, input("S"), input("~S"))
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS);

    private final Switch aSwitch;
    private ObservableValue s;
    private ObservableValue ns;
    private boolean closed;

    /**
     * Create a new instance
     *
     * @param attr the attributes
     */
    public TransGate(ElementAttributes attr) {
        aSwitch = new Switch(attr, false, "A", "B");
        aSwitch.getOutput1().setPinDescription(DESCRIPTION);
        aSwitch.getOutput2().setPinDescription(DESCRIPTION);

    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        s = inputs.get(0).checkBits(1, this, 0).addObserverToValue(this);
        ns = inputs.get(1).checkBits(1, this, 1).addObserverToValue(this);
        aSwitch.setInputs(new ObservableValues(inputs.get(2), inputs.get(3)));
    }

    @Override
    public ObservableValues getOutputs() throws PinException {
        return aSwitch.getOutputs();
    }

    @Override
    public void readInputs() throws NodeException {
        if (s.isHighZ() || ns.isHighZ())
            closed = false;
        else if (s.getBool() != ns.getBool())
            closed = s.getBool();
    }

    @Override
    public void writeOutputs() throws NodeException {
        aSwitch.setClosed(closed);
    }

    @Override
    public void init(Model model) throws NodeException {
        aSwitch.init(model);
        model.addObserver(event -> {
            if (event.equals(ModelEvent.STEP)) {
                if (!s.isHighZ() && !ns.isHighZ() && (s.getBool() == ns.getBool()))
                    throw new BurnException(Lang.get("err_invalidTransmissionGateState"), new ObservableValues(s, ns));
            }
        });
    }

    /**
     * @return the state of the transmission gate
     */
    public boolean isClosed() {
        return closed;
    }
}
