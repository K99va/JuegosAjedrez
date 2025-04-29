package org.example.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "game_records")
public class GameRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_type", nullable = false)
    private String gameType; // "N_QUEENS", "KNIGHT_TOUR", "HANOI"

    @Column(name = "game_parameters")
    private String gameParameters; // JSON o datos específicos del juego

    @Column(name = "successful", nullable = false)
    private boolean successful;

    @Column(name = "moves_count")
    private Integer movesCount;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "played_at", nullable = false)
    private Date playedAt;

    // Constructores
    public GameRecord() {
        this.playedAt = new Date();
    }

    public GameRecord(String gameType, String gameParameters, boolean successful,
                      Integer movesCount, Long durationMs) {
        this();
        this.gameType = gameType;
        this.gameParameters = gameParameters;
        this.successful = successful;
        this.movesCount = movesCount;
        this.durationMs = durationMs;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameParameters() {
        return gameParameters;
    }

    public void setGameParameters(String gameParameters) {
        this.gameParameters = gameParameters;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Integer getMovesCount() {
        return movesCount;
    }

    public void setMovesCount(Integer movesCount) {
        this.movesCount = movesCount;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Date getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Date playedAt) {
        this.playedAt = playedAt;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "id=" + id +
                ", gameType='" + gameType + '\'' +
                ", gameParameters='" + gameParameters + '\'' +
                ", successful=" + successful +
                ", movesCount=" + movesCount +
                ", durationMs=" + durationMs +
                ", playedAt=" + playedAt +
                '}';
    }
}