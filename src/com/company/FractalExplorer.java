package com.company;

import com.company.fractal.BurningShipGenerator;
import com.company.fractal.FractalGenerator;
import com.company.fractal.MandelbrotGenerator;
import com.company.fractal.TricornGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

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

    private JFrame frame;

    private JButton btnSave, btnReset;

    private JComboBox<FractalGenerator> comboBox;

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

        frame = new JFrame("Fractals");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel();
        JLabel lblFractal = new JLabel("Fractal:");
        comboBox = new JComboBox<>();
        top.add(lblFractal);
        top.add(comboBox);
        JPanel bottom = new JPanel();
        btnReset = new JButton("Reset display");
        btnSave = new JButton("Save image");
        bottom.add(btnSave);
        bottom.add(btnReset);
        display = new JImageDisplay(displaySize, displaySize);

        frame.add(top, BorderLayout.NORTH);
        frame.add(display, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.addMouseListener(new MouseClickListener());

        btnReset.setActionCommand("reset");
        btnSave.setActionCommand("save");
        ActionListener btnListener = new ButtonListener();
        btnReset.addActionListener(btnListener);
        btnSave.addActionListener(btnListener);

        comboBox.addItem(generator);
        comboBox.addItem(new TricornGenerator());
        comboBox.addItem(new BurningShipGenerator());
        comboBox.addActionListener((e) -> {
            generator = (FractalGenerator) comboBox.getSelectedItem();
            if (generator != null)
                generator.getInitialRange(complexArea);
            drawFractal();
        });

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        generator.getInitialRange(complexArea);
        drawFractal();
    }

    private void enableUI(boolean v) {
        frame.setEnabled(v);
        btnReset.setEnabled(v);
        btnSave.setEnabled(v);
        comboBox.setEnabled(v);
    }

    /**
     * Makes fractals generate row by row
     * in background in order to improve performance
     */
    private class FractalWorker extends SwingWorker<Object, Object> {

        private int y;

        private int remaining;

        private int[] colors;

        public FractalWorker(int y, int remaining) {
            this.y = y;
            this.remaining = remaining;
        }

        @Override
        protected void done() {
            super.done();
            for (int x = 0; x < colors.length; x++) {
                display.getImage().setRGB(x, y, colors[x]);
            }
            display.repaint(0, y, displaySize, 1);
            if (remaining == 1) enableUI(true);
        }

        @Override
        protected Object doInBackground() {

            colors = new int[displaySize];
            double yCoord = FractalGenerator.getCoord(complexArea.y, complexArea.y + complexArea.height,
                    displaySize, y);
            for (int x = 0; x < displaySize; x++) {
                double xCoord = FractalGenerator.getCoord(complexArea.x, complexArea.x + complexArea.width,
                        displaySize, x);
                colors[x] = generator.numIterations(xCoord, yCoord);
            }
            return null;
        }

    }

    /**
     * Action listener responsible for
     * reset and save buttons events
     */
    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "reset":
                    generator.getInitialRange(complexArea);
                    drawFractal();
                    break;
                case "save":
                    JFileChooser fileChooser = new JFileChooser();
                    FileFilter filter = new FileNameExtensionFilter("PNG Images", "pgn");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    int res = fileChooser.showDialog(frame, "Save");
                    if (res == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try {
                            ImageIO.write(display.getImage(), "png", file);
                        } catch (IOException ex) {
                            System.out.println("Had problems with saving fractal image");
                            JOptionPane.showMessageDialog(frame, "Could not save image",
                                    "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
            }
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

        enableUI(false);
        for (int y = 0; y < displaySize; y++) {
            FractalWorker worker = new FractalWorker(y, displaySize - y);
            worker.execute();
        }
    }

}
