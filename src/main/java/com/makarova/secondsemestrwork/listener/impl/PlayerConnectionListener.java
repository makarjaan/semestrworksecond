package com.makarova.secondsemestrwork.listener.impl;

import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.entity.RocketDto;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.listener.ServerEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import com.makarova.secondsemestrwork.server.ServerImpl;

import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            player.setX(77);
            player.setY(68);
        } else if (connectionId == 1) {
            player.setX(486);
            player.setY(438);
        } else if (connectionId == 2) {
            player.setX(486);
            player.setY(68);
        } else if (connectionId == 3) {
            player.setX(77);
            player.setY(438);
        }
        serverImpl.getPlayers().add(player);
        String playerJson = gson.toJson(player);

        int playerCount = serverImpl.getPlayers().size();
        Map<String, Object> data = new HashMap<>();
        data.put("playerDto", player);
        data.put("connectedPlayers", playerCount);

        String messageJson = gson.toJson(data);

        Message connectedMessage = MessageFactory.create(
                MessageType.PLAYER_CONNECTION_TYPE,
                messageJson.getBytes(StandardCharsets.UTF_8)
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

    }


    @Override
    public int getType() {
        return MessageType.PLAYER_CONNECTION_TYPE;
    }
}
