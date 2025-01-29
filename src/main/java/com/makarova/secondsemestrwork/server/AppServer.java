package com.makarova.secondsemestrwork.server;


import com.makarova.secondsemestrwork.listener.impl.PlayerConnectionListener;
import com.makarova.secondsemestrwork.listener.impl.PlayerPositionUpdateListener;

public class AppServer {

    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            Server server = new ServerImpl(PORT);
            server.registerListener(new PlayerConnectionListener(2));
            server.registerListener(new PlayerPositionUpdateListener());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
