package com.makarova.secondsemestrwork.controller;

import java.lang.reflect.Type;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makarova.secondsemestrwork.client.GameClient;
import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessegeType;
import com.makarova.secondsemestrwork.sprite.SpriteAnimation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static com.makarova.secondsemestrwork.view.BaseView.getApplication;


public class MainController implements MessageReceiverController{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane pane;

    private Map<Player, ImageView> playerViews = new HashMap<>();
    public List<Player> players = new ArrayList<>();
    private int localPlayerId;
    private Gson gson = new Gson();
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
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> generateRandomImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        timer.start();
    }

    private void updatePlayerViews() {
        for (Player p : players) {
            Platform.runLater(() -> {
                if (p.getId() == 0) {
                    p.setimageSpriteView("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\playerRed\\red.png");
                } else {
                    p.setimageSpriteView("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\blue\\blue.png");
                }

                if (playerViews.containsKey(p)) {
                    pane.getChildren().remove(playerViews.get(p));
                }
                ImageView imageView = p.getImageSpriteView();
                imageView.setFitWidth(64);
                imageView.setFitHeight(64);
                imageView.setLayoutX(p.getX());
                imageView.setLayoutY(p.getY());

                pane.getChildren().add(imageView);
                System.out.println("ImageView count: " + pane.getChildren().size());

                playerViews.put(p, imageView);
            });
        }
    }



    private void update() {
        for (Player player : players) {
            if (player.getId() == localPlayerId) {
                Platform.runLater(() -> {
                    ImageView imageView = playerViews.get(player);
                    double paneWidth = pane.getWidth();
                    double paneHeight = pane.getHeight();
                    double playerWidth = imageView.getFitWidth();
                    double playerHeight = imageView.getFitHeight();

                    boolean isMoving = false;

                    if (right ^ left ^ up ^ down) {
                        if (right && player.getX() + playerWidth + player.getSpeed() <= paneWidth) {
                            player.moveRight();
                            isMoving = true;
                        } else if (left && player.getX() - player.getSpeed() >= 0) {
                            player.moveLeft();
                            isMoving = true;
                        } else if (up && player.getY() - player.getSpeed() >= 0) {
                            player.moveUp();
                            isMoving = true;
                        } else if (down && player.getY() + playerHeight + player.getSpeed() <= paneHeight) {
                            player.moveDown();
                            isMoving = true;
                        }
                    }

                    if (isMoving) {
                        player.startAnimation();
                    } else {
                        player.stopAnimation();
                    }

                    imageView.setLayoutX(player.getX());
                    imageView.setLayoutY(player.getY());
                });
            }
       //     checkCollisions();
        }

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
/*
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


 */


    @Override
    public void receiveMessage(Message message) {
        switch (message.getType()) {
            case MessegeType.PLAYER_POSITION_UPDATE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                System.out.println("Получен список игроков: " + json);

                Type listType = new TypeToken<List<PlayerDto>>() {}.getType();
                List<PlayerDto> playersDto = gson.fromJson(json, listType);

                for (PlayerDto p : playersDto) {
                    Player player = new Player(p.getX(), p.getY(), p.getId());
                    players.add(player);
                }
                localPlayerId = getApplication().getGameClient().idPlayer;
                updatePlayerViews();
            }
        }
    }
}