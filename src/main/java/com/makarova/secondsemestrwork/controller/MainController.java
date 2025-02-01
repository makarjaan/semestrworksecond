package com.makarova.secondsemestrwork.controller;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makarova.secondsemestrwork.entity.*;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.model.UserConfig;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;



import static com.makarova.secondsemestrwork.view.BaseView.getApplication;



public class MainController implements MessageReceiverController{

    @FXML
    private Pane pane;
    @FXML
    private ImageView lifeBlue;
    @FXML
    private ImageView lifeGreen;
    @FXML
    private ImageView lifeRed;
    @FXML
    private ImageView lifeYellow;
    @FXML
    private ImageView wallBottom;
    @FXML
    private ImageView wallLeft;
    @FXML
    private ImageView wallRight;
    @FXML
    private ImageView wallTop;

    private Map<Player, ImageView> playerViews = new HashMap<>();
    private Map<Player, ImageView> pistolsView = new HashMap<>();
    private Map<Player, ImageView> bulletViews = new HashMap<>();
    private Map<Player, ImageView> lifeViews = new HashMap<>();
    private Map<String, ImageView> obstacleViews = new HashMap<>();
    private List<ImageView> walls = new ArrayList<>();
    public static List<Player> players = new ArrayList<>();
    List<ImageView> rocketImages = new ArrayList<>();
    List<ImageView> obstacleImages = new ArrayList<>();
    private int localPlayerId;
    private Gson gson = new Gson();
    public boolean left = false;
    public boolean right = false;
    public boolean up = false;
    public boolean down = false;
    public boolean space = false;
    private boolean gameStarted = false;
    private MediaPlayer mediaPlayer;



    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            update();
        }
    };

    @FXML
    void initialize() {
        playBackgroundMusic();
        timer.start();
        gameStarted = true;
    }

    private void playBackgroundMusic() {
        String musicPath = "/music/for_background.mp3";
        Media media = new Media(getClass().getResource(musicPath).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.4);
        mediaPlayer.play();
    }

    private void playShootSound() {
        String soundPath = "/music/shoot.mp3";
        Media sound = new Media(getClass().getResource(soundPath).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setVolume(0.7);
        mediaPlayer.play();
    }


    private void update() {
        Platform.runLater(() -> {
            List<ImageView> rocketImagesCopy = new ArrayList<>(rocketImages);
            for (ImageView rocketImage : rocketImagesCopy) {
                if (!pane.getChildren().contains(rocketImage)) {
                    pane.getChildren().add(rocketImage);
                }
            }
            List<ImageView> obstacleImagesCopy = new ArrayList<>(obstacleImages);
            for (ImageView obstacleImage : obstacleImagesCopy) {
                if (!pane.getChildren().contains(obstacleImage)) {
                    pane.getChildren().add(obstacleImage);
                }
            }
            for (Player player : players) {
                if (player.getId() == localPlayerId) {
                    updateLocalPlayer(player);
                    checkObstacleCollisions(player);
                } else {
                    updateOtherPlayers(player);
                }
            }
            checkCollisions();
        });
    }

    private void checkObstacleCollisions(Player player) {
        ImageView playerView = playerViews.get(player);
        if (playerView == null) return;

        Set<ImageView> movedObstacles = new HashSet<>();
        for (ImageView obstacle : obstacleImages) {
            if (playerView.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                pushObstacle(player, obstacle, movedObstacles);
            }
        }
    }

    private void pushObstacle(Player player, ImageView obstacle, Set<ImageView> movedObstacles) {
        if (movedObstacles.contains(obstacle)) return;
        movedObstacles.add(obstacle);

        double pushDistance = 5;
        double newX = obstacle.getLayoutX();
        double newY = obstacle.getLayoutY();

        String obstacleKey = null;
        String obstacleId = null;

        for (Map.Entry<String, ImageView> entry : obstacleViews.entrySet()) {
            if (entry.getValue().equals(obstacle)) {
                obstacleKey = entry.getKey();
                obstacleId = obstacleKey;
                break;
            }
        }
        System.out.println("первая проверка " + obstacleKey);

        String direction = "";
        if (right) {
            newX += pushDistance;
            direction = "right";
        }
        if (left) {
            newX -= pushDistance;
            direction = "left";
        }
        if (up) {
            newY -= pushDistance;
            direction = "up";
        }
        if (down) {
            newY += pushDistance;
            direction = "down";
        }

        if (newX < 0) newX = 0;
        if (newY < 0) newY = 0;
        if (newX + obstacle.getFitWidth() > pane.getWidth()) {
            newX = pane.getWidth() - obstacle.getFitWidth();
        }
        if (newY + obstacle.getFitHeight() > pane.getHeight()) {
            newY = pane.getHeight() - obstacle.getFitHeight();
        }

        for (ImageView otherObstacle : obstacleImages) {
            if (otherObstacle != obstacle &&
                    otherObstacle.getBoundsInParent().intersects(newX, newY, obstacle.getFitWidth(), obstacle.getFitHeight())) {
                pushObstacle(player, otherObstacle, movedObstacles);
            }
        }

        obstacle.setLayoutX(newX);
        obstacle.setLayoutY(newY);

        obstacleViews.put(obstacleId, obstacle);
        ObstacleDto updatedObstacleDto = new ObstacleDto((int) newX, (int) newY, obstacleId);

        updatedObstacleDto.setDirection(direction);
        try {
            Message obstacleUpdateMessage = MessageFactory.create(
                    MessageType.UPDATE_OBSTACLE_TYPE,
                    gson.toJson(updatedObstacleDto).getBytes()
            );
            getApplication().getGameClient().sendMessage(obstacleUpdateMessage);
        } catch (InvalidMessageException | ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleRemoval(ImageView object, List<ImageView> list, int delayMillis) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(delayMillis), e -> {
            Platform.runLater(() -> {
                pane.getChildren().remove(object);
                list.remove(object);
            });
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void checkForWinner() {
        if (!gameStarted) return;

        List<Player> alivePlayers = players.stream()
                .filter(player -> player.getLifeScore() > 0)
                .collect(Collectors.toList());

        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.get(0);
            Platform.runLater(() -> {
                disconnectAllPlayers();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Победа!");
                alert.setHeaderText(null);
                alert.setContentText("Победитель: " + winner.getName());
                alert.getButtonTypes().clear();
                alert.initModality(Modality.APPLICATION_MODAL);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setOnCloseRequest(event -> event.consume());
                alert.show();
            });
        }

    }

    private void disconnectAllPlayers() {
        getApplication().getGameClient().stopClient();
    }

    private void updatePlayerViews() {
        for (Player p : players) {
            Platform.runLater(() -> {
                if (p.getId() == 0) {
                    String imagePath = "/image/player/playerRed/red.png";
                    p.setimageSpriteView(getClass().getResource(imagePath).toExternalForm());
                    p.spriteAnimation.setOffsetY(96);
                    lifeViews.put(p, lifeRed);
                } else if (p.getId() == 1) {
                    String imagePath = "/image/player/playerBlue/blue.png";
                    p.setimageSpriteView(getClass().getResource(imagePath).toExternalForm());
                    lifeViews.put(p, lifeBlue);
                } else if (p.getId() == 2) {
                    String imagePath = "/image/player/playerGreen/green.png";
                    p.setimageSpriteView(getClass().getResource(imagePath).toExternalForm());
                    p.spriteAnimation.setOffsetY(96);
                    lifeViews.put(p, lifeGreen);
                } else if (p.getId() == 3) {
                    String imagePath = "/image/player/playerYellow/yellow.png";
                    p.setimageSpriteView(getClass().getResource(imagePath).toExternalForm());
                    lifeViews.put(p, lifeYellow);
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
        if (player.getLifeScore() == 0) return;
        ImageView imageView = playerViews.get(player);
        ImageView pistolImageView = pistolsView.get(player);
        if (imageView == null) return;
        if (pistolImageView == null) return;

        double paneWidth = pane.getWidth() - 45;
        double paneHeight = pane.getHeight() - 45;
        double playerWidth = imageView.getFitWidth();
        double playerHeight = imageView.getFitHeight();
        boolean isMoving = false;


        if (right && player.getX() + playerWidth + player.getSpeed() <= paneWidth) {
            player.moveRight();
            isMoving = true;
        } else if (left && player.getX() - player.getSpeed() - 45>= 0) {
            player.moveLeft();
            isMoving = true;
        } else if (up && player.getY() - player.getSpeed() - 45>= 0) {
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

        ImageView lifeImageView = lifeViews.get(player);
        if (lifeImageView == null) return;
        if (player.getId() == 0) {
            lifeRed.setImage(lifeImageView.getImage());
        } else if (player.getId() == 1) {
            lifeBlue.setImage(lifeImageView.getImage());
        } else if (player.getId() == 2) {
            lifeGreen.setImage(lifeImageView.getImage());
        } else if (player.getId() == 3) {
            lifeYellow.setImage(lifeImageView.getImage());
        }
    }

    private void updateOtherPlayers(Player player) {
        if (player.getLifeScore() == 0) return;
        if (player.isMovementEnabled()) {
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
            if (player.getLifeScore() == 0) continue;
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
        int[][] restrictedCoordinates = {{77, 68}, {486, 438}, {486, 68}, {77, 438}};

        int radius = 64;
        for (int[] restricted : restrictedCoordinates) {
            int restrictedX = restricted[0];
            int restrictedY = restricted[1];
            if (Math.abs(x - restrictedX) <= radius && Math.abs(y - restrictedY) <= radius) {
                return null;
            }
        }
        String imagePath = "/image/boosters/rocket.png";
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(35);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setRotate(45);
        return imageView;
    }

    private ImageView createObstacleImage(double x, double y) {
        double[] newPosition = getValidObstaclePosition(x, y);
        x = newPosition[0];
        y = newPosition[1];

        String imagePath = "/image/background/ostacle.png";
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        scheduleRemoval(imageView, obstacleImages, 25000);
        return imageView;
    }

    private double[] getValidObstaclePosition(double x, double y) {
        int[][] restrictedCoordinates = {{77, 68}, {486, 438}, {486, 68}, {77, 438}};
        int radius = 64;
        int minDistance = 20;
        int maxAttempts = 10;

        for (int[] restricted : restrictedCoordinates) {
            if (Math.abs(x - restricted[0]) <= radius && Math.abs(y - restricted[1]) <= radius) {
                return new double[]{-1, -1};
            }
        }

        int attempts = 0;
        boolean positionValid;
        do {
            positionValid = true;
            for (ImageView existingObstacle : obstacleImages) {
                double existingX = existingObstacle.getLayoutX();
                double existingY = existingObstacle.getLayoutY();

                if (Math.abs(x - existingX) <= minDistance && Math.abs(y - existingY) <= minDistance) {
                    x += minDistance * 2;
                    y += minDistance * 2;
                    positionValid = false;
                    attempts++;
                    break;
                }
            }
        } while (!positionValid && attempts < maxAttempts);

        return new double[]{x, y};
    }

    private void shoot(Player player) {
        if (!player.isLoaded()) return;
        if (player.getLifeScore() == 0) return;
        playShootSound();
        ImageView pistolImageView = pistolsView.get(player);
        player.setLoaded(false);
        pistolImageView.setImage(player.getPistol().getImage());
        pistolsView.put(player, pistolImageView);

        String imagePath = "/image/boosters/bullet.png";
        ImageView bulletImageView = new ImageView(getClass().getResource(imagePath).toExternalForm());
        bulletImageView.setFitHeight(75);
        bulletImageView.setFitWidth(25);
        bulletImageView.setLayoutX(pistolsView.get(player).getLayoutX());
        bulletImageView.setLayoutY(pistolsView.get(player).getLayoutY());
        bulletViews.put(player, bulletImageView);
        String bulletDirection = player.getPistol().getDirection();
        pane.getChildren().add(bulletImageView);
        Timeline timeline = new Timeline();
        if (bulletDirection != null) {
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
                bulletViews.put(player, bulletImageView);

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

                for (ImageView obstacle : obstacleImages) {
                    if (bulletImageView.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                        pane.getChildren().remove(bulletImageView);
                        pane.getChildren().remove(obstacle);
                        obstacleImages.remove(obstacle);
                        String obstacleKey = "";
                        String obstacleId = "";
                        for (Map.Entry<String, ImageView> entry : obstacleViews.entrySet()) {
                            if (entry.getValue().equals(obstacle)) {
                                obstacleKey = entry.getKey();
                                obstacleId = obstacleKey;
                                break;
                            }
                        }
                        System.out.println("ID ПРЕПЯДСТВИЯ " + obstacleId);
                        ObstacleDto obstacleDto = new ObstacleDto((int) obstacle.getLayoutX(),
                                (int) obstacle.getLayoutY(),
                                obstacleId);
                        try {
                            Message obstacleMessage = MessageFactory.create(
                                    MessageType.DELETE_OBSTACLE_TYPE,
                                    gson.toJson(obstacleDto).getBytes()
                            );
                            getApplication().getGameClient().sendMessage(obstacleMessage);
                        } catch (InvalidMessageException ex) {
                            ex.printStackTrace();
                        } catch (ClientException ex) {
                            ex.printStackTrace();
                        }


                        try {
                            Message bulletMessage = MessageFactory.create(
                                    MessageType.LAST_BULLET_TYPE,
                                    gson.toJson(bulletDto).getBytes());
                            getApplication().getGameClient().sendMessage(bulletMessage);
                        } catch (InvalidMessageException ex) {
                            ex.printStackTrace();
                        } catch (ClientException ex) {
                            ex.printStackTrace();
                        }
                        timeline.stop();
                        return;
                    }
                }

                for (Player p : players) {
                    ImageView target = playerViews.get(p);
                    if (target != null && bulletImageView.getBoundsInParent().intersects(target.getBoundsInParent())) {
                        if (p.isHit()) {
                            return;
                        }

                        try {
                            Message bulletMessage = MessageFactory.create(
                                    MessageType.LAST_BULLET_TYPE,
                                    gson.toJson(bulletDto).getBytes());
                            getApplication().getGameClient().sendMessage(bulletMessage);
                        } catch (InvalidMessageException ex) {
                            ex.printStackTrace();
                        } catch (ClientException ex) {
                            ex.printStackTrace();
                        }

                        p.setHit(true);
                        p.minusLifeScore();
                        checkForWinner();
                        updateLifeImage(p);
                        p.setMovementEnabled(false);
                        ImageView pistolImageView2 = pistolsView.get(p);
                        pistolImageView2.setVisible(false);
                        ImageView playerImageView = playerViews.get(p);
                        playerImageView.setVisible(false);
                        String hitImagePath = "";

                        if (p.getLifeScore() == 0) {
                            hitImagePath = "/image/player/dead.png";
                        } else {
                            switch (p.getId()) {
                                case 0 -> hitImagePath = "/image/player/playerRed/shooted.png";
                                case 1 -> hitImagePath = "/image/player/playerBlue/shooted.png";
                                case 2 -> hitImagePath = "/image/player/playerGreen/shooted.png";
                                case 3 -> hitImagePath = "/image/player/playerYellow/shooted.png";
                            }
                        }
                        Image hitImage = new Image(getClass().getResource(hitImagePath).toExternalForm());
                        ImageView hitImageView = new ImageView(hitImage);
                        hitImageView.setLayoutX(playerImageView.getLayoutX());
                        hitImageView.setLayoutY(playerImageView.getLayoutY());
                        hitImageView.setFitHeight(83);
                        hitImageView.setFitWidth(49);
                        pane.getChildren().add(hitImageView);

                        try {
                            Message hitPlayerMessage = MessageFactory.create(
                                    MessageType.HIT_PLAYER_TYPE,
                                    gson.toJson(p.getPlayerDto()).getBytes()
                            );
                            getApplication().getGameClient().sendMessage(hitPlayerMessage);
                        } catch (InvalidMessageException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClientException ex) {
                            throw new RuntimeException(ex);
                        }
                        playerViews.get(player).toFront();
                        pane.getChildren().remove(bulletImageView);
                        timeline.stop();

                        if (!hitImagePath.equals("/image/player/dead.png")) {
                            resetPlayerState(p, hitImageView);
                        }

                        return;
                    }
                }
            });
            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }

    private void updateLifeImage(Player p) {
        ImageView imageView = lifeViews.get(p);
        if (imageView != null) {
            String imagePath = "";
            if (p.getLifeScore() == 0) {
                imagePath = "/image/boosters/0life.png";
            } else {
                switch (p.getId()) {
                    case 0:
                        imagePath = "/image/player/playerRed/" + p.getLifeScore() + ".png";
                        break;
                    case 1:
                        imagePath = "/image/player/playerBlue/" + p.getLifeScore() + ".png";
                        break;
                    case 2:
                        imagePath = "/image/player/playerGreen/" + p.getLifeScore() + ".png";
                        break;
                    case 3:
                        imagePath = "/image/player/playerYellow/" + p.getLifeScore() + ".png";
                        break;
                }
            }
            imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
        }
    }

    private void resetPlayerState(Player player, ImageView hitImageView) {
        if (player.getRecoveryTimeline() != null) {
            player.getRecoveryTimeline().stop();
        }
        Timeline recoveryTimeline = new Timeline(new KeyFrame(Duration.millis(4000), event -> {
            playerViews.get(player).setVisible(true);
            pistolsView.get(player).setVisible(true);
            pane.getChildren().remove(hitImageView);
            player.setMovementEnabled(true);
            player.setLoaded(true);
            player.setHit(false);
        }));
        player.setRecoveryTimeline(recoveryTimeline);
        recoveryTimeline.play();
    }


    @Override
    public void receiveMessage(Message message) {
        switch (message.getType()) {
            case MessageType.SET_PLAYER_POSITION_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                System.out.println("Получен список игроков: " + json);
                UserConfig userConfig = getApplication().getUserConfig();

                Type listType = new TypeToken<List<PlayerDto>>() {}.getType();
                List<PlayerDto> playersDto = gson.fromJson(json, listType);

                for (PlayerDto p : playersDto) {
                    boolean playerExists = players.stream()
                            .anyMatch(existingPlayer -> existingPlayer.getId() == p.getId());

                    if (!playerExists) {
                        Player player = new Player(p.getX(), p.getY(), p.getId());
                        player.setName(userConfig.getUsername());
                        player.setPrevX(player.getX());
                        player.setPrevY(player.getY());
                        players.add(player);
                    } else {
                        System.out.println("Игрок с id " + p.getId() + " уже существует.");
                    }
                }
                localPlayerId = getApplication().getGameClient().idPlayer;
                updatePlayerViews();
                walls.add(wallBottom);
                walls.add(wallLeft);
                walls.add(wallRight);
                walls.add(wallTop);
            }

            case MessageType.PLAYER_POSITION_UPDATE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> receivedData = gson.fromJson(json, type);

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

            case MessageType.GENERATE_OBSTACLE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                ObstacleDto obstacleDto = gson.fromJson(json, ObstacleDto.class);
                String key = obstacleDto.getKey();

                ImageView imageView = createObstacleImage(obstacleDto.getX(), obstacleDto.getY());
                if (imageView != null) {
                    obstacleImages.add(imageView);
                    obstacleViews.put(key, imageView);
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
                        bulletImageView = new ImageView(new Image(getClass().getResource("/image/boosters/bullet.png").toExternalForm()));
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

            case MessageType.HIT_PLAYER_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                PlayerDto playerDto = gson.fromJson(json, PlayerDto.class);
                Player player = players.get(playerDto.getId());
                if (!player.isHit()) player.minusLifeScore();
                ImageView playerImageView = playerViews.get(player);
                Platform.runLater(() -> {
                    ImageView imageView = lifeViews.get(player);
                    if (imageView != null) {
                        String imagePath = "";
                        if (player.getLifeScore() != 0) {
                            switch (player.getId()) {
                                case 0 -> imagePath = "/image/player/playerRed/" + player.getLifeScore() + ".png";
                                case 1 -> imagePath = "/image/player/playerBlue/" + player.getLifeScore() + ".png";
                                case 2 -> imagePath = "/image/player/playerGreen/" + player.getLifeScore() + ".png";
                                case 3 -> imagePath = "/image/player/playerYellow/" + player.getLifeScore() + ".png";
                            }
                        } else {
                            imagePath = "/image/boosters/0life.png";
                        }
                        imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));

                    }

                    player.setMovementEnabled(false);
                    ImageView pistolImageView = pistolsView.get(player);
                    playerImageView.setVisible(false);
                    pistolImageView.setVisible(false);
                    String hitImagePath = "";
                    switch (player.getId()) {
                        case 0 -> hitImagePath = "/image/player/playerRed/shooted.png";
                        case 1 -> hitImagePath = "/image/player/playerBlue/shooted.png";
                        case 2 -> hitImagePath = "/image/player/playerGreen/shooted.png";
                        case 3 -> hitImagePath = "/image/player/playerYellow/shooted.png";
                    }
                    Image hitImage = new Image(getClass().getResource(hitImagePath).toExternalForm());
                    ImageView hitImageView = new ImageView(hitImage);
                    hitImageView.setLayoutX(playerImageView.getLayoutX());
                    hitImageView.setLayoutY(playerImageView.getLayoutY());
                    hitImageView.setFitHeight(83);
                    hitImageView.setFitWidth(49);
                    pane.getChildren().add(hitImageView);
                    hitImageView.toBack();
                    resetPlayerState(player, hitImageView);
                });
            }


            case MessageType.LAST_BULLET_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                BulletDto bulletDto = gson.fromJson(json, BulletDto.class);
                Player player = players.get(bulletDto.getSenderPlayer().getId());
                Platform.runLater(() -> {
                    ImageView bulletImageView = bulletViews.get(player);
                    pane.getChildren().remove(bulletImageView);
                    bulletViews.remove(player);
                });
            }

            case MessageType.UPDATE_OBSTACLE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                ObstacleDto obstacleDto = gson.fromJson(json, ObstacleDto.class);
                String key = obstacleDto.getKey();
                System.out.println(key);

                Platform.runLater(() -> {
                    ImageView imageView = obstacleViews.get(key);
                    System.out.println(imageView);
                    if (imageView != null) {
                        imageView.setLayoutX(obstacleDto.getX());
                        imageView.setLayoutY(obstacleDto.getY());
                    } else {
                        ImageView newImageView = new ImageView();
                        newImageView.setLayoutX(obstacleDto.getX());
                        newImageView.setLayoutY(obstacleDto.getY());
                        newImageView.setFitHeight(50);
                        newImageView.setFitWidth(50);
                        String imagePath = "/image/background/ostacle.png";
                        newImageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
                        pane.getChildren().add(newImageView);
                        obstacleViews.put(key, newImageView);
                    }
                });
            }

            case MessageType.DELETE_OBSTACLE_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                ObstacleDto obstacleDto = gson.fromJson(json, ObstacleDto.class);
                String key = obstacleDto.getKey();
                System.out.println("key " + key);
                ImageView imageView = obstacleViews.get(key);
                System.out.println(imageView);
                Platform.runLater(() -> {
                    pane.getChildren().remove(imageView);
                    obstacleViews.remove(key);
                });
            }
        }
    }
}