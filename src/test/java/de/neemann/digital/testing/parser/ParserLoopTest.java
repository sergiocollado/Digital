package de.neemann.digital.testing.parser;

import de.neemann.digital.data.Value;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created by hneemann on 19.04.17.
 */
public class ParserLoopTest extends TestCase {

    public void testLoop() throws IOException, ParserException {
        Parser parser = new Parser("A B\nloop(n,10)\n C (n*2)\nend loop").parse();
        LineCollector td = new LineCollector(parser);

        assertEquals(2, td.getNames().size());
        assertEquals(10, td.getLines().size());

        for (int i = 0; i < 10; i++) {
            assertEquals(Value.Type.CLOCK, td.getLines().get(i)[0].getType());
            assertEquals(i * 2, td.getLines().get(i)[1].getValue());
        }
    }

    public void testLoopVar() throws IOException, ParserException {
        Parser parser = new Parser("A B\nloop(i,10)\n C (i*2)\nend loop").parse();
        LineCollector td = new LineCollector(parser);

        assertEquals(2, td.getNames().size());
        assertEquals(10, td.getLines().size());

        for (int i = 0; i < 10; i++) {
            assertEquals(Value.Type.CLOCK, td.getLines().get(i)[0].getType());
            assertEquals(i * 2, td.getLines().get(i)[1].getValue());
        }
    }

    public void testNested() throws IOException, ParserException {
        Parser parser = new Parser("A B\nloop(i,10)\nloop(j,10)\n C (i+j*2)\nend loop\nend loop").parse();
        LineCollector td = new LineCollector(parser);

        assertEquals(2, td.getNames().size());
        assertEquals(100, td.getLines().size());

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int ind = i * 10 + j;
                assertEquals(Value.Type.CLOCK, td.getLines().get(ind)[0].getType());
                assertEquals(i + j * 2, td.getLines().get(ind)[1].getValue());
            }
        }
    }

    public void testLoopMultiLines() throws IOException, ParserException {
        Parser parser = new Parser("A B\nloop(i,10)\n C (i*2)\n C (i*2+1)\nend loop").parse();
        LineCollector td = new LineCollector(parser);

        assertEquals(2, td.getNames().size());
        assertEquals(20, td.getLines().size());

        for (int i = 0; i < 10; i++) {
            assertEquals(Value.Type.CLOCK, td.getLines().get(i * 2)[0].getType());
            assertEquals(i * 2, td.getLines().get(i * 2)[1].getValue());
            assertEquals(Value.Type.CLOCK, td.getLines().get(i * 2 + 1)[0].getType());
            assertEquals(i * 2 + 1, td.getLines().get(i * 2 + 1)[1].getValue());
        }
    }

    public void testMissingEndLoop() throws IOException, ParserException {
        try {
            new Parser("A B\nloop(i,10) C (i)").parse();
            fail();
        } catch (ParserException e) {
        }
    }

    public void testUnexpectedEndLoop() throws IOException, ParserException {
        try {
            new Parser("A B\n C 1\nend loop").parse();
            fail();
        } catch (ParserException e) {
        }
    }

    public void testIncompleteEndLoop() throws IOException, ParserException {
        try {
            new Parser("A B\n C 1\nend").parse();
            fail();
        } catch (ParserException e) {
        }
    }

}
