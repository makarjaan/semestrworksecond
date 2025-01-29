package com.makarova.secondsemestrwork.controller;

import java.lang.reflect.Parameter;
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
                playerViews.put(p, imageView);
            });
        }
    }

    private void update() {
        Platform.runLater(() -> {
            for (Player player : players) {
                if (player.getId() == localPlayerId) {
                    updateLocalPlayer(player);
                } else {
                    updateOtherPlayers(player);
                }
            }
        });
    }

    private void updateLocalPlayer(Player player) {
        ImageView imageView = playerViews.get(player);
        if (imageView == null) return;

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
    }

    private void updateOtherPlayers(Player player) {
        ImageView imageView = playerViews.get(player);
        if (imageView == null) return;
        boolean isMoving = player.getPrevX() != player.getX() || player.getPrevY() != player.getY();
        if (isMoving) {
            player.startAnimation();
        } else {
            player.stopAnimation();
        }
        imageView.setLayoutX(player.getX());
        imageView.setLayoutY(player.getY());

        player.setPrevX(player.getX());
        player.setPrevY(player.getY());
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
            case MessegeType.SET_PLAYER_POSITION_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                System.out.println("Получен список игроков: " + json);

                Type listType = new TypeToken<List<PlayerDto>>() {}.getType();
                List<PlayerDto> playersDto = gson.fromJson(json, listType);

                for (PlayerDto p : playersDto) {
                    boolean playerExists = players.stream()
                            .anyMatch(existingPlayer -> existingPlayer.getId() == p.getId());

                    if (!playerExists) {
                        Player player = new Player(p.getX(), p.getY(), p.getId());
                        player.setPrevX(player.getX());
                        player.setPrevY(player.getY());
                        players.add(player);
                    } else {
                        System.out.println("Игрок с id " + p.getId() + " уже существует.");
                    }
                }
                localPlayerId = getApplication().getGameClient().idPlayer;
                updatePlayerViews();
            }

            case MessegeType.PLAYER_POSITION_UPDATE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> receivedData = gson.fromJson(json, type);

                PlayerDto playerDto = gson.fromJson(gson.toJson(receivedData.get("playerDto")), PlayerDto.class);
                int offsetY = ((Double) receivedData.get("offsetY")).intValue();
                Player player = players.get(playerDto.getId());
                player.setX(playerDto.getX());
                player.setY(playerDto.getY());
                Platform.runLater(() -> {
                    ImageView imageView = playerViews.get(player);
                    if (imageView != null) {
                        pane.getChildren().remove(imageView);
                    }
                    imageView = player.getImageSpriteView();
                    imageView.setLayoutX(player.getX());
                    imageView.setLayoutY(player.getY());
                    player.spriteAnimation.setOffsetY(offsetY);
                    if (!pane.getChildren().contains(imageView)) {
                        pane.getChildren().add(imageView);
                    }
                    playerViews.put(player, imageView);
                });
            }
        }
    }
}