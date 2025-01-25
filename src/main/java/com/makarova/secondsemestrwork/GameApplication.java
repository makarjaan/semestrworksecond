package com.makarova.secondsemestrwork;

import com.makarova.secondsemestrwork.client.GameClient;
import com.makarova.secondsemestrwork.model.UserConfig;
import com.makarova.secondsemestrwork.view.BaseView;
import com.makarova.secondsemestrwork.view.GameView;
import com.makarova.secondsemestrwork.view.UserConfigView;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class GameApplication extends Application {

    private UserConfig userConfig;
    private UserConfigView userConfigView;
    private BorderPane root;
    private GameView gameView;
    private GameClient gameClient;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        BaseView.setApplication(this);

        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);
        primaryStage.setResizable(false);

        userConfigView = new UserConfigView();
        gameView = new GameView();
        gameClient = new GameClient(this);
        root = new BorderPane();

        Scene scene = new Scene(root);
        System.out.println(getClass().getResource("/image/background.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

        setView(gameView);
    }

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public UserConfigView getUserConfigView() {
        return userConfigView;
    }


    public UserConfig getUserConfig() {
        return userConfig;
    }

    public GameView getGameView() { return gameView; }

    public GameClient getGameClient() {
        return gameClient;
    }

    public void startGame() {
        gameClient.start();
    }

    public void setView(BaseView view) {
        root.setCenter(view.getView());
    }

}