package org.example.gui;

import org.example.creational.DatabaseManager;
import org.example.structural.GamePersistenceFacade;
import org.example.structural.GameComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KnightTourGUI extends GameComponent {
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 60;
    private static final Color DARK_COLOR = new Color(181, 136, 99);
    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color HIGHLIGHT_COLOR = new Color(0, 200, 0, 128);
    private static final Color PATH_COLOR = new Color(100, 200, 100, 100);
    private static final Color CURRENT_COLOR = new Color(200, 0, 0, 150);

    private JPanel chessPanel;
    private JButton startButton;
    private JSpinner startXSpinner;
    private JSpinner startYSpinner;
    private JTextArea logArea;
    private JLabel timeLabel;
    private JLabel movesLabel;

    private int[][] chessboard;
    private boolean[] visited;
    private boolean finished;
    private List<Point> moveHistory = new ArrayList<>();
    private long startTime;
    private ScheduledExecutorService executorService;
    private GamePersistenceFacade persistenceFacade;

    public KnightTourGUI() {
        this.persistenceFacade = new GamePersistenceFacade();
        initializeComponents(); // Mover la inicialización aquí
    }


    @Override
    public void initialize() {
        initializeComponents();
    }

    @Override
    public void saveResults() {
        int startX = (Integer) startXSpinner.getValue() - 1;
        int startY = (Integer) startYSpinner.getValue() - 1;
        long duration = System.currentTimeMillis() - startTime;
        persistenceFacade.saveKnightTourGame(startX, startY, moveHistory.size(), finished, duration);
    }

    private void drawChessBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                // Dibujar casilla
                if ((row + col) % 2 == 0) {
                    g.setColor(LIGHT_COLOR);
                } else {
                    g.setColor(DARK_COLOR);
                }
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // Dibujar número de paso si existe
                if (chessboard != null && chessboard[row][col] > 0) {
                    g.setColor(Color.BLACK);
                    FontMetrics fm = g.getFontMetrics();
                    String step = String.valueOf(chessboard[row][col]);
                    int x = col * CELL_SIZE + (CELL_SIZE - fm.stringWidth(step)) / 2;
                    int y = row * CELL_SIZE + (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();
                    g.drawString(step, x, y);
                }
            }
        }
    }

    private void drawPath(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar el camino
        g2d.setColor(PATH_COLOR);
        for (int i = 0; i < moveHistory.size() - 1; i++) {
            Point current = moveHistory.get(i);
            Point next = moveHistory.get(i + 1);
            int x1 = current.x * CELL_SIZE + CELL_SIZE / 2;
            int y1 = current.y * CELL_SIZE + CELL_SIZE / 2;
            int x2 = next.x * CELL_SIZE + CELL_SIZE / 2;
            int y2 = next.y * CELL_SIZE + CELL_SIZE / 2;
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Dibujar la posición actual
        if (!moveHistory.isEmpty()) {
            Point current = moveHistory.get(moveHistory.size() - 1);
            g2d.setColor(CURRENT_COLOR);
            g2d.fillOval(current.x * CELL_SIZE + 5, current.y * CELL_SIZE + 5,
                    CELL_SIZE - 10, CELL_SIZE - 10);
        }

        g2d.dispose();
    }

    private void startTour(ActionEvent e) {
        int startX = (Integer) startXSpinner.getValue() - 1;
        int startY = (Integer) startYSpinner.getValue() - 1;

        // Validar posición inicial
        if (startX < 0 || startX >= BOARD_SIZE || startY < 0 || startY >= BOARD_SIZE) {
            JOptionPane.showMessageDialog(this, "Posición inicial inválida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Inicializar variables
        chessboard = new int[BOARD_SIZE][BOARD_SIZE];
        visited = new boolean[BOARD_SIZE * BOARD_SIZE];
        finished = false;
        moveHistory.clear();
        startTime = System.currentTimeMillis();

        // Configurar botón y área de log
        startButton.setEnabled(false);
        logArea.setText("Iniciando recorrido desde (" + (startX + 1) + ", " + (startY + 1) + ")\n");

        // Ejecutar en un hilo separado para no bloquear la interfaz
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (!finished) {
                SwingUtilities.invokeLater(() -> {
                    traversalChessboard(chessboard, startY, startX, 1);
                    updateUI();
                });
            } else {
                executorService.shutdown();
                saveResults(); // Guardar resultados al finalizar
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void updateUI() {
        if (timeLabel == null || movesLabel == null || chessPanel == null) {
            return; // Aún no inicializado
        }
        timeLabel.setText("Tiempo: " + (System.currentTimeMillis() - startTime) + "ms");
        movesLabel.setText("Movimientos: " + (moveHistory.size() > 0 ? moveHistory.size() - 1 : 0));
        chessPanel.repaint();
    }


    private void traversalChessboard(int[][] chessboard, int row, int column, int step) {
        chessboard[row][column] = step;
        visited[row * BOARD_SIZE + column] = true;
        moveHistory.add(new Point(column, row));

        // Obtener los siguientes movimientos posibles
        ArrayList<Point> ps = next(new Point(column, row));
        sort(ps);

        // Recorrer los movimientos posibles
        while (!ps.isEmpty()) {
            Point p = ps.remove(0);
            if (!visited[p.y * BOARD_SIZE + p.x]) {
                traversalChessboard(chessboard, p.y, p.x, step + 1);
            }
        }

        if (step < BOARD_SIZE * BOARD_SIZE && !finished) {
            chessboard[row][column] = 0;
            visited[row * BOARD_SIZE + column] = false;
            moveHistory.remove(moveHistory.size() - 1);
        } else {
            finished = true;
            SwingUtilities.invokeLater(() -> {
                logArea.append("Recorrido completado en " + (System.currentTimeMillis() - startTime) + "ms\n");
                startButton.setEnabled(true);
            });
        }
    }

    public ArrayList<Point> next(Point curPoint) {
        ArrayList<Point> ps = new ArrayList<>();
        int[][] moves = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1},
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1}};

        for (int[] move : moves) {
            int x = curPoint.x + move[0];
            int y = curPoint.y + move[1];
            if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
                ps.add(new Point(x, y));
            }
        }
        return ps;
    }
    private void initializeComponents() {
        setLayout(new BorderLayout());

        // 1. Inicializar componentes primero
        timeLabel = new JLabel("Tiempo: 0ms");
        movesLabel = new JLabel("Movimientos: 0");
        chessPanel = new ChessPanel(); // Panel interno especializado

        // 2. Configurar panel de control
        JPanel controlPanel = new JPanel(new FlowLayout());
        startXSpinner = new JSpinner(new SpinnerNumberModel(1, 1, BOARD_SIZE, 1));
        startYSpinner = new JSpinner(new SpinnerNumberModel(1, 1, BOARD_SIZE, 1));
        startButton = new JButton("Iniciar Recorrido");
        startButton.addActionListener(this::startTour);

        controlPanel.add(new JLabel("Posición inicial X:"));
        controlPanel.add(startXSpinner);
        controlPanel.add(new JLabel("Y:"));
        controlPanel.add(startYSpinner);
        controlPanel.add(startButton);

        // 3. Panel de información
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.add(timeLabel);
        infoPanel.add(movesLabel);

        // 4. Área de log
        logArea = new JTextArea(5, 20);
        logArea.setEditable(false);

        // 5. Configurar layout principal
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(chessPanel), BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);
    }

    // Clase interna para el panel de ajedrez
    private class ChessPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawChessBoard(g);
            if (moveHistory.size() > 0) {
                drawPath(g);
            }
        }
    }

    public void sort(ArrayList<Point> ps) {
        ps.sort(Comparator.comparingInt(p -> next(p).size()));
    }
}