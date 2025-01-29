package com.makarova.secondsemestrwork.listener.impl;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessegeType;
import com.makarova.secondsemestrwork.server.ServerImpl;

import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;

public class SetPlayerPositionListener extends AbstractEventListener {

    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        System.out.println("Обновляем позицию игроку, connectionId: " + connectionId);

        ServerImpl serverImpl = (ServerImpl) server;
        String listPlayers = gson.toJson(serverImpl.getPlayers());

        Message setPositionMessage = MessageFactory.create(
                MessegeType.SET_PLAYER_POSITION_TYPE,
                listPlayers.getBytes(StandardCharsets.UTF_8)
        );

        try {
            this.server.sendBroadcastMessage(setPositionMessage);
            System.out.println("Сообщение об размещение позиций игроков");
        } catch (ServerException ex) {
            throw new ServerEventListenerException("Error starting the game", ex);
        }


    }

    @Override
    public int getType() {
        return MessegeType.SET_PLAYER_POSITION_TYPE;
    }


}
