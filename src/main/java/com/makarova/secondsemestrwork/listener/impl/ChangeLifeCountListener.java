package com.makarova.secondsemestrwork.listener.impl;

import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;

import java.rmi.ServerException;

public class ChangeLifeCountListener extends AbstractEventListener {
    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        if(!this.isInit){
            throw new ServerEventListenerException("Listener has not been initiated yet.");
        }

        Message changePlayerLifeOfOthertsMessage = MessageFactory.create(
                MessageType.CHANGE_LIFE_COUNT_TYPE,
                message.getData()
        );

        try {
            server.sendMessageToAllExceptSender(changePlayerLifeOfOthertsMessage, connectionId);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return MessageType.CHANGE_LIFE_COUNT_TYPE;
    }
}
