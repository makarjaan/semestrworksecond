package com.makarova.secondsemestrwork.view;

import com.makarova.secondsemestrwork.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import java.io.IOException;



public class GameView extends BaseView {

    @Override
    public Parent getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/gameView.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            root.setFocusTraversable(true);
            root.requestFocus();

            root.setOnKeyPressed(e -> {
                if(e.getCode() == KeyCode.RIGHT) {
                    controller.right = true;
                }
                if(e.getCode() == KeyCode.LEFT) {
                    controller.left = true;
                }
                if (e.getCode() == KeyCode.UP) {
                    controller.up = true;
                }
                if (e.getCode() == KeyCode.DOWN) {
                    controller.down = true;
                }
            });

            root.setOnKeyReleased(e -> {
                if(e.getCode() == KeyCode.RIGHT) {
                    controller.right = false;
                }
                if(e.getCode() == KeyCode.LEFT) {
                    controller.left = false;
                }
                if (e.getCode() == KeyCode.UP) {
                    controller.up = false;
                }
                if (e.getCode() == KeyCode.DOWN) {
                    controller.down = false;
                }
            });

            return root;


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить gameview.fxml");
        }
    }
}
