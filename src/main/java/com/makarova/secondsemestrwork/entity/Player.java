package com.makarova.secondsemestrwork.entity;

import com.makarova.secondsemestrwork.sprite.SpriteAnimation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Player extends Entity {

    Image personRedImage = new Image("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\playerRed\\red.png");

    public Player(int x, int y) {
        this.imageView = new ImageView(personRedImage);
        this.imageView.setViewport(new Rectangle2D(0, 0, 47, 48));
        spriteAnimation = new SpriteAnimation(
                imageView, Duration.millis(600),
                12, 12,
                0, 0,
                47, 48);
        this.speed = 2;
        this.x = x;
        this.y = y;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public ImageView getImageView() { return imageView; }

    public void stopAnimation() {
        spriteAnimation.stop();
        imageView.setViewport(new Rectangle2D(0, spriteAnimation.getOffsetY(), 47, 48));
    }

    public void startAnimation() {
        spriteAnimation.play();
    }

}
