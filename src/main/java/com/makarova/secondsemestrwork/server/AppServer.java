package com.makarova.secondsemestrwork.server;


import com.makarova.secondsemestrwork.listener.impl.*;

public class AppServer {

    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            Server server = new ServerImpl(PORT);
            server.registerListener(new PlayerConnectionListener(4));
            server.registerListener(new SetPlayerPositionListener());
            server.registerListener(new PositionUpdateListener());
            server.registerListener(new InitRocketListener());
            server.registerListener(new GenerateRocketListener());
            server.registerListener(new BulletUpdateListener());
            server.registerListener(new HitPlayerListener());
            server.registerListener(new ChangeLifeCountListener());
            server.registerListener(new GameStartTypeListener());
            server.registerListener(new LastBulletListener());
            server.registerListener(new UpdateObstaclePositionListener());
            server.registerListener(new DeleteObstacleListener());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
