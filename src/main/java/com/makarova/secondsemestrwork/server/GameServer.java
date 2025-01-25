package com.makarova.secondsemestrwork.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {

    private ServerSocket serverSocket;
    private List<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
    }

    static class Client implements Runnable {

        private BufferedReader in;
        private BufferedWriter out;
        private GameServer server;

        public Client(BufferedReader in, BufferedWriter out, GameServer server) {
            this.in = in;
            this.out = out;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = in.readLine();
                  //  server.sendMessage(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(5555);
            System.out.println("Сервер запущен на порту 5555");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключился клиент: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                Client client = new Client(in, out, this);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }


}
