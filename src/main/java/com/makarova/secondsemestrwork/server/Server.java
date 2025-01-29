package com.makarova.secondsemestrwork.server;

import com.makarova.secondsemestrwork.listener.ServerEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import java.rmi.ServerException;

public interface Server {

    //логика обработки типов сообщений
    void registerListener(ServerEventListener listener) throws ServerException;

    void sendMessage(int connectionId, Message message) throws ServerException;

    void sendBroadcastMessage(Message message) throws ServerException;

    void sendMessageToAllExceptSender(Message message, int senderId) throws ServerException;

    public void start() throws ServerException;
}
