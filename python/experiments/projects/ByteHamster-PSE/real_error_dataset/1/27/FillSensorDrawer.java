package edu.kit.pse.osip.simulation.view.main;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class visualizes a fill level sensor.
 */
public class FillSensorDrawer extends edu.kit.pse.osip.simulation.view.main.ObjectDrawer {

    private static final double WIDTH = 0.05;
    private static final double HEIGHT = 0.05;

    private int rows;
    private int cols;

    /**
     * Generates a new drawer for fill sensors		
     * @param pos The center of the drawer
     * @param rows The number of rows in which the tanks are ordered
     * @param cols The number of columns in which the tanks are ordered
     */
    public FillSensorDrawer(Point2D pos, int rows, int cols) {
        super(pos);
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * The Drawer draws itself onto the GraphicsContext at its position.
     * @param context The context that the object draws itself onto
     */
    @Override
    public final void draw(GraphicsContext context, long time) {
        context.setFill(Color.BLACK);
        context.setStroke(Color.BLACK);
        context.setLineWidth(2);

        Canvas canvas = context.getCanvas();
        double totalWidth = canvas.getWidth();
        double totalHeight = canvas.getHeight() - 25;

        double compWidth = totalWidth / rows;
        double compHeight = totalHeight / cols;

        double rectXPos = (getPosition().getX() + 1.0 / 4.0 * WIDTH / cols) * totalWidth;
        double rectYPos = (getPosition().getY() - 1.0 / 2.0 * HEIGHT / rows) * totalHeight;
        double rectWidth = compWidth * WIDTH / 2.0;
        double rectHeight = compHeight * HEIGHT;

        double leftEndX = (getPosition().getX() - 1 / 4.0 * WIDTH / cols) * totalWidth;

        // Draw a rectangle and a line going into the tank
        context.fillRect(rectXPos, rectYPos, rectWidth, rectHeight);
        context.strokeLine(leftEndX, getPosition().getY() * totalHeight, rectXPos, getPosition().getY() * totalHeight);

    }
}
