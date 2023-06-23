package com.artostapyshyn.puzzleapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class PuzzlePiece extends JLabel {
    private BufferedImage puzzleImage;
    private int index;
    private int rotation = 0;
    private String filename;

    public PuzzlePiece(BufferedImage image, int index, String filename) {
        puzzleImage = image;
        this.index = index;
        this.filename = filename;
        setPreferredSize(new Dimension(100, 100));
        setOpaque(true);

        randomRotatePiece();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    rotatePiece();
                }
            }
        });
    }

    private void randomRotatePiece() {
        Random random = new Random();
        int rotationDegrees = random.nextInt(4) * 90;
        rotation = rotationDegrees;
        updateIcon();
    }

    void rotatePiece() {
        rotation = (rotation + 45) % 360;
        updateIcon();
    }

    private void updateIcon() {
        ImageIcon imageIcon = new ImageIcon(rotateImage(puzzleImage, rotation));
        Image scaledImage = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaledImage));
    }

    private BufferedImage rotateImage(BufferedImage image, int degrees) {
        int width = image.getWidth();
        int height = image.getHeight();

        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.round(width * cos + height * sin);
        int newHeight = (int) Math.round(width * sin + height * cos);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(-radians, newWidth / 2, newHeight / 2);
        g2d.drawImage(image, (newWidth - width) / 2, (newHeight - height) / 2, null);
        g2d.dispose();

        return rotatedImage;
    }

    public int getIndex() {
        return index;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        updateIcon();
    }
}
