package com.makarova.secondsemestrwork.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Pistol {
    private double x, y;
    private ImageView imageView;
    Image image = new Image("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\shoot.png");
    private Image imageLoaded = new Image("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\loaded_pistol.png");
    private String direction;
    private int playerId;
    private PistolDto pistolDto;
    private Boolean loaded = false;

    public Pistol(int x, int y) {
        this.x = x;
        this.y = y;
        imageView = new ImageView(image);
        pistolDto = new PistolDto(this);
    }

    public Boolean getLoaded() {
        return loaded;
    }

    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }

    public double getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public void setY(int y) {
        this.y = y;
        pistolDto.setY(y);
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        pistolDto.setX(x);
    }

    public Image getImageLoaded() {
        return imageLoaded;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getDirection() { return direction; }

    public void setDirection(String direction) {
        this.direction = direction;
        pistolDto.setDirection(direction);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public PistolDto getPistolDto() {
        return pistolDto;
    }

    public void setPistolDto(Pistol pistol) {
        this.pistolDto = new PistolDto(this);
    }
}
