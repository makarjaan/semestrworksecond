package com.makarova.secondsemestrwork.listener.impl;

import com.makarova.secondsemestrwork.entity.Player;
import com.makarova.secondsemestrwork.entity.PlayerDto;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;

import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;

public class HitPlayerListener extends AbstractEventListener {
    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        if(!this.isInit){
            throw new ServerEventListenerException("Listener has not been initiated yet.");
        }

        String json = new String(message.getData(), StandardCharsets.UTF_8);
        PlayerDto playerDto = gson.fromJson(json, PlayerDto.class);
        int id = playerDto.getId();

        Message sendPosition = MessageFactory.create(
                MessageType.HIT_PLAYER_TYPE,
                message.getData()
        );

        try {
            this.server.sendMessage(id, sendPosition);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return MessageType.HIT_PLAYER_TYPE;
    }
}
