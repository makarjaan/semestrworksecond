package com.makarova.secondsemestrwork.entity;

public class PistolDto {
    private int connectionId;
    private int x;
    private int y;
    private String direction;

    public PistolDto(Pistol pistol) {
        this.connectionId = pistol.getPlayerId();
        this.x = (int) pistol.getX();
        this.y = (int) pistol.getY();
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() { return direction; }

    public void setDirection(String direction) { this.direction = direction; }
}
