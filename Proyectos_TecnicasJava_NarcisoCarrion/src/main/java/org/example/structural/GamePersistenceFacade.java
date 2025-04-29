package org.example.structural;

import org.example.model.GameRecord;
import org.example.creational.DatabaseManager;

public class GamePersistenceFacade {
    private final DatabaseManager dbManager;

    public GamePersistenceFacade() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public void saveNQueensGame(int n, boolean successful, long duration) {
        String params = String.format("{\"n\": %d}", n);
        saveGame("N_QUEENS", params, successful, null, duration);
    }

    public void saveKnightTourGame(int startX, int startY, int moves, boolean successful, long duration) {
        String params = String.format("{\"startX\": %d, \"startY\": %d}", startX, startY);
        saveGame("KNIGHT_TOUR", params, successful, moves, duration);
    }

    public void saveHanoiGame(int disks, int moves, boolean successful, long duration) {
        String params = String.format("{\"disks\": %d}", disks);
        saveGame("HANOI", params, successful, moves, duration);
    }

    public void saveGame(String type, String params, boolean successful, Integer moves, long duration) {
        GameRecord record = new GameRecord(type, params, successful, moves, duration);
        dbManager.getGameRecordDAO().saveGameRecord(record);
    }
}