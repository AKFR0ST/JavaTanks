package edu.school21.tanks.models;

import java.io.Serializable;

public class Shot implements Serializable {
    private int x;
    private int y;
    private boolean onUp;

    public Shot(int x, int y, boolean onUp) {
        this.x = x;
        this.y = y;
        this.onUp = onUp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isOnUp() {
        return onUp;
    }

    @Override
    public String toString() {
        return "Shot{" +
                "x=" + x +
                ", y=" + y +
                ", onUp=" + onUp +
                '}';
    }

}
