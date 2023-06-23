package com.artostapyshyn.puzzleapp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleImageSplitter {
    public static void main(String[] args) {
        splitImage();
    }

    private static void splitImage() {
        int rows = 4;
        int columns = 4;

        try {
            BufferedImage inputImage = ImageIO.read(new File("input.jpg"));

            int pieceWidth = inputImage.getWidth() / columns;
            int pieceHeight = inputImage.getHeight() / rows;

            File outputFolder = new File("puzzles/");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            List<PuzzlePieceInfo> puzzlePieces = new ArrayList<>();

            Random random = new Random();

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    BufferedImage puzzlePiece = inputImage.getSubimage(
                            x * pieceWidth, y * pieceHeight, pieceWidth, pieceHeight);

                    String outputImagePath = "puzzles/puzzle_" + random.nextInt(10000) + ".jpg";

                    ImageIO.write(puzzlePiece, "jpg", new File(outputImagePath));

                    PuzzlePieceInfo puzzlePieceInfo = new PuzzlePieceInfo(outputImagePath, x, y);
                    puzzlePieces.add(puzzlePieceInfo);
                }
            }

            savePuzzlePiecesInfo(puzzlePieces);

            System.out.println("Puzzle creation completed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void savePuzzlePiecesInfo(List<PuzzlePieceInfo> puzzlePieces) {
        try {
            File infoFile = new File("puzzles/puzzle_info.txt");
            if (!infoFile.exists()) {
                infoFile.createNewFile();
            }

            FileWriter writer = new FileWriter(infoFile);
            for (PuzzlePieceInfo puzzlePiece : puzzlePieces) {
                writer.write(puzzlePiece.getImagePath() + "," + puzzlePiece.getX() + "," + puzzlePiece.getY() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PuzzlePieceInfo {
    private String imagePath;
    private int x;
    private int y;

    public PuzzlePieceInfo(String imagePath, int x, int y) {
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
