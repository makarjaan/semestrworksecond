package com.makarova.secondsemestrwork.entity;

import com.makarova.secondsemestrwork.sprite.SpriteAnimation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class Player {
    public ImageView imageSpriteView;
    public SpriteAnimation spriteAnimation;
    public int x, y;
    public int speed;
    public int id;

    public Player(int id) {
        this.id = id;
    }

    public Player(int x, int y, int id) {
        this.speed = 2;
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public void setimageSpriteView(String url) {
        Image personImage = new Image(url);
        this.imageSpriteView = new ImageView(personImage);
        this.imageSpriteView.setViewport(new Rectangle2D(0, 0, 47, 48));
        spriteAnimation = new SpriteAnimation(
                imageSpriteView, Duration.millis(600),
                12, 12,
                0, 0,
                47, 48);
    }

    public Player getById(int id) {
        return this;
    }


    public void moveUp() {
        y -= speed;
        spriteAnimation.setOffsetY(144);
    }

    public void moveDown() {
        y += speed;
        spriteAnimation.setOffsetY(48);
    }

    public void moveLeft() {
        x -= speed;
        spriteAnimation.setOffsetY(0);
    }

    public void moveRight() {
        x += speed;
        spriteAnimation.setOffsetY(96);
    }

    public ImageView getImageSpriteView() {
        if (imageSpriteView == null) {
            imageSpriteView = new ImageView();
        }
        return imageSpriteView;
    }

    public void setImageSpriteView(ImageView imageSpriteView) {
        this.imageSpriteView = imageSpriteView;
    }

    public SpriteAnimation getSpriteAnimation() {
        return spriteAnimation;
    }

    public void setSpriteAnimation(SpriteAnimation spriteAnimation) {
        this.spriteAnimation = spriteAnimation;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void stopAnimation() {
        spriteAnimation.stop();
        imageSpriteView.setViewport(new Rectangle2D(0, spriteAnimation.getOffsetY(), 47, 48));
    }

    public void startAnimation() {
        spriteAnimation.play();
    }

}