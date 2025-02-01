package com.makarova.secondsemestrwork.entity;

import java.util.Objects;

public class ObstacleDto {
    private int x;
    private int y;
    private String id;
    private String direction;

    public ObstacleDto(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public String getKey() {
        return id;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObstacleDto that = (ObstacleDto) o;
        return x == that.x && y == that.y && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, id);
    }
}
