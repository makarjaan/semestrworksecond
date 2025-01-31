package com.makarova.secondsemestrwork.server;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.entity.RocketDto;
import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.ServerEventListener;
import com.makarova.secondsemestrwork.listener.impl.SetPlayerPositionListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageProtocol;
import com.makarova.secondsemestrwork.protocol.MessageType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.makarova.secondsemestrwork.view.BaseView.getApplication;

public class ServerImpl implements Server {

    protected List<ServerEventListener> listeners;
    protected int port;
    protected ServerSocket server;
    protected boolean started;
    protected boolean stertTimer = false;
    protected List<Socket> sockets;
    public List<PlayerDto> players;
    public Gson gson = new Gson();

    public ServerImpl(int port){
        this.listeners = new ArrayList<>();
        this.port = port;
        this.sockets = new ArrayList<>();
        players = new ArrayList<>();
        this.started = false;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }


    public void startPeriodicMessageSending() {
        if (stertTimer) {
            return;
        }
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Запуск генерации ракеты");
                RocketDto newRocket = new RocketDto((int) (Math.random() * 556.6), (int) (Math.random() * 506.4));
                String response = gson.toJson(newRocket);
                System.out.println("одна ракета " + response);

                try {
                    Message rocketGenerationMessage = MessageFactory.create(
                            MessageType.GENERATE_ROCKET_TYPE,
                            response.getBytes(StandardCharsets.UTF_8)
                    );

                    boolean sent = false;
                    int retryCount = 3;
                    while (!sent && retryCount > 0) {
                        try {
                            sendBroadcastMessage(rocketGenerationMessage);
                            System.out.println("Сообщение о новой ракете отправлено: " + response);
                            sent = true;
                        } catch (ServerException e) {
                            retryCount--;
                            System.err.println("Ошибка с сервером. Попыток осталось: " + retryCount);
                            if (retryCount == 0) {
                                System.err.println("Не удалось отправить сообщение после 3 попыток.");
                            }
                        }
                        if (!sent && retryCount > 0) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                } catch (InvalidMessageException e) {
                    System.err.println("Ошибка с сообщением: " + e.getMessage());
                }
            }
        };

        timer.scheduleAtFixedRate(task, 8000, 3000);
    }



    @Override
    public void registerListener(ServerEventListener listener) throws ServerException {
        if (started) {
            throw new ServerException("Server is already started");
        }
        listener.init(this);
        this.listeners.add(listener);
    }

    //стартовать и начать ожидание подсоединения клиента
    @Override
    public void start() throws ServerException{
        try {
            server = new ServerSocket(this.port);
            started = true;
            System.out.println("Сервер запущен. Жду подключения на порту " + this.port + "...");
            while (true) {
                Socket socket = server.accept();
                System.out.println("Клиент подключился: " + socket.getRemoteSocketAddress());
                new Thread(() -> {
                    try {
                        handleConnection(socket);
                    } catch (ServerException e) {
                        System.err.println("Ошибка при обработке соединения: " + e.getMessage());
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            throw new ServerException("Problem with server starting", e);
        }
    }

    @Override
    public void sendMessage(int connectionId, Message message) throws ServerException {
        if(!started){
            throw new ServerException("Server hasn't been started yet.");
        }
        try{
            Socket socket = sockets.get(connectionId);
            socket.getOutputStream().write(MessageProtocol.getBytes(message));
            socket.getOutputStream().flush();
        } catch (IOException ex) {
            throw new ServerException("Can't send message.", ex);
        }
    }

    @Override
    public void sendBroadcastMessage(Message message) throws ServerException {
        if(!started){
            throw new ServerException("Server hasn't been started yet.");
        }
        try{
            byte[] rawMessage = MessageProtocol.getBytes(message);
            for(Socket socket : sockets){
                socket.getOutputStream().write(rawMessage);
                socket.getOutputStream().flush();
            }
        } catch (IOException ex) {
            throw new ServerException("Can't send message.", ex);
        }
    }

    @Override
    public void sendMessageToAllExceptSender(Message message, int senderId) throws ServerException {
        if (!started) {
            throw new ServerException("Server hasn't been started yet.");
        }
        try {
            byte[] rawMessage = MessageProtocol.getBytes(message);
            for (int i = 0; i < sockets.size(); i++) {
                if (i != senderId) {
                    Socket socket = sockets.get(i);
                    socket.getOutputStream().write(rawMessage);
                    socket.getOutputStream().flush();
                }
            }
        } catch (IOException ex) {
            throw new ServerException("Can't send message.", ex);
        }
    }

    protected void handleConnection(Socket socket) throws ServerException {
        sockets.add(socket);
        int connectionId = sockets.lastIndexOf(socket);
        System.out.println("Новое соединение добавлено. ID подключения: " + connectionId);
        try {
            while (true) {
                Message message = MessageProtocol.readMessage(socket.getInputStream());
                for (ServerEventListener listener : listeners) {
                    if (message.getType() == listener.getType()) {
                        listener.handle(connectionId, message);
                    }
                    if (message.getType() == MessageType.SET_PLAYER_POSITION_TYPE) {
                        startPeriodicMessageSending();
                        stertTimer = true;
                    }
                }
            }

        } catch (IOException e) {
            throw new ServerException("Problem with handling connection", e);
        } catch (InvalidMessageException e) {
            throw new ServerException("Problem with message", e);
        } catch (ServerEventListenerException e) {
            throw new ServerException("Problem with handling message", e);
        }
    }


}
