package org.example.creational;

import org.example.gui.HanoiGUI;
import javax.swing.JPanel;

public class HanoiPanelCreator implements GamePanelCreator {
    @Override
    public JPanel createGamePanel() {
        return new HanoiGUI();
    }
}