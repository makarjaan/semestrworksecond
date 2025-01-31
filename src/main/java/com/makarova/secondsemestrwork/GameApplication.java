package com.makarova.secondsemestrwork;

import com.makarova.secondsemestrwork.client.GameClient;
import com.makarova.secondsemestrwork.controller.MainController;
import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.model.UserConfig;
import com.makarova.secondsemestrwork.view.BaseView;
import com.makarova.secondsemestrwork.view.GameView;
import com.makarova.secondsemestrwork.view.UserConfigView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class GameApplication extends Application {

    private UserConfig userConfig;
    private UserConfigView userConfigView;
    private BorderPane root;
    private GameView gameView;
    private GameClient gameClient;

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5555;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        primaryStage.setTitle("Game");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        BaseView.setApplication(this);
/*
        primaryStage.initStyle(StageStyle.UNDECORATED);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        primaryStage.setWidth(screenBounds.getWidth()); //1536
        primaryStage.setHeight(screenBounds.getHeight()); //864

 */

        primaryStage.setWidth(1000);
        primaryStage.setHeight(864);
        primaryStage.setResizable(false);


        userConfigView = new UserConfigView();
        gameClient = new GameClient(InetAddress.getByName(HOST), PORT);
        root = new BorderPane();
        gameView = new GameView();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        setView(userConfigView);
    }

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public GameView getGameView() { return gameView; };

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public void startGame() throws ClientException {
        gameClient.connect();
    }

    public void setView(BaseView view) {
        root.setCenter(view.getView());
    }

}