package de.neemann.digital.draw.graphics;

import java.awt.*;

/**
 * @author hneemann
 */
public class GraphicSwing implements Graphic {

    private final Graphics2D gr;
    private Style lastStyle;

    public GraphicSwing(Graphics2D gr) {
        this.gr = gr;
//        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    @Override
    public void drawLine(Vector p1, Vector p2, Style style) {
        applyStyle(style);
        gr.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    @Override
    public void drawPolygon(Polygon p, Style style) {
        applyStyle(style);
        java.awt.Polygon poly = new java.awt.Polygon();
        for (Vector v : p.getPoints())
            poly.addPoint(v.x, v.y);

        if (style.isFilled())
            gr.fill(poly);
        gr.draw(poly);
    }

    @Override
    public void drawCircle(Vector p1, Vector p2, Style style) {
        applyStyle(style);
        Vector p = Vector.min(p1, p2);
        Vector w = Vector.width(p1, p2);
        if (style.isFilled())
            gr.fillOval(p.x - 1, p.y - 1, w.x + 2, w.y + 2);
        else
            gr.drawOval(p.x, p.y, w.x, w.y);
    }

    private void applyStyle(Style style) {
        if (style != lastStyle) {
            gr.setStroke(style.getStroke());
            gr.setColor(style.getColor());
            gr.setFont(style.getFont());
            lastStyle = style;
        }
    }

    @Override
    public void drawText(Vector p1, Vector p2, String text, Orientation orientation, Style style) {
        applyStyle(style);
        int xoff = 0;
        if (orientation.getX() != 0) {
            int width = gr.getFontMetrics().stringWidth(text);
            xoff -= width * orientation.getX() / 2;
        }

        int yoff = 0;
        if (orientation.getY() != 0) {
            int height = gr.getFontMetrics().getHeight();
            yoff += height * orientation.getY() / 3;
        }

        gr.drawString(text, p1.x + xoff, p1.y + yoff);
    }
}