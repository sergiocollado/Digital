package de.neemann.digital.gui.draw.shapes;

import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.arithmetic.Add;
import de.neemann.digital.core.arithmetic.Mul;
import de.neemann.digital.core.arithmetic.Sub;
import de.neemann.digital.core.basic.*;
import de.neemann.digital.core.io.Const;
import de.neemann.digital.core.io.In;
import de.neemann.digital.core.io.Out;
import de.neemann.digital.core.part.AttributeKey;
import de.neemann.digital.core.part.PartAttributes;
import de.neemann.digital.core.part.PartTypeDescription;
import de.neemann.digital.core.wiring.Delay;
import de.neemann.digital.gui.draw.library.PartLibrary;

import java.util.HashMap;

/**
 * @author hneemann
 */
public final class ShapeFactory {

    private static final class InstanceHolder {
        static final ShapeFactory INSTANCE = new ShapeFactory();
    }

    public static ShapeFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public HashMap<String, Creator> map = new HashMap<>();
    private PartLibrary library;

    private ShapeFactory() {
        map.put(And.DESCRIPTION.getName(), new CreatorSimple("&", And.DESCRIPTION, false));
        map.put(Or.DESCRIPTION.getName(), new CreatorSimple("\u22651", Or.DESCRIPTION, false));
        map.put(NAnd.DESCRIPTION.getName(), new CreatorSimple("&", NAnd.DESCRIPTION, true));
        map.put(NOr.DESCRIPTION.getName(), new CreatorSimple("\u22651", NOr.DESCRIPTION, true));
        map.put(Not.DESCRIPTION.getName(), new CreatorSimple("", Not.DESCRIPTION, true));
        map.put(Delay.DESCRIPTION.getName(), new CreatorSimple("", Delay.DESCRIPTION, false));

        map.put(XOr.DESCRIPTION.getName(), new CreatorSimple("=1", XOr.DESCRIPTION, false));
        map.put(XNOr.DESCRIPTION.getName(), new CreatorSimple("=1", XNOr.DESCRIPTION, true));

        map.put(Add.DESCRIPTION.getName(), attr -> new GenericShape("+", Add.DESCRIPTION.getInputNames(attr), outputNames(Add.DESCRIPTION, attr), true));
        map.put(Sub.DESCRIPTION.getName(), attr -> new GenericShape("-", Sub.DESCRIPTION.getInputNames(attr), outputNames(Sub.DESCRIPTION, attr), true));
        map.put(Mul.DESCRIPTION.getName(), attr -> new GenericShape("*", Mul.DESCRIPTION.getInputNames(attr), outputNames(Mul.DESCRIPTION, attr), true));


        map.put(In.DESCRIPTION.getName(), attr -> new InputShape(attr.get(AttributeKey.Label)));
        map.put(Const.DESCRIPTION.getName(), attr -> new ConstShape(attr.get(AttributeKey.Value)));
        map.put(Out.DESCRIPTION.getName(), attr -> new OutputShape(attr.get(AttributeKey.Label)));
        map.put(Out.LEDDESCRIPTION.getName(), attr -> new LEDShape(attr.get(AttributeKey.Label), attr.get(AttributeKey.Color)));
        map.put(Out.PROBEDESCRIPTION.getName(), attr -> new ProbeShape(attr.get(AttributeKey.Label)));
    }

    public PartLibrary setLibrary(PartLibrary library) {
        this.library = library;
        return library;
    }

    private String[] outputNames(PartTypeDescription description, PartAttributes attributes) {
        ObservableValue[] o = description.createPart(attributes).getOutputs();
        String[] names = new String[o.length];
        for (int i = 0; i < names.length; i++)
            names[i] = o[i].getName();
        return names;
    }

    public Shape getShape(String partName, PartAttributes partAttributes) {
        Creator cr = map.get(partName);
        if (cr == null) {
            if (library == null)
                throw new RuntimeException("no shape for " + partName);
            else {
                PartTypeDescription pt = library.getPartType(partName);
                return new GenericShape(createName(partName), pt.getInputNames(partAttributes), outputNames(pt, partAttributes), true);
            }
        } else
            return cr.create(partAttributes);
    }

    private String createName(String partName) {
        int p = partName.indexOf('_');
        if (p < 0)
            return partName;
        else
            return partName.substring(0, p);
    }

    private interface Creator {
        Shape create(PartAttributes attributes);
    }


    public class CreatorSimple implements Creator {

        private final String name;
        private final PartTypeDescription description;
        private final boolean invers;

        public CreatorSimple(String name, PartTypeDescription description, boolean invers) {
            this.name = name;
            this.description = description;
            this.invers = invers;
        }

        @Override
        public Shape create(PartAttributes attributes) {
            return new GenericShape(name, description.getInputNames(attributes), outputNames(description, attributes)).invert(invers);
        }
    }
}