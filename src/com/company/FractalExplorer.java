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
        JComboBox<FractalGenerator> comboBox = new JComboBox<>();
        top.add(lblFractal);
        top.add(comboBox);
        JPanel bottom = new JPanel();
        JButton btnReset = new JButton("Reset display");
        JButton btnSave = new JButton("Save image");
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

    /**
     * Event listener responsible for
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
