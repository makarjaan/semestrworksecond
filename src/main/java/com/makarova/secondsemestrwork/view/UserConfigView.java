package com.makarova.secondsemestrwork.view;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.controller.MessageReceiverController;
import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.entity.RocketDto;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.listener.impl.GenerateRocketListener;
import com.makarova.secondsemestrwork.model.UserConfig;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class UserConfigView extends BaseView implements MessageReceiverController{

    private AnchorPane pane;
    private VBox box;
    private TextField username;
    private TextField host;
    private TextField port;
    private Button start;
    Gson gson = new Gson();

    @Override
    public Parent getView() {
        if (pane == null) {
            createView();
        }
        return pane;
    }

    private void createView() {
        pane = new AnchorPane();
        box = new VBox(10);

        Label usernameLabel = new Label("Username:");
        username = new TextField();
        Label hostLabel = new Label("Host:");
        host = new TextField();
        host.setText("127.0.0.1");
        Label portLabel = new Label("Port:");
        port = new TextField();
        port.setText("5555");
        start = new Button("Start");

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

                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (InvalidMessageException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        box.getChildren().addAll(
                usernameLabel, username, hostLabel,
                host, portLabel, port, start
        );
        pane.getChildren().addAll(box);
    }

    @Override
    public void receiveMessage(Message message) {
        switch (message.getType()) {
            case MessageType.GAME_START_TYPE -> {

                Platform.runLater(() -> {
                    getApplication().setView(getApplication().getGameView());
                });
            }
        }
    }
}