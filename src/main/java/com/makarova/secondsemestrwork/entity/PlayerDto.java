package com.makarova.secondsemestrwork.entity;

public class PlayerDto {
    private int connectionId;
    private int x;
    private int y;

    public PlayerDto(Player player) {
        this.connectionId = player.getId();
        this.x = player.getX();
        this.y = player.getY();
    }

    public PlayerDto(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getId() {
        return connectionId;
    }

    public void setId(int connectionId) {
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
}
