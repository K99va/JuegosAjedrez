package org.example.structural;

import org.example.gui.KnightTourGUI;
import java.awt.*;

public class KnightTourComponent extends GameComponent {
    private KnightTourGUI knightTourGUI;

    public KnightTourComponent() {
        this.knightTourGUI = new KnightTourGUI();
    }

    @Override
    public void initialize() {
        this.setLayout(new BorderLayout());
        // KnightTourGUI ahora ES un JPanel (hereda de GameComponent)
        this.add(knightTourGUI, BorderLayout.CENTER);
    }

    @Override
    public void saveResults() {
        // Delegamos la responsabilidad de guardar al KnightTourGUI
        knightTourGUI.saveResults();
    }
}