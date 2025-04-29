package org.example.gui;

import org.example.model.GameRecord;
import org.example.util.HibernateUtil;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private JTable historyTable;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadHistory();
    }

    private void initializeComponents() {
        historyTable = new JTable();
        historyTable.setModel(new DefaultTableModel(
                new Object[]{"Juego", "Parámetros", "Éxito", "Movimientos", "Duración", "Fecha"}, 0
        ));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Actualizar");
        refreshButton.addActionListener(e -> loadHistory());
        add(refreshButton, BorderLayout.SOUTH);
    }

    private void loadHistory() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0); // Limpiar tabla

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<GameRecord> records = session.createQuery("FROM GameRecord ORDER BY playedAt DESC", GameRecord.class)
                    .setMaxResults(50)
                    .getResultList();

            for (GameRecord record : records) {
                model.addRow(new Object[]{
                        record.getGameType(),
                        record.getGameParameters(),
                        record.isSuccessful() ? "Sí" : "No",
                        record.getMovesCount(),
                        record.getDurationMs() + " ms",
                        record.getPlayedAt()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el historial", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}