package com.makarova.secondsemestrwork.controller;

import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makarova.secondsemestrwork.entity.*;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageType;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import static com.makarova.secondsemestrwork.view.BaseView.getApplication;


public class MainController implements MessageReceiverController{

    @FXML
    private Pane pane;

    private Map<Player, ImageView> playerViews = new HashMap<>();
    private Map<Player, ImageView> pistolsView = new HashMap<>();
    public List<Player> players = new ArrayList<>();
    List<ImageView> rocketImages = new ArrayList<>();
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
        timer.start();
    }

    private void updatePlayerViews() {
        for (Player p : players) {
            Platform.runLater(() -> {
                if (p.getId() == 0) {
                    p.setimageSpriteView("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\player\\red.png");
                    p.spriteAnimation.setOffsetY(96);
                } else {
                    p.setimageSpriteView("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\player\\blue.png");
                }
                if (playerViews.containsKey(p)) {
                    pane.getChildren().remove(playerViews.get(p));
                }

                if (!pistolsView.containsKey(p)) {
                    Pistol pistol = new Pistol(p.getX(), p.getY());
                    p.setPistol(pistol);
                    ImageView pistolImageView = pistol.getImageView();
                    if (p.getId() == 1 || p.getId() == 2) {
                        pistol.setX(p.getX() - 73);
                        pistol.setY(p.getY() - 113);
                        pistolImageView.setRotate(-90);
                    } else {
                        pistol.setX(p.getX() + 113);
                        pistol.setY(p.getY() - 57);
                        pistolImageView.setRotate(90);
                    }
                    pistolImageView.setFitWidth(25);
                    pistolImageView.setFitHeight(236);
                    pistolImageView.setLayoutX(pistol.getX());
                    pistolImageView.setLayoutY(pistol.getY());

                    pistolsView.put(p, pistolImageView);
                    p.setPistol(pistol);
                    pane.getChildren().add(pistol.getImageView());
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
            List<ImageView> rocketImagesCopy = new ArrayList<>(rocketImages);
            for (ImageView rocketImage : rocketImagesCopy) {
                if (!pane.getChildren().contains(rocketImage)) {
                    pane.getChildren().add(rocketImage);
                }
            }
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
        ImageView pistolImageView = pistolsView.get(player);
        if (imageView == null) return;
        if (pistolImageView == null) return;

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

        pistolImageView.setLayoutX(player.getPistol().getX());
        pistolImageView.setLayoutY(player.getPistol().getY());
        String diraction = player.getPistol().getDirection();
        if (diraction != null) {
            updatePistolPosition(player, pistolImageView, diraction);
        }
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

        ImageView pistolImageView = pistolsView.get(player);
        if (pistolImageView == null) return;

        Pistol pistol = player.getPistol();
        if (pistol == null) return;

        String direction = pistol.getDirection();
        if (direction != null) {
            updatePistolPosition(player, pistolImageView, direction);
        }
    }

    private void updatePistolPosition(Player player, ImageView pistolImageView, String direction) {
        double offsetX = 0, offsetY = 0;
        int rotation = 0;

        if (player.getId() == 0 || player.getId() == 3) {
            switch (direction) {
                case "left" -> { offsetX = -185; offsetY = -56; rotation = -90; }
                case "right" -> { rotation = 90; }
                case "up" -> { offsetX = -64; offsetY = -121; }
                case "down" -> { offsetX = -123; offsetY = 64; rotation = 180; }
            }
        } else if (player.getId() == 1 || player.getId() == 2) {
            switch (direction) {
                case "left" -> { rotation = -90; }
                case "right" -> { offsetX = 185; offsetY = 56; rotation = 90; }
                case "up" -> { offsetX = 123; offsetY = -64; }
                case "down" -> { offsetX = 64; offsetY = 121; rotation = 180; }
            }
        }

        pistolImageView.setLayoutX(player.getPistol().getX() + offsetX);
        pistolImageView.setLayoutY(player.getPistol().getY() + offsetY);
        pistolImageView.setRotate(rotation);
    }

    private ImageView createRocketImage(double x, double y) {
        Image image = new Image("file:C:/Users/arina/IdeaProjects/secondsemestrwork/src/main/resources/image/boosters/rocket.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(35);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setRotate(45);
        return imageView;
    }


    private void checkCollisions() {
        for (Player player : players) {
            if (player.id == localPlayerId) {
                Bounds playerBounds = player.getImageSpriteView().getBoundsInParent();
                pane.getChildren().removeIf(node -> {
                    if (node instanceof ImageView && node != player.getImageSpriteView()) {
                        Bounds rocketBounds = node.getBoundsInParent();
                        if (playerBounds.intersects(rocketBounds)) {
                            return true;
                        }
                    }
                    return false;
                });
            }
        }
    }



    @Override
    public void receiveMessage(Message message) {
        switch (message.getType()) {
            case MessageType.SET_PLAYER_POSITION_TYPE -> {
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

            case MessageType.PLAYER_POSITION_UPDATE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> receivedData = gson.fromJson(json, type);
                System.out.println(json);

                PlayerDto playerDto = gson.fromJson(gson.toJson(receivedData.get("playerDto")), PlayerDto.class);
                PistolDto pistolDto = gson.fromJson(gson.toJson(receivedData.get("pistolDto")), PistolDto.class);
                int offsetY = ((Double) receivedData.get("offsetY")).intValue();

                Player player = players.get(playerDto.getId());
                player.setX(playerDto.getX());
                player.setY(playerDto.getY());

                Pistol pistol = player.getPistol();
                pistol.setX(pistolDto.getX());
                pistol.setY(pistolDto.getY());
                pistol.setDirection(pistolDto.getDirection());

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

            case MessageType.INIT_ROCKET_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                Type listType = new TypeToken<List<RocketDto>>() {}.getType();
                List<RocketDto> rockets = gson.fromJson(json, listType);
                for (RocketDto r : rockets) {
                    rocketImages.add(createRocketImage(r.getX(), r.getY()));
                }
            }

            case MessageType.GENERATE_ROCKET_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                RocketDto rocketDto = gson.fromJson(json, RocketDto.class);
                rocketImages.add(createRocketImage(rocketDto.getX(), rocketDto.getY()));
            }
        }
    }
}