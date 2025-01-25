package com.makarova.secondsemestrwork.server;

import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.ServerEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

public class ServerImpl implements Server {

    protected List<ServerEventListener> listeners;
    protected int port;
    protected ServerSocket server;
    protected boolean started;
    protected List<Socket> sockets;

    public ServerImpl(int port){
        this.listeners = new ArrayList<>();
        this.port = port;
        this.sockets = new ArrayList<>();
        this.started = false;
    }


    @Override
    public void registerListener(ServerEventListener listener) throws ServerException {
        if (started) {
            throw new ServerException("Server is already started");
        }
        listener.init(this);
        this.listeners.add(listener);
    }

    @Override
    public void start() throws ServerException{
        try {
            server = new ServerSocket(this.port);
            started = true;
            while (true) {
                Socket socket = server.accept();

            }


        } catch (IOException e) {
            throw new ServerException("Problem with server starting", e);
        }
    }

    protected void handleConnection(Socket socket) throws ServerException {
        sockets.add(socket);
        int connectionId = sockets.lastIndexOf(socket);
        try {
            Message message = MessageProtocol.readMessage(socket.getInputStream());
            System.out.println("New message received: " + MessageProtocol.toString(message));
            for (ServerEventListener listener : listeners) {
                if (message.getType() == listener.getType()) {
                    //TODO Здесь или ранее для каждого слушателя может быть создан другой поток
                    listener.handle(connectionId, message);
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
