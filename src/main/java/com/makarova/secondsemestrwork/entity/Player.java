package com.makarova.secondsemestrwork.entity;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.controller.MainController;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageType;
import com.makarova.secondsemestrwork.sprite.SpriteAnimation;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.makarova.secondsemestrwork.controller.MainController.players;
import static com.makarova.secondsemestrwork.view.BaseView.getApplication;


public class Player {
    public ImageView imageSpriteView;
    public SpriteAnimation spriteAnimation;
    public int x, y;
    public int prevX, prevY;
    public int speed;
    public int id;
    public String name;
    private PlayerDto playerDto;
    private Pistol pistol;
    private boolean loaded;
    private int lifeScore;
    private boolean movementEnabled;
    private boolean isHit = false;
    Gson gson = new Gson();
    private Timeline recoveryTimeline;
    private static final double MIN_DISTANCE = 65;

    public Player(int x, int y, int id) {
        this.speed = 2;
        this.x = x;
        this.y = y;
        this.id = id;
        playerDto = new PlayerDto(this);
        pistol = new Pistol(x, y);
        pistol.setPistolDto(pistol);
        lifeScore = 10;
        loaded = false;
        setMovementEnabled(true);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        playerDto.setName(name);
    }

    public void moveUp() {
        double newX = x;
        double newY = y - speed;
        for (Player otherPlayer : players) {
            if (otherPlayer != this) {
                double distance = calculateDistance(newX, newY, otherPlayer.getX(), otherPlayer.getY());
                if (distance < MIN_DISTANCE) {
                    return;
                }
            }
        }
        y = (int) newY;
        playerDto.setY(y);
        pistol.setDirection("up");
        pistol.setY((int) pistol.getY() - speed);
        spriteAnimation.setOffsetY(144);
        try {
            sendMessage(playerDto, spriteAnimation.getOffsetY(), pistol.getPistolDto());
        } catch (InvalidMessageException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveDown() {
        double newX = x;
        double newY = y + speed;
        for (Player otherPlayer : players) {
            if (otherPlayer != this) {
                double distance = calculateDistance(newX, newY, otherPlayer.getX(), otherPlayer.getY());
                if (distance < MIN_DISTANCE) {
                    return;
                }
            }
        }
        y = (int) newY;
        playerDto.setY(y);
        pistol.setDirection("down");
        pistol.setY((int) pistol.getY() + speed);
        spriteAnimation.setOffsetY(48);
        try {
            sendMessage(playerDto, spriteAnimation.getOffsetY(), pistol.getPistolDto());
        } catch (InvalidMessageException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveLeft() {
        double newX = x - speed;
        double newY = y;

        for (Player otherPlayer : players) {
            if (otherPlayer != this) {
                double distance = calculateDistance(newX, newY, otherPlayer.getX(), otherPlayer.getY());
                if (distance < MIN_DISTANCE) {
                    return;
                }
            }
        }

        x = (int) newX;
        playerDto.setX(x);
        pistol.setDirection("left");
        pistol.setX((int) pistol.getX() - speed);
        spriteAnimation.setOffsetY(0);
        try {
            sendMessage(playerDto, spriteAnimation.getOffsetY(), pistol.getPistolDto());
        } catch (InvalidMessageException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveRight() {
        double newX = x + speed;
        double newY = y;

        for (Player otherPlayer : players) {
            if (otherPlayer != this) {
                double distance = calculateDistance(newX, newY, otherPlayer.getX(), otherPlayer.getY());
                if (distance < MIN_DISTANCE) {
                    return;
                }
            }
        }
        x = (int) newX;
        playerDto.setX(x);
        pistol.setDirection("right");
        pistol.setX((int) (pistol.getX() + speed));
        spriteAnimation.setOffsetY(96);
        try {
            sendMessage(playerDto, spriteAnimation.getOffsetY(), pistol.getPistolDto());
        } catch (InvalidMessageException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    public void stopAnimation() {
        spriteAnimation.stop();
        imageSpriteView.setViewport(new Rectangle2D(0, spriteAnimation.getOffsetY(), 47, 48));
    }

    public void startAnimation() {
        spriteAnimation.play();
    }

    public void sendMessage(PlayerDto playerDto, int offsetY, PistolDto pistolDto) throws InvalidMessageException, ClientException {
        Map<String, Object> data = new HashMap<>();
        data.put("playerDto", playerDto);
        data.put("offsetY", offsetY);
        data.put("pistolDto", pistolDto);
        String json = gson.toJson(data);
        Message moveMessage = new Message(
                MessageType.PLAYER_POSITION_UPDATE_TYPE,
                json.getBytes(StandardCharsets.UTF_8)
        );
        getApplication().getGameClient().sendMessage(moveMessage);
    }

    public ImageView getImageSpriteView() {
        if (imageSpriteView == null) {
            imageSpriteView = new ImageView();
        }
        return imageSpriteView;
    }

    public PlayerDto getPlayerDto() {
        return playerDto;
    }

    public int getX() { return x;}

    public void setX(int x) {
        this.x = x;
        playerDto.setX(x);
    }

    public int getY() { return y;}

    public void setY(int y) {
        this.y = y;
        playerDto.setY(y);
    }

    public int getSpeed() {
        return speed;
    }

    public int getId() { return id; }

    public int getPrevX() { return prevX; }

    public void setPrevX(int prevX) { this.prevX = prevX; }

    public int getPrevY() { return prevY; }

    public void setPrevY(int prevY) { this.prevY = prevY;}

    public Pistol getPistol() { return pistol; }

    public void setPistol(Pistol pistol) { this.pistol = pistol;}

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
        playerDto.setLoaded(loaded);
    }

    public int getLifeScore() {
        return lifeScore;
    }

    public void setLifeScore(int lifeScore) {
        this.lifeScore = lifeScore;
        playerDto.setLifeScore(lifeScore);
    }

    public void minusLifeScore() {
        this.lifeScore = this.lifeScore - 1;
        playerDto.setLifeScore(lifeScore);
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public void setMovementEnabled(boolean movementEnabled) {
        this.movementEnabled = movementEnabled;
        playerDto.setMovementEnabled(movementEnabled);
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
        playerDto.setHit(hit);
    }

    public Timeline getRecoveryTimeline() {
        return recoveryTimeline;
    }

    public void setRecoveryTimeline(Timeline recoveryTimeline) {
        this.recoveryTimeline = recoveryTimeline;
    }
}