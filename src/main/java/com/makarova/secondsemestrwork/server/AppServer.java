package com.makarova.secondsemestrwork.server;


import com.makarova.secondsemestrwork.listener.impl.PlayerConnectionListener;
import com.makarova.secondsemestrwork.listener.impl.PositionUpdateListener;
import com.makarova.secondsemestrwork.listener.impl.SetPlayerPositionListener;

public class AppServer {

    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            Server server = new ServerImpl(PORT);
            server.registerListener(new PlayerConnectionListener(2));
            server.registerListener(new SetPlayerPositionListener());
            server.registerListener(new PositionUpdateListener());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
