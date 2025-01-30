package com.makarova.secondsemestrwork.controller;

import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makarova.secondsemestrwork.entity.*;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static com.makarova.secondsemestrwork.view.BaseView.getApplication;


public class MainController implements MessageReceiverController{

    @FXML
    private Pane pane;

    private Map<Player, ImageView> playerViews = new HashMap<>();
    private Map<Player, ImageView> pistolsView = new HashMap<>();
    private Map<Player, ImageView> bulletViews = new HashMap<>();
    List<Player> players = new ArrayList<>();
    List<ImageView> rocketImages = new ArrayList<>();
    private int localPlayerId;
    private Gson gson = new Gson();
    public boolean left = false;
    public boolean right = false;
    public boolean up = false;
    public boolean down = false;
    public boolean space = false;


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
            checkCollisions();
        });
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


        if (isMoving) {
            player.startAnimation();
        } else {
            player.stopAnimation();
        }

        imageView.setLayoutX(player.getX());
        imageView.setLayoutY(player.getY());

        if (space && player.isLoaded()) {
            shoot(player);
            space = false;
        }

        pistolImageView.setLayoutX(player.getPistol().getX());
        pistolImageView.setLayoutY(player.getPistol().getY());
        String diraction = player.getPistol().getDirection();
        if (diraction != null) {
            updatePistolPosition(player, pistolImageView, diraction);
        }
    }

    private void updateOtherPlayers(Player player) {
        ImageView imageView = playerViews.get(player);
        ImageView pistolImageView = pistolsView.get(player);
        if (imageView == null || pistolImageView == null) return;

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

        pistolImageView.setLayoutX(player.getPistol().getX());
        pistolImageView.setLayoutY(player.getPistol().getY());
        String diraction = player.getPistol().getDirection();
        if (diraction != null) {
            updatePistolPosition(player, pistolImageView, diraction);
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
        pistolImageView.setRotate(rotation);
        pistolImageView.setLayoutX(player.getPistol().getX() + offsetX);
        pistolImageView.setLayoutY(player.getPistol().getY() + offsetY);
    }

    private void checkCollisions() {
        List<ImageView> rocketsToRemove = new ArrayList<>();

        for (Player player : players) {
            ImageView playerView = playerViews.get(player);
            ImageView pistolView = pistolsView.get(player);

            if (playerView == null || pistolView == null) continue;

            for (ImageView rocket : rocketImages) {
                if (playerView.getBoundsInParent().intersects(rocket.getBoundsInParent())) {
                    rocketsToRemove.add(rocket);
                    Pistol pistol = player.getPistol();
                    Image newImage = pistol.getImageLoaded();
                    pistolView.setImage(newImage);
                    player.setPistol(pistol);
                    player.setLoaded(true);
                    pistolsView.put(player, pistolView);
                }
            }
        }
        Platform.runLater(() -> {
            pane.getChildren().removeAll(rocketsToRemove);
            rocketImages.removeAll(rocketsToRemove);
        });
    }

    private ImageView createRocketImage(double x, double y) {
        if ((x == 77 & y == 68) || (x == 486 & y == 438) ||
                (x == 486 & y == 68) || (x == 77 & y == 438)) {
            return null; }
        Image image = new Image("file:C:/Users/arina/IdeaProjects/secondsemestrwork/src/main/resources/image/boosters/rocket.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(35);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setRotate(45);
        return imageView;
    }

    private void shoot(Player player) {
        player.setLoaded(false);
        ImageView pistolImageView = pistolsView.get(player);
        pistolImageView.setImage(player.getPistol().getImage());
        pistolsView.put(player, pistolImageView);

        ImageView bulletImageView = new ImageView("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\bullet.png");
        bulletImageView.setFitHeight(75);
        bulletImageView.setFitWidth(25);
        bulletImageView.setLayoutX(pistolsView.get(player).getLayoutX());
        bulletImageView.setLayoutY(pistolsView.get(player).getLayoutY());
        String bulletDirection = player.getPistol().getDirection();
        pane.getChildren().add(bulletImageView);
        Timeline timeline = new Timeline();
        switch (bulletDirection) {
            case "right" -> {
                bulletImageView.setRotate(90);
                bulletImageView.setLayoutY(bulletImageView.getLayoutY() + 78);
            }
            case "left" -> {
                bulletImageView.setRotate(-90);
                bulletImageView.setLayoutY(bulletImageView.getLayoutY() + 82);
            }
            case "up" -> {
                bulletImageView.setRotate(0);
            }
            case "down" -> {
                bulletImageView.setRotate(180);
                bulletImageView.setLayoutX(bulletImageView.getLayoutX() + 3);
                bulletImageView.setLayoutY(bulletImageView.getLayoutY() + 80);
            }
        }
        KeyFrame keyFrame = new KeyFrame(Duration.millis(10), e -> {
            switch (bulletDirection) {
                case "left" -> {
                    bulletImageView.setLayoutX(bulletImageView.getLayoutX() - 5);
                }
                case "right" -> {
                    bulletImageView.setLayoutX(bulletImageView.getLayoutX() + 5);
                }
                case "up" -> {
                    bulletImageView.setLayoutY(bulletImageView.getLayoutY() - 5);
                }
                case "down" -> {
                    bulletImageView.setLayoutY(bulletImageView.getLayoutY() + 5);
                }
            }

            BulletDto bulletDto = new BulletDto((int) bulletImageView.getLayoutX(),
                    (int) bulletImageView.getLayoutY(), bulletDirection);
            bulletDto.setSenderPlayer(player.getPlayerDto());

            try {
                Message bulletMessage = MessageFactory.create(
                        MessageType.BULLET_UPDATE_TYPE,
                        gson.toJson(bulletDto).getBytes());
                getApplication().getGameClient().sendMessage(bulletMessage);
            } catch (InvalidMessageException ex) {
                ex.printStackTrace();
            } catch (ClientException ex) {
                ex.printStackTrace();
            }


            for (Player p : players) {
                ImageView target = playerViews.get(p);
                if (target != null && bulletImageView.getBoundsInParent().intersects(target.getBoundsInParent())) {
                    pane.getChildren().remove(bulletImageView);
                    timeline.stop();
                    return;
                }
            }
            /*
            for (ImageView wall : walls) {
                if (bulletImageView.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                    pane.getChildren().remove(bulletImageView);
                    ((Timeline) e.getSource()).stop();
                    return;
                }
            }

             */
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
                player.setPistol(pistol);

                Platform.runLater(() -> {
                    ImageView imageView = playerViews.get(player);
                    imageView.setLayoutX(player.getX());
                    imageView.setLayoutY(player.getY());
                    player.spriteAnimation.setOffsetY(offsetY);
                    playerViews.put(player, imageView);
                });
            }

            case MessageType.INIT_ROCKET_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                Type listType = new TypeToken<List<RocketDto>>() {}.getType();
                List<RocketDto> rockets = gson.fromJson(json, listType);
                for (RocketDto r : rockets) {
                    ImageView imageView = createRocketImage(r.getX(), r.getY());
                    if (imageView != null) {
                        rocketImages.add(imageView);
                    }
                }
                ImageView imageView = createRocketImage(77, 68);
                if (imageView != null) {
                    rocketImages.add(imageView);
                }
            }

            case MessageType.GENERATE_ROCKET_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                RocketDto rocketDto = gson.fromJson(json, RocketDto.class);
                ImageView imageView = createRocketImage(rocketDto.getX(), rocketDto.getY());
                if (imageView != null) {
                    rocketImages.add(imageView);
                }
            }

            case MessageType.BULLET_UPDATE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                BulletDto bulletDto = gson.fromJson(json, BulletDto.class);
                Player player = players.get(bulletDto.getSenderPlayer().getId());
                PlayerDto playerDto = bulletDto.getSenderPlayer();
                ImageView pistolImageView = pistolsView.get(player);

                if (playerDto.isLoaded()) {
                    pistolImageView.setImage(player.getPistol().getImageLoaded());
                } else {
                    pistolImageView.setImage(player.getPistol().getImage());
                }

                Platform.runLater(() -> {
                    ImageView bulletImageView = bulletViews.get(player);
                    if (bulletImageView == null) {
                        bulletImageView = new ImageView("C:\\Users\\arina\\IdeaProjects\\secondsemestrwork\\src\\main\\resources\\image\\boosters\\bullet.png");
                        bulletImageView.setFitHeight(75);
                        bulletImageView.setFitWidth(25);
                        pane.getChildren().add(bulletImageView);
                        bulletViews.put(player, bulletImageView);
                    }

                    bulletImageView.setLayoutX(bulletDto.getX());
                    bulletImageView.setLayoutY(bulletDto.getY());

                    switch (bulletDto.getDirection()) {
                        case "left" -> bulletImageView.setRotate(-90);
                        case "right" -> bulletImageView.setRotate(90);
                        case "up" -> bulletImageView.setRotate(0);
                        case "down" -> bulletImageView.setRotate(180);
                    }
                });
            }


        }
    }
}