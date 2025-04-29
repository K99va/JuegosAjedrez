package org.example.structural;

import javax.swing.JPanel;

public abstract class GameComponent extends JPanel {
    public abstract void initialize();
    public abstract void saveResults();
}