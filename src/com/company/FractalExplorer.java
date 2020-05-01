package com.company;

import com.company.fractal.FractalGenerator;
import com.company.fractal.MandelbrotGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * This class is responsible of creating GUI
 * that shows Mandelbrot fractal and lets user
 * zoom it or reset to the initial scale
 *
 * @author Stoyalov Arseny BVT1803
 */
public class FractalExplorer {

    private int displaySize;

    private JImageDisplay display;

    private FractalGenerator generator;

    private Rectangle2D.Double complexArea;

    public FractalExplorer(int displaySize) {
        this.displaySize = displaySize;
        this.generator = new MandelbrotGenerator();
        this.complexArea = new Rectangle2D.Double();
    }

    /**
     * Initializes a form with an image of fractal and
     * a reset button
     */
    public void createView() {

        JFrame frame = new JFrame("Fractals");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display = new JImageDisplay(displaySize, displaySize);
        JButton button = new JButton("Reset");
        frame.add(display, BorderLayout.CENTER);
        frame.add(button, BorderLayout.SOUTH);
        frame.addMouseListener(new MouseClickListener());

        button.addActionListener(new ResetButtonListener());

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        generator.getInitialRange(complexArea);
        drawFractal();
    }

    /**
     * Reset image scale to the initial value
     */
    private class ResetButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            generator.getInitialRange(complexArea);
            drawFractal();
        }

    }

    /**
     * Zooms in fractal image
     */
    private class MouseClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            double x = FractalGenerator.getCoord(complexArea.x, complexArea.x + complexArea.width,
                    displaySize, e.getX());
            double y = FractalGenerator.getCoord(complexArea.y, complexArea.y + complexArea.height,
                    displaySize, e.getY());
            generator.recenterAndZoomRange(complexArea, x, y, 0.8);
            drawFractal();
        }

    }

    /**
     * Generates fractal image
     */
    private void drawFractal() {

        for (int x = 0; x < displaySize; x++) {
            double xCoord = FractalGenerator.getCoord(complexArea.x, complexArea.x + complexArea.width,
                    displaySize, x);
            for (int y = 0; y < displaySize; y++) {
                double yCoord = FractalGenerator.getCoord(complexArea.y, complexArea.y + complexArea.height,
                        displaySize, y);
                int color = generator.numIterations(xCoord, yCoord);
                display.drawPixel(x, y, color);
            }
        }
        display.repaint();
    }

}
