package com.makarova.secondsemestrwork.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Pistol {
    private int x, y;
    private ImageView imageView;
    private Image image = new Image("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\shoot.png");

    public Pistol(int x, int y) {
        this.x = x;
        this.y = y;
        imageView = new ImageView(image);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
