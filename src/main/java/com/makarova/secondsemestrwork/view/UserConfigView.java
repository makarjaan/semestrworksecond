package com.makarova.secondsemestrwork.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makarova.secondsemestrwork.controller.MessageReceiverController;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.model.UserConfig;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserConfigView extends BaseView implements MessageReceiverController{

    private AnchorPane pane;
    private VBox box;
    private TextField username;
    private TextField host;
    private TextField port;
    private Button start;
    Gson gson = new Gson();
    private Button launchGame;
    private boolean isFirstPlayer = false;
    private int count = 0;


    @Override
    public Parent getView() {
        if (pane == null) {
            createView();
        }
        return pane;
    }

    private void createView() {
        pane = new AnchorPane();
        pane.setPrefSize(600, 400);

        box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(400);


        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px;");
        username = new TextField();
        username.setPrefSize(300, 40);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            start.setDisable(newValue.trim().isEmpty());
        });

        Label hostLabel = new Label("Host:");
        hostLabel.setStyle("-fx-font-size: 16px;");
        host = new TextField("127.0.0.1");
        host.setPrefSize(300, 40);

        Label portLabel = new Label("Port:");
        portLabel.setStyle("-fx-font-size: 16px;");
        port = new TextField("5555");
        port.setPrefSize(300, 40);

        start = new Button("Start");
        start.setPrefSize(300, 40);
        start.setStyle("-fx-font-size: 16px;");
        start.setDisable(true);  // Кнопка отключена по умолчанию

        launchGame = new Button("Запустить игру");
        launchGame.setPrefSize(300, 40);
        launchGame.setStyle("-fx-font-size: 16px;");
        launchGame.setDisable(true);
        launchGame.setVisible(false);

        start.setOnAction(actionEvent -> {
            if (actionEvent.getSource() == start) {
                UserConfig userConfig = new UserConfig();
                userConfig.setUsername(username.getText());
                userConfig.setHost(host.getText());
                userConfig.setPort(Integer.parseInt(port.getText()));

                getApplication().setUserConfig(userConfig);
                getApplication().getGameClient().setController(this);

                try {
                    getApplication().startGame();
                    Message connectionMessage = MessageFactory.create(
                            MessageType.PLAYER_CONNECTION_TYPE,
                            ("Игрок " + userConfig.getUsername() + " ждёт запуска игры").getBytes());

                    getApplication().getGameClient().sendMessage(connectionMessage);

                    start.setDisable(true);

                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (InvalidMessageException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        launchGame.setOnAction(actionEvent -> {
            try {
                Message startGameMessage = MessageFactory.create(
                        MessageType.GAME_START_TYPE,
                        "Игра запускается...".getBytes());

                getApplication().getGameClient().sendMessage(startGameMessage);
                launchGame.setDisable(true);
                System.out.println("Сообщение о старте игры отправлено.");
            } catch (InvalidMessageException e) {
                throw new RuntimeException(e);
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        });


        box.getChildren().addAll(usernameLabel, username, hostLabel, host, portLabel, port, start, launchGame);

        pane.getChildren().add(box);

        AnchorPane.setTopAnchor(box, (pane.getPrefHeight() - box.getPrefHeight()) / 2);
        AnchorPane.setLeftAnchor(box, (pane.getPrefWidth() - box.getPrefWidth()) / 2);
    }

    @Override
    public void receiveMessage(Message message) {
        switch (message.getType()) {
             case MessageType.PLAYER_CONNECTION_TYPE -> {
                String json = new String(message.getData(), StandardCharsets.UTF_8);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> receivedData = gson.fromJson(json, type);
                Integer connectedPlayers = gson.fromJson(gson.toJson(receivedData.get("connectedPlayers")), Integer.class);
                System.out.println(connectedPlayers);
                count = connectedPlayers;

                Platform.runLater(() -> {
                    if (count >= 2 && count <= 4) {
                        launchGame.setVisible(true);
                        launchGame.setDisable(false);
                        System.out.println("Кнопка активирована, количество игроков: " + count);
                    } else {
                        launchGame.setVisible(false);
                        launchGame.setDisable(true);
                        System.out.println("Кнопка скрыта, количество игроков: " + count);
                    }
                });
            }

            case MessageType.GAME_START_TYPE -> {
                Platform.runLater(() -> {
                    getApplication().setView(getApplication().getGameView());
                });
            }
        }
    }
}