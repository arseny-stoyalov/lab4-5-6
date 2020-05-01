package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class represents and image component
 * that can be redrawn by changing it's pixels
 *
 * @author Stoyalov Arseny BVT1803
 */
public class JImageDisplay extends JComponent {

    private BufferedImage image;

    public JImageDisplay(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(0, 0, 50, 50);
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    /**
     * Clears image by setting all pixels
     * black
     */
    public void clearImage() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, 0);
            }
        }
    }

    /**
     * Sets color to a certain pixel
     */
    public void drawPixel(int x, int y, int rgbColor) {
        image.setRGB(x, y, rgbColor);
    }

    public BufferedImage getImage() {
        return image;
    }

}
