package org.example.structural;

import org.example.gui.HanoiGUI;
import java.awt.*;

public class HanoiComponent extends GameComponent {
    private HanoiGUI hanoiGUI;

    public HanoiComponent() {
        this.hanoiGUI = new HanoiGUI();
    }

    @Override
    public void initialize() {
        this.setLayout(new BorderLayout());
        // HanoiGUI ahora ES un JPanel, lo añadimos directamente
        this.add(hanoiGUI, BorderLayout.CENTER);
    }

    @Override
    public void saveResults() {
        // Delegamos la responsabilidad de guardar al HanoiGUI
        hanoiGUI.saveResults();
    }
}