package org.example.creational;

import org.example.gui.HistoryPanel;
import javax.swing.JPanel;

public class HistoryPanelCreator implements GamePanelCreator {
    @Override
    public JPanel createGamePanel() {
        return new HistoryPanel();
    }
}