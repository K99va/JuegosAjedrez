package org.example.gui;

import org.example.creational.DatabaseManager;
import org.example.model.GameRecord;
import org.example.structural.GamePersistenceFacade;
import org.example.structural.GameComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class NQueensGUI extends GameComponent {
    private JPanel chessPanel;
    private JSpinner nSpinner;
    private JButton solveButton;
    private JTextArea solutionsArea;
    private JLabel solutionCountLabel;
    private JComboBox<String> solutionSelector;

    private int boardSize = 4;
    private long startTime;
    private GamePersistenceFacade persistenceFacade;
    private List<int[][]> solutions;
    private int currentSolutionIndex = 0;

    private static final Color DARK_COLOR = new Color(181, 136, 99);
    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color QUEEN_COLOR = new Color(200, 0, 0, 200);
    private static final Color CONFLICT_COLOR = new Color(255, 0, 0, 100);

    public NQueensGUI() {
        this.persistenceFacade = new GamePersistenceFacade();
        initialize();
    }

    @Override
    public void initialize() {
        this.setLayout(new BorderLayout());

        // Panel de control
        JPanel controlPanel = new JPanel(new FlowLayout());
        nSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 12, 1));
        solveButton = new JButton("Resolver");
        solveButton.addActionListener(this::solveNQueens);

        controlPanel.add(new JLabel("Número de reinas:"));
        controlPanel.add(nSpinner);
        controlPanel.add(solveButton);

        // Panel de información
        JPanel infoPanel = new JPanel(new FlowLayout());
        solutionCountLabel = new JLabel("Soluciones encontradas: 0");
        solutionSelector = new JComboBox<>();
        solutionSelector.addActionListener(e -> showSelectedSolution());

        infoPanel.add(solutionCountLabel);
        infoPanel.add(new JLabel("Solución:"));
        infoPanel.add(solutionSelector);

        // Panel de ajedrez
        chessPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChessBoard(g);
            }
        };

        // Área de soluciones
        solutionsArea = new JTextArea(5, 20);
        solutionsArea.setEditable(false);
        JScrollPane solutionsScroll = new JScrollPane(solutionsArea);

        // Configurar layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(chessPanel, BorderLayout.CENTER);
        this.add(solutionsScroll, BorderLayout.SOUTH);
    }

    @Override
    public void saveResults() {
        long duration = System.currentTimeMillis() - startTime;
        persistenceFacade.saveNQueensGame(boardSize, !solutions.isEmpty(), duration);
    }
    private void drawChessBoard(Graphics g) {
        int panelSize = Math.min(chessPanel.getWidth(), chessPanel.getHeight());
        int cellSize = panelSize / boardSize;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                // Dibujar casilla
                if ((row + col) % 2 == 0) {
                    g.setColor(LIGHT_COLOR);
                } else {
                    g.setColor(DARK_COLOR);
                }
                g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                // Dibujar reina si existe
                if (solutions != null && !solutions.isEmpty() &&
                        currentSolutionIndex < solutions.size() &&
                        solutions.get(currentSolutionIndex)[row][col] == 1) {

                    // Verificar si hay conflicto (solo para visualización)
                    boolean hasConflict = hasConflict(solutions.get(currentSolutionIndex), row, col);

                    if (hasConflict) {
                        g.setColor(CONFLICT_COLOR);
                        g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                    }

                    g.setColor(QUEEN_COLOR);
                    int padding = cellSize / 4;
                    g.fillOval(col * cellSize + padding, row * cellSize + padding,
                            cellSize - 2 * padding, cellSize - 2 * padding);

                    // Dibujar corona
                    g.setColor(Color.YELLOW);
                    g.drawString("♛", col * cellSize + cellSize/2 - 5, row * cellSize + cellSize/2 + 5);
                }
            }
        }
    }

    private boolean hasConflict(int[][] board, int row, int col) {
        if (board[row][col] != 1) return false;

        // Verificar fila
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            if (board[row][i] == 1) count++;
            if (count > 1) return true;
        }

        // Verificar columna
        count = 0;
        for (int i = 0; i < boardSize; i++) {
            if (board[i][col] == 1) count++;
            if (count > 1) return true;
        }

        // Verificar diagonales
        for (int i = 1; i < boardSize; i++) {
            if (row + i < boardSize && col + i < boardSize && board[row + i][col + i] == 1) return true;
            if (row - i >= 0 && col - i >= 0 && board[row - i][col - i] == 1) return true;
            if (row + i < boardSize && col - i >= 0 && board[row + i][col - i] == 1) return true;
            if (row - i >= 0 && col + i < boardSize && board[row - i][col + i] == 1) return true;
        }

        return false;
    }

    private void solveNQueens(ActionEvent e) {
        boardSize = (Integer) nSpinner.getValue();
        solutions = new ArrayList<>();
        currentSolutionIndex = 0;
        startTime = System.currentTimeMillis();

        new Thread(() -> {
            int[][] board = new int[boardSize][boardSize];
            solveNQUtil(board, 0);

            SwingUtilities.invokeLater(() -> {
                solutionCountLabel.setText("Soluciones encontradas: " + solutions.size());
                updateSolutionSelector();

                if (!solutions.isEmpty()) {
                    showSelectedSolution();
                } else {
                    solutionsArea.setText("No se encontraron soluciones para N = " + boardSize);
                    chessPanel.repaint();
                }
                saveResults(); // Llamada al método de guardado
            });
        }).start();
    }

    private void solveNQUtil(int[][] board, int col) {
        if (col >= boardSize) {
            // Guardar una copia de la solución encontrada
            int[][] solution = new int[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                System.arraycopy(board[i], 0, solution[i], 0, boardSize);
            }
            solutions.add(solution);
            return;
        }

        for (int i = 0; i < boardSize; i++) {
            if (isSafe(board, i, col)) {
                board[i][col] = 1;

                // Actualizar la interfaz ocasionalmente para mostrar progreso
                if (col % 2 == 0) {
                    SwingUtilities.invokeLater(() -> {
                        solutionsArea.setText("Buscando soluciones...\nEncontradas: " + solutions.size());
                        chessPanel.repaint();
                    });
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                }

                solveNQUtil(board, col + 1);
                board[i][col] = 0; // BACKTRACK
            }
        }
    }

    private boolean isSafe(int board[][], int row, int col) {
        // Verificar fila
        for (int i = 0; i < col; i++)
            if (board[row][i] == 1)
                return false;

        // Verificar diagonal superior
        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--)
            if (board[i][j] == 1)
                return false;

        // Verificar diagonal inferior
        for (int i = row, j = col; j >= 0 && i < boardSize; i++, j--)
            if (board[i][j] == 1)
                return false;

        return true;
    }

    private void updateSolutionSelector() {
        solutionSelector.removeAllItems();
        for (int i = 0; i < solutions.size(); i++) {
            solutionSelector.addItem("Solución " + (i + 1));
        }
    }

    private void showSelectedSolution() {
        currentSolutionIndex = solutionSelector.getSelectedIndex();
        if (currentSolutionIndex >= 0 && currentSolutionIndex < solutions.size()) {
            solutionsArea.setText(solutionToString(solutions.get(currentSolutionIndex)));
            chessPanel.repaint();
        }
    }

    private String solutionToString(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                sb.append(board[i][j] == 1 ? " Q " : " . ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    private void saveGameResult(boolean successful, int n, long durationMs) {
        try {
            String parameters = String.format("{\"n\": %d}", n);
            persistenceFacade.saveGame(
                    "N_QUEENS",
                    parameters,
                    successful,
                    null,
                    durationMs
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}