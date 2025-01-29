package com.makarova.secondsemestrwork.listener.impl;

import com.google.gson.reflect.TypeToken;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;
import com.makarova.secondsemestrwork.server.ServerImpl;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;


public class PositionUpdateListener extends AbstractEventListener {

    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        if(!this.isInit){
            throw new ServerEventListenerException("Listener has not been initiated yet.");
        }

        ServerImpl serverImpl = (ServerImpl) server;
        String json = new String(message.getData(), StandardCharsets.UTF_8);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> receivedData = gson.fromJson(json, type);
        PlayerDto playerDto = gson.fromJson(gson.toJson(receivedData.get("playerDto")), PlayerDto.class);
        int offsetY = ((Double) receivedData.get("offsetY")).intValue();
        serverImpl.getPlayers().get(playerDto.getId()).setX(playerDto.getX());
        serverImpl.getPlayers().get(playerDto.getId()).setY(playerDto.getY());

        Map<String, Object> data = new HashMap<>();
        data.put("playerDto", serverImpl.getPlayers().get(playerDto.getId()));
        data.put("offsetY", offsetY);
        String jsonSend = gson.toJson(data);

        Message sendPosition = MessageFactory.create(
                MessageType.PLAYER_POSITION_UPDATE_TYPE,
                jsonSend.getBytes(StandardCharsets.UTF_8)
        );

        try {
            this.server.sendMessageToAllExceptSender(sendPosition, connectionId);
        } catch (ServerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getType() {
        return MessageType.PLAYER_POSITION_UPDATE_TYPE;
    }
}
