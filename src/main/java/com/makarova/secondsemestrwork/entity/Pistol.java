package com.makarova.secondsemestrwork.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Pistol {
    private double x, y;
    private ImageView imageView;
    Image image = new Image("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\shoot.png");
    private String direction;
    private int playerId;
    private PistolDto pistolDto;

    public Pistol(int x, int y) {
        this.x = x;
        this.y = y;
        imageView = new ImageView(image);
        pistolDto = new PistolDto(this);
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getDirection() { return direction; }

    public void setDirection(String direction) { this.direction = direction; }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public PistolDto getPistolDto() {
        return pistolDto;
    }

    public void setPistolDto(PistolDto pistolDto) {
        this.pistolDto = pistolDto;
    }
}
