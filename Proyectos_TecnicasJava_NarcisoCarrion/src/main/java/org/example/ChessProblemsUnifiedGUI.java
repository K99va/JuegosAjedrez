package org.example;

import org.example.creational.*;
import org.example.structural.GameComponent;
import org.example.structural.HanoiComponent;
import org.example.structural.KnightTourComponent;
import org.example.structural.NQueensComponent;

import javax.swing.*;
import java.awt.*;

public class ChessProblemsUnifiedGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Problemas de Ajedrez - Soluciones Unificadas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 750);

            JTabbedPane tabbedPane = new JTabbedPane();

            // Usando Factory Method para crear los paneles
            GamePanelCreator[] creators = {
                    new HanoiPanelCreator(),
                    new KnightTourPanelCreator(),
                    new NQueensPanelCreator(),
                    new HistoryPanelCreator()
            };

            String[] titles = {"Torres de Hanoi", "Recorrido del Caballo", "N Reinas", "Historial"};

            for (int i = 0; i < creators.length; i++) {
                tabbedPane.addTab(titles[i], creators[i].createGamePanel());
            }

            // Usando Composite
            GameComponent[] components = {
                    new HanoiComponent(),
                    new KnightTourComponent(),
                    new NQueensComponent()
            };

            for (GameComponent component : components) {
                component.initialize();
            }

            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setVisible(true);

            // Registrar shutdown hook usando Singleton
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DatabaseManager.getInstance().shutdown();
            }));
        });
    }
}