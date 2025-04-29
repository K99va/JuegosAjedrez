package org.example.gui;

import org.example.creational.DatabaseManager;
import org.example.structural.GamePersistenceFacade;
import org.example.structural.GameComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

public class HanoiGUI extends GameComponent {
    private static final int DISK_HEIGHT = 20;
    private static final int MAX_DISK_WIDTH = 150;
    private static final int MIN_DISK_WIDTH = 50;
    private static final int POLE_WIDTH = 20;
    private static final int POLE_HEIGHT = 200;
    private static final int BASE_WIDTH = 250;
    private static final int BASE_HEIGHT = 30;

    private JPanel drawingPanel;
    private JTextArea logArea;
    private JSpinner diskSpinner;
    private Stack<Integer>[] towers;
    private long startTime;
    private int totalDisks;
    private int selectedTower = -1;
    private int moveCount = 0;
    private GamePersistenceFacade persistenceFacade;

    public HanoiGUI() {
        this.persistenceFacade = new GamePersistenceFacade();
        initialize();
    }

    @Override
    public void initialize() {
        this.setLayout(new BorderLayout());

        // Panel de control
        JPanel controlPanel = new JPanel();
        diskSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 8, 1));
        JButton resetButton = new JButton("Reiniciar");
        resetButton.addActionListener(e -> resetTowers((Integer) diskSpinner.getValue()));

        controlPanel.add(new JLabel("Número de discos:"));
        controlPanel.add(diskSpinner);
        controlPanel.add(resetButton);

        // Área de log
        logArea = new JTextArea(5, 20);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        // Panel de dibujo con listener de mouse
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTowers(g);
            }
        };
        drawingPanel.setPreferredSize(new Dimension(700, 400));
        drawingPanel.setBackground(Color.WHITE);

        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTowerClick(e.getX());
            }
        });

        // Configurar layout del panel principal
        this.add(controlPanel, BorderLayout.NORTH);
        this.add(drawingPanel, BorderLayout.CENTER);
        this.add(logScroll, BorderLayout.SOUTH);

        resetTowers((Integer) diskSpinner.getValue());
    }

    @Override
    public void saveResults() {
        long duration = System.currentTimeMillis() - startTime;
        persistenceFacade.saveHanoiGame(totalDisks, moveCount, towers[2].size() == totalDisks, duration);
    }

    private void resetTowers(int disks) {
        towers = new Stack[3];
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }

        totalDisks = disks;
        for (int i = disks; i > 0; i--) {
            towers[0].push(i);
        }

        selectedTower = -1;
        moveCount = 0;
        startTime = System.currentTimeMillis();
        drawingPanel.repaint();
        logArea.setText("Juego reiniciado con " + disks + " discos.\n");
    }

    private void drawTowers(Graphics g) {
        int centerX = drawingPanel.getWidth() / 2;
        int baseY = drawingPanel.getHeight() - 50;

        // Dibujar bases y postes
        g.setColor(Color.BLACK);
        for (int i = 0; i < 3; i++) {
            int poleX = centerX - BASE_WIDTH + i * BASE_WIDTH;
            // Base
            g.fillRect(poleX - BASE_WIDTH/2, baseY, BASE_WIDTH, BASE_HEIGHT);
            // Poste
            g.fillRect(poleX - POLE_WIDTH/2, baseY - POLE_HEIGHT, POLE_WIDTH, POLE_HEIGHT);

            // Marcar torre seleccionada
            if (i == selectedTower) {
                g.setColor(Color.YELLOW);
                g.drawOval(poleX - 30, baseY - POLE_HEIGHT - 40, 60, 60);
                g.setColor(Color.BLACK);
            }

            // Etiquetar torres (A, B, C)
            g.drawString(Character.toString((char)('A' + i)), poleX - 5, baseY + 20);
        }

        // Dibujar discos
        Color[] diskColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK};

        for (int t = 0; t < 3; t++) {
            int poleX = centerX - BASE_WIDTH + t * BASE_WIDTH;
            Stack<Integer> tower = towers[t];

            for (int i = 0; i < tower.size(); i++) {
                int diskSize = tower.get(i);
                int diskWidth = MIN_DISK_WIDTH + (MAX_DISK_WIDTH - MIN_DISK_WIDTH) * diskSize / totalDisks;
                int diskX = poleX - diskWidth/2;
                int diskY = baseY - BASE_HEIGHT - (i+1) * DISK_HEIGHT;

                g.setColor(diskColors[diskSize-1]);
                g.fillRect(diskX, diskY, diskWidth, DISK_HEIGHT);
                g.setColor(Color.BLACK);
                g.drawRect(diskX, diskY, diskWidth, DISK_HEIGHT);

                // Mostrar número en disco
                g.setColor(Color.BLACK);
                g.drawString(Integer.toString(diskSize), poleX - 5, diskY + DISK_HEIGHT/2 + 5);
            }
        }
    }

    private void handleTowerClick(int clickX) {
        int centerX = drawingPanel.getWidth() / 2;
        int towerIndex = (clickX - (centerX - BASE_WIDTH * 3 / 2)) / BASE_WIDTH;

        if (towerIndex < 0 || towerIndex > 2) return;

        if (selectedTower == -1) {
            // Seleccionar primera torre
            if (!towers[towerIndex].isEmpty()) {
                selectedTower = towerIndex;
                logArea.append("Seleccionada torre " + (char)('A' + towerIndex) + "\n");
                drawingPanel.repaint();
            }
        } else {
            // Intentar mover disco
            if (selectedTower == towerIndex) {
                // Deseleccionar
                selectedTower = -1;
                logArea.append("Deseleccionada torre " + (char)('A' + towerIndex) + "\n");
            } else {
                moveDisk(selectedTower, towerIndex);
                selectedTower = -1;
            }
            drawingPanel.repaint();
        }
    }

    private void moveDisk(int from, int to) {
        if (towers[from].isEmpty()) {
            logArea.append("¡Torre origen vacía!\n");
            return;
        }

        int disk = towers[from].peek();

        if (!towers[to].isEmpty() && towers[to].peek() < disk) {
            logArea.append("¡Movimiento inválido! No puedes colocar un disco más grande sobre uno más pequeño.\n");
            return;
        }

        towers[from].pop();
        towers[to].push(disk);
        moveCount++;

        logArea.append("Movimiento #" + moveCount + ": Disco " + disk +
                " de " + (char)('A' + from) + " a " +
                (char)('A' + to) + "\n");

        // Verificar si se ha ganado
        if (towers[2].size() == totalDisks) {
            logArea.append("\n¡Felicidades! Has resuelto el puzzle en " + moveCount + " movimientos.\n");
            logArea.append("El mínimo número de movimientos posible es " + ((1 << totalDisks) - 1) + "\n");
            saveResults(); // Guardar resultados al completar
        }
    }
}