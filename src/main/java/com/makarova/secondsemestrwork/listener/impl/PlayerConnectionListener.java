package com.makarova.secondsemestrwork.listener.impl;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessegeType;
import com.makarova.secondsemestrwork.server.ServerImpl;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;


public class PlayerConnectionListener extends AbstractEventListener {

    private List<Integer> connectedPlayers = new ArrayList<>();

    private final int requiredPlayers;


    public PlayerConnectionListener(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
    }

    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        if(!this.isInit){
            throw new ServerEventListenerException("Listener has not been initiated yet.");
        }

        connectedPlayers.add(connectionId);

        ServerImpl serverImpl = (ServerImpl) server;
        PlayerDto player = new PlayerDto(connectionId);
        if (connectionId == 0) {
            player.setX(100);
            player.setY(100);
        } else if (connectionId == 1) {
            player.setX(200);
            player.setY(200);
        }
        serverImpl.getPlayers().add(player);
        String playerJson = gson.toJson(player);

        Message connectedMessage = MessageFactory.create(
                MessegeType.PLAYER_CONNECTION_TYPE,
                playerJson.getBytes(StandardCharsets.UTF_8)
        );

        try {
            this.server.sendMessage(connectionId, connectedMessage);
        } catch (ServerException e) {
            e.printStackTrace();
        }

        if (connectedPlayers.size() == requiredPlayers) {
            System.out.println("Все игроки подключены, начинаем игру...");

            String listPlayers = gson.toJson(serverImpl.getPlayers());

            Message startGameMessage = MessageFactory.create(
                    MessegeType.GAME_START_TYPE,
                    listPlayers.getBytes(StandardCharsets.UTF_8)
            );

            try {
                this.server.sendBroadcastMessage(startGameMessage);
                System.out.println("Сообщение о начале игры отправлено всем игрокам.");
            } catch (ServerException ex) {
                throw new ServerEventListenerException("Error starting the game", ex);
            }
        }

    }


    @Override
    public int getType() {
        return MessegeType.PLAYER_CONNECTION_TYPE;
    }
}
