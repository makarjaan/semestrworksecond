package com.makarova.secondsemestrwork.server;


public class AppServer {

    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            Server server = new ServerImpl(PORT);
           // server.registerListener();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
