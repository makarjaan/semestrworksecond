package com.makarova.secondsemestrwork.view;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.controller.MainController;
import com.makarova.secondsemestrwork.controller.MessageReceiverController;
import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessegeType;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import java.io.IOException;



public class GameView extends BaseView  {

    @Override
    public Parent getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/gameView.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            getApplication().getGameClient().setController(controller);
            root.setFocusTraversable(true);
            root.requestFocus();

            Message updateMessage = MessageFactory.create(
                    MessegeType.SET_PLAYER_POSITION_TYPE,
                    ("Разместить игроков").getBytes());

            getApplication().getGameClient().sendMessage(updateMessage);

            root.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case RIGHT -> controller.right = true;
                    case LEFT -> controller.left = true;
                    case UP -> controller.up = true;
                    case DOWN -> controller.down = true;
                }
            });

            root.setOnKeyReleased(e -> {
                switch (e.getCode()) {
                    case RIGHT -> controller.right = false;
                    case LEFT -> controller.left = false;
                    case UP -> controller.up = false;
                    case DOWN -> controller.down = false;
                }
            });

            return root;


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить gameview.fxml");
        } catch (InvalidMessageException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

}
