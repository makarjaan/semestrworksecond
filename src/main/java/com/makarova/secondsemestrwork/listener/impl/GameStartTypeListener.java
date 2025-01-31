package com.makarova.secondsemestrwork.listener.impl;

import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import com.makarova.secondsemestrwork.server.ServerImpl;

import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;

public class GameStartTypeListener extends AbstractEventListener {
    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        System.out.println("Все игроки подключены, начинаем игру...");

        ServerImpl serverImpl = (ServerImpl) server;
        String listPlayers = gson.toJson(serverImpl.getPlayers());

        Message startGameMessage = MessageFactory.create(
                MessageType.GAME_START_TYPE,
                listPlayers.getBytes(StandardCharsets.UTF_8)
        );
        try {
            this.server.sendBroadcastMessage(startGameMessage);
            System.out.println("Сообщение о начале игры отправлено всем игрокам.");
        } catch (ServerException ex) {
            throw new ServerEventListenerException("Error starting the game", ex);
        }
    }

    @Override
    public int getType() {
        return MessageType.GAME_START_TYPE;
    }
}
