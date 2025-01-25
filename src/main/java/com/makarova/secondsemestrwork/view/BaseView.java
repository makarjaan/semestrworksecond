package com.makarova.secondsemestrwork.view;


import com.makarova.secondsemestrwork.GameApplication;
import javafx.scene.Parent;

public abstract class BaseView {

    private static GameApplication application;

    public static GameApplication getApplication() {
        if (application != null) {
            return application;
        }
        throw new RuntimeException("application is null");
    }

    public static void setApplication(GameApplication application) {
        BaseView.application = application;
    }

    public abstract Parent getView();
}