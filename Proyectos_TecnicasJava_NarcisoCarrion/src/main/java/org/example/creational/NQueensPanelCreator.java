package org.example.creational;

import org.example.gui.NQueensGUI;
import javax.swing.*;

public class NQueensPanelCreator implements GamePanelCreator {
    @Override
    public JPanel createGamePanel() {
        return new NQueensGUI();
    }
}