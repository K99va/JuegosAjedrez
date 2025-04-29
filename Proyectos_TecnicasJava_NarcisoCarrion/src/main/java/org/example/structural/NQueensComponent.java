package org.example.structural;

import org.example.gui.NQueensGUI;
import java.awt.*;

public class NQueensComponent extends GameComponent {
    private NQueensGUI nQueensGUI;  // Corregido nombre de variable (minúscula)

    public NQueensComponent() {
        this.nQueensGUI = new NQueensGUI();
        this.initialize();  // Inicialización automática al crear el componente
    }

    @Override
    public void initialize() {
        this.setLayout(new BorderLayout());
        // NQueensGUI ahora ES un JPanel (hereda de GameComponent)
        this.add(nQueensGUI, BorderLayout.CENTER);
    }

    @Override
    public void saveResults() {
        // Delegamos la responsabilidad de guardar al NQueensGUI
        nQueensGUI.saveResults();
    }
}