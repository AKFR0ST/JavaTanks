package edu.school21.tanks.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameStatement implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int hpPlayer1;
    private int hpPlayer2;
    private int positionPlayer1;
    private int positionPlayer2;
    private List<Shot> shots;

    int playerOneTotalShot;
    int playerTwoTotalShot;
    int playerOneMissingShot;
    int playerTwoMissingShot;

    boolean gameOver;

    public GameStatement() {
        hpPlayer1 = 100;
        hpPlayer2 = 100;
        positionPlayer1 = 150;
        positionPlayer2 = 150;
        shots = new ArrayList<Shot>();
        playerOneTotalShot = 0;
        playerTwoTotalShot = 0;
        playerOneMissingShot = 0;
        playerTwoMissingShot = 0;
        gameOver = false;
    }

    public GameStatement(int hpPlayer1, int hpPlayer2, int positionPlayer1, int positionPlayer2, int playerOneTotalShot, int playerTwoTotalShot, int playerOneMissingShot, int playerTwoMissingShot, List<Shot> shots, boolean gameOver) {
        this.hpPlayer1 = hpPlayer1;
        this.hpPlayer2 = hpPlayer2;
        this.positionPlayer1 = positionPlayer1;
        this.positionPlayer2 = positionPlayer2;
        this.playerOneTotalShot = playerOneTotalShot;
        this.playerTwoTotalShot = playerTwoTotalShot;
        this.playerOneMissingShot = playerOneMissingShot;
        this.playerTwoMissingShot = playerTwoMissingShot;
        this.shots = shots;
        this.gameOver = gameOver;
    }

    public int getHpPlayer1() {
        return hpPlayer1;
    }

    public void setHpPlayer1(int hpPlayer1) {
        this.hpPlayer1 = hpPlayer1;
    }

    public int getHpPlayer2() {
        return hpPlayer2;
    }

    public void setHpPlayer2(int hpPlayer2) {
        this.hpPlayer2 = hpPlayer2;
    }

    public int getPositionPlayer1() {
        return positionPlayer1;
    }

    public void setPositionPlayer1(int positionPlayer1) {
        this.positionPlayer1 = positionPlayer1;
    }

    public int getPositionPlayer2() {
        return positionPlayer2;
    }

    public void setPositionPlayer2(int positionPlayer2) {
        this.positionPlayer2 = positionPlayer2;
    }

    public List<Shot> getShots() {
        return shots;
    }

    public void addShot(Shot shot) {
        this.shots.add(shot);
    }

    public int getPlayerOneTotalShot() {
        return playerOneTotalShot;
    }

    public int getPlayerTwoTotalShot() {
        return playerTwoTotalShot;
    }

    public void incrementPlayerOneTotalShot() {
        playerOneTotalShot++;
    }

    public void incrementPlayerTwoTotalShot() {
        playerTwoTotalShot++;
    }

    public void incrementPlayerOneMissingShot() {
        playerOneMissingShot++;
    }

    public void incrementPlayerTwoMissingShot() {
        playerTwoMissingShot++;
    }

    public int getPlayerOneMissingShot() {
        return playerOneMissingShot;
    }

    public int getPlayerTwoMissingShot() {
        return playerTwoMissingShot;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public String toString() {
        return "GameStatement{" +
                "hpPlayer1=" + hpPlayer1 +
                ", hpPlayer2=" + hpPlayer2 +
                ", positionPlayer1=" + positionPlayer1 +
                ", positionPlayer2=" + positionPlayer2 +
                ", shots=" + shots +
                ", playerOneTotalShot=" + playerOneTotalShot +
                ", playerTwoTotalShot=" + playerTwoTotalShot +
                ", playerOneMissingShot=" + playerOneMissingShot +
                ", playerTwoMissingShot=" + playerTwoMissingShot +
                ", gameOver=" + gameOver +
                '}';
    }

    @Override
    public GameStatement clone() {
        try {
            GameStatement clone = (GameStatement) super.clone();
            clone.hpPlayer1 = hpPlayer1;
            clone.hpPlayer2 = hpPlayer2;
            clone.positionPlayer1 = positionPlayer1;
            clone.positionPlayer2 = positionPlayer2;
            clone.shots = shots;
            clone.playerOneTotalShot = playerOneTotalShot;
            clone.playerTwoTotalShot = playerTwoTotalShot;
            clone.playerOneMissingShot = playerOneMissingShot;
            clone.playerTwoMissingShot = playerTwoMissingShot;
            clone.gameOver = gameOver;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
