package com.makarova.secondsemestrwork.entity;

public class PlayerDto {
    private int connectionId;
    private int x;
    private int y;
    private boolean loaded = false;
    private String name;
    private int lifeScore;
    private boolean movementEnabled;
    private boolean isHit = false;

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

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
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

    public int getLifeScore() {
        return lifeScore;
    }

    public void setLifeScore(int lifeScore) {
        this.lifeScore = lifeScore;
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public void setMovementEnabled(boolean movementEnabled) {
        this.movementEnabled = movementEnabled;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
