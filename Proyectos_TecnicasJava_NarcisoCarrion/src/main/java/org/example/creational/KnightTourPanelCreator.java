package org.example.creational;

import org.example.gui.KnightTourGUI;
import javax.swing.*;

public class KnightTourPanelCreator implements GamePanelCreator {
    @Override
    public JPanel createGamePanel() {
        return new KnightTourGUI();
    }
}