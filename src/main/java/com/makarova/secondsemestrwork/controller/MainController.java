package com.makarova.secondsemestrwork.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.sprite.SpriteAnimation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class MainController {

    private Player player;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane pane;

    @FXML
    private ImageView playerView;

    public boolean left = false;
    public boolean right = false;
    public boolean up = false;
    public boolean down = false;

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            update();
        }
    };

    @FXML
    void initialize() {
        player = new Player(300, 300);
        playerView.setImage(player.getImageView().getImage());
        playerView.setLayoutX(player.getX());
        playerView.setLayoutY(player.getY());
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> generateRandomImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        timer.start();
    }

    private void update() {
        double paneWidth = playerView.getParent().getLayoutBounds().getWidth();
        double paneHeight = playerView.getParent().getLayoutBounds().getHeight();
        double playerWidth = playerView.getFitWidth();
        double playerHeight = playerView.getFitHeight();

        playerView.setViewport(player.imageView.getViewport());
        if (right & player.getX() + playerWidth + player.getSpeed() <= paneWidth) {
            player.moveRight();
            playerView.setLayoutX(player.getX());
        } else if (left && player.getX() - player.getSpeed() >= 0) {
            player.moveLeft();
            playerView.setLayoutX(player.getX());
        } else if (up && player.getY() - player.getSpeed() >= 0) {
            player.moveUp();
            playerView.setLayoutY(player.getY());
        } else if (down && player.getY() + playerHeight + player.getSpeed() <= paneHeight) {
            player.moveDown();
            playerView.setLayoutY(player.getY());
        }

        if (right || left || up || down) {
            player.startAnimation();
        } else {
            player.stopAnimation();
        }
        checkCollisions();
    }

    private void generateRandomImage() {
        Image randomImage = new Image("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\rocket.png");
        double xPos = Math.random() * (pane.getWidth() - 64);
        double yPos = Math.random() * (pane.getHeight() - 64);
        ImageView imageView = new ImageView(randomImage);
        imageView.setFitWidth(20);
        imageView.setFitHeight(35);
        imageView.setLayoutX(xPos);
        imageView.setLayoutY(yPos);
        imageView.setRotate(45);
        pane.getChildren().add(imageView);
    }

    private void checkCollisions() {
        Bounds playerBounds = playerView.getBoundsInParent();
        pane.getChildren().removeIf(node -> {
            if (node instanceof ImageView && node != playerView) {
                Bounds rocketBounds = node.getBoundsInParent();
                if (playerBounds.intersects(rocketBounds)) {
                    return true;
                }
            }
            return false;
        });
    }


}
