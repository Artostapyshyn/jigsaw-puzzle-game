package com.artostapyshyn.puzzleapp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class PuzzleApp extends JFrame {
    private static JPanel puzzlePanel;
    private ArrayList<PuzzlePiece> puzzlePieces;
    private PuzzlePiece selectedPiece;
    private boolean isSolved = false;
    private ArrayList<Integer> currentRotations;
    private ArrayList<Integer> originalRotations;

    public PuzzleApp(File puzzleFolder) {
        setTitle("Puzzle App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        puzzlePanel = new JPanel();
        puzzlePanel.setLayout(new GridLayout(4, 4));
        puzzlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadPuzzlePieces(puzzleFolder);

        add(puzzlePanel, BorderLayout.CENTER);

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new CheckButtonListener());
        add(checkButton, BorderLayout.NORTH);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new SolveButtonListener());
        add(solveButton, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void loadPuzzlePieces(File puzzleFolder) {
        puzzlePieces = new ArrayList<>();
        originalRotations = new ArrayList<>();
        currentRotations = new ArrayList<>();

        List<File> puzzleFiles = Arrays.asList(Objects.requireNonNull(puzzleFolder.listFiles()));
        Collections.shuffle(puzzleFiles);

        for (int i = 0; i < puzzleFiles.size(); i++) {
            File puzzleImageFile = puzzleFiles.get(i);
            if (puzzleImageFile.getName().equalsIgnoreCase("puzzle_info.txt")) {
                continue;
            }

            BufferedImage puzzleImage = loadImage(puzzleImageFile);
            PuzzlePiece puzzlePiece = new PuzzlePiece(puzzleImage, i, puzzleImageFile.getName());
            puzzlePiece.addMouseListener(new PuzzlePieceMouseListener());

            originalRotations.add(puzzlePiece.getRotation());
            currentRotations.add(puzzlePiece.getRotation());
            puzzlePieces.add(puzzlePiece);
            puzzlePanel.add(puzzlePiece);
        }
    }

    private static BufferedImage loadImage(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class PuzzlePieceMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isSolved) {
                return;
            }

            if (SwingUtilities.isRightMouseButton(e)) {
                PuzzlePiece puzzlePiece = (PuzzlePiece) e.getSource();
                puzzlePiece.rotatePiece();
            } else {
                if (selectedPiece == null) {
                    selectedPiece = (PuzzlePiece) e.getSource();
                    selectedPiece.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                } else {
                    PuzzlePiece targetPiece = (PuzzlePiece) e.getSource();
                    if (targetPiece != selectedPiece) {
                        swapPieces(selectedPiece, targetPiece);
                    }
                    selectedPiece.setBorder(BorderFactory.createEmptyBorder());
                    selectedPiece = null;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private void swapPieces(PuzzlePiece piece1, PuzzlePiece piece2) {
        int index1 = puzzlePieces.indexOf(piece1);
        int index2 = puzzlePieces.indexOf(piece2);
        Collections.swap(puzzlePieces, index1, index2);
        Collections.swap(originalRotations, index1, index2);

        updatePuzzlePanel();
    }

    private class CheckButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showFullImage();
        }
    }

    private void updatePuzzlePanel() {
        puzzlePanel.removeAll();
        for (PuzzlePiece piece : puzzlePieces) {
            puzzlePanel.add(piece);
        }
        puzzlePanel.revalidate();
        puzzlePanel.repaint();

        boolean isCorrect = true;
        for (int i = 0; i < puzzlePieces.size(); i++) {
            if (puzzlePieces.get(i).getIndex() != i) {
                isCorrect = false;
                break;
            }
        }

        isSolved = isCorrect;
    }

    private void showFullImage() {
        JFrame fullImageFrame = new JFrame("Solution");
        fullImageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon imageIcon = new ImageIcon("input.jpg");
        Image image = imageIcon.getImage().getScaledInstance(450, 450, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(image);

        JLabel imageLabel = new JLabel(scaledImageIcon);
        imageLabel.setPreferredSize(new Dimension(450, 450));

        fullImageFrame.add(imageLabel);

        fullImageFrame.pack();
        fullImageFrame.setLocationRelativeTo(null);
        fullImageFrame.setVisible(true);
    }

    private class SolveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            solvePuzzle(new File("puzzles/"));
        }

        private void solvePuzzle(File puzzleFolder) {
            File puzzleInfoFile = new File(puzzleFolder, "puzzle_info.txt");
            try (Scanner scanner = new Scanner(puzzleInfoFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    String puzzleImagePath = parts[0].trim();
                    int row = Integer.parseInt(parts[1].trim());
                    int column = Integer.parseInt(parts[2].trim());

                    File puzzleImageFile = new File(puzzleImagePath);
                    BufferedImage puzzleImage = loadImage(puzzleImageFile);
                    int newWidth = 100;
                    int newHeight = 100;
                    Image scaledImage = Objects.requireNonNull(puzzleImage).getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                    int puzzleIndex = column * 4 + row;
                    PuzzlePiece puzzlePiece = puzzlePieces.get(puzzleIndex);
                    puzzlePiece.setIcon(new ImageIcon(scaledImage));

                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            puzzlePanel.revalidate();
            puzzlePanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String inputFolderPath = "puzzles/";
            File puzzleFolder = new File(inputFolderPath);

            PuzzleApp puzzleApp = new PuzzleApp(puzzleFolder);
            puzzleApp.setVisible(true);
        });
    }
}