package com.makarova.secondsemestrwork.entity;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageType;
import com.makarova.secondsemestrwork.sprite.SpriteAnimation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.makarova.secondsemestrwork.view.BaseView.getApplication;


public class Player {
    public ImageView imageSpriteView;
    public SpriteAnimation spriteAnimation;
    public int x, y;
    public int prevX, prevY;
    public int speed;
    public int id;
    private PlayerDto playerDto;
    private Pistol pistol;
    private boolean loaded;
    Gson gson = new Gson();

    public Player(int x, int y, int id) {
        this.speed = 2;
        this.x = x;
        this.y = y;
        this.id = id;
        playerDto = new PlayerDto(this);
        pistol = new Pistol(x, y);
        pistol.setPistolDto(pistol);
        loaded = false;
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


    public void moveUp() {
        y -= speed;
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
        y += speed;
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
        x -= speed;
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
        x += speed;
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

    public ImageView getImageSpriteView() {
        if (imageSpriteView == null) {
            imageSpriteView = new ImageView();
        }
        return imageSpriteView;
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

    public int getX() { return x;}

    public void setX(int x) {
        this.x = x;
    }

    public int getY() { return y;}

    public void setY(int y) {
        this.y = y;
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
    }

    public void stopAnimation() {
        spriteAnimation.stop();
        imageSpriteView.setViewport(new Rectangle2D(0, spriteAnimation.getOffsetY(), 47, 48));
    }

    public void startAnimation() {
        spriteAnimation.play();
    }

}