package com.makarova.secondsemestrwork.client;

import com.makarova.secondsemestrwork.GameApplication;


import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class GameClient {
    private GameApplication application;
    private Socket socket;
    private ClientThread clientThread;

    public GameClient(GameApplication application) {
        this.application = application;
    }

    public GameApplication getApplication() {
        return application;
    }

    public void sendMessage(String message) {
        try {
            clientThread.out.write(message+"\n");
            clientThread.out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        String host = application.getUserConfig().getHost();
        int port = application.getUserConfig().getPort();

        BufferedReader in;
        BufferedWriter out;

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            clientThread = new ClientThread(in, out, this);

            new Thread(clientThread).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static class ClientThread implements Runnable {

        private BufferedReader in;
        private BufferedWriter out;
        private GameClient client;

        public ClientThread(BufferedReader in, BufferedWriter out, GameClient client) {
            this.in = in;
            this.out = out;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = in.readLine();
                    //client.getApplication().appendMessage(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
