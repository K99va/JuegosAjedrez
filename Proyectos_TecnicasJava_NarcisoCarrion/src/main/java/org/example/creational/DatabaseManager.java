package org.example.creational;

import org.example.util.HibernateUtil;
import org.example.dao.GameRecordDAO;

public class DatabaseManager {
    private static DatabaseManager instance;
    private GameRecordDAO gameRecordDAO;

    private DatabaseManager() {
        // Inicialización privada
        gameRecordDAO = new GameRecordDAO();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public GameRecordDAO getGameRecordDAO() {
        return gameRecordDAO;
    }

    public void shutdown() {
        HibernateUtil.shutdown();
    }
}