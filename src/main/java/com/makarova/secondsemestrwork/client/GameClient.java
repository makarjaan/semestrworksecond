package com.makarova.secondsemestrwork.client;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.controller.MessageReceiverController;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageProtocol;
import com.makarova.secondsemestrwork.protocol.MessageType;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class GameClient implements Client {

    protected final InetAddress address;

    protected final int port;

    protected Socket socket;

    private ClientThread thread;

    private MessageReceiverController controller;

    public int idPlayer;

    public GameClient(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void connect() throws ClientException {
        try {
            socket = new Socket(address, port);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            thread = new ClientThread(input, output, this);
            new Thread(thread).start();
            System.out.println("Подключение к серверу " + address + ":" + port + " успешно!");

        } catch (IOException e) {
            throw new ClientException("Can't connect. ", e);
        }
    }

    @Override
    public void sendMessage(Message message) throws ClientException, InvalidMessageException {
        try {
            thread.getOut().write(MessageProtocol.getBytes(message));
            thread.getOut().flush();
        } catch (IOException e) {
            throw new ClientException("Can't send message. ", e);
        }
    }

    public void setController(MessageReceiverController controller) {
        this.controller = controller;
    }

    public MessageReceiverController getController() {
        return controller;
    }

    public void stopClient() {
        if (thread != null) {
            thread.stopThread();
            System.out.println("Поток клиента завершен.");
        }
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("Соединение с сервером закрыто.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ClientThread implements Runnable {

        private final InputStream in;
        private final OutputStream out;
        private final GameClient gameClient;
        private boolean running;
        private final Gson gson = new Gson();

        public ClientThread(InputStream in, OutputStream out, GameClient gameClient) {
            this.in = in;
            this.out = out;
            this.gameClient = gameClient;
            this.running = true;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    Message message = MessageProtocol.readMessage(in);
                    if (message != null && gameClient.controller != null) {
                        gameClient.controller.receiveMessage(message);
                    }

                    if (message.getType() == MessageType.PLAYER_CONNECTION_TYPE) {
                        String json = new String(message.getData(), StandardCharsets.UTF_8);
                        PlayerDto player = gson.fromJson(json, PlayerDto.class);
                        gameClient.idPlayer = player.getId();
                    }
                }
            } catch (InvalidMessageException e) {
                e.printStackTrace();
            }
        }

        public InputStream getIn() {
            return in;
        }

        public OutputStream getOut() {
            return out;
        }

        public void stopThread() {
            running = false;
            try {
                in.close();
                out.close();
                gameClient.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
