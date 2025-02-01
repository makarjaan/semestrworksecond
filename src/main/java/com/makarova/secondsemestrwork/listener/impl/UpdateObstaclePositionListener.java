package com.makarova.secondsemestrwork.listener.impl;

import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;

import java.rmi.ServerException;

public class UpdateObstaclePositionListener extends AbstractEventListener {
    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        if(!this.isInit){
            throw new ServerEventListenerException("Listener has not been initiated yet.");
        }

        Message sendPosition = MessageFactory.create(
                MessageType.UPDATE_OBSTACLE_TYPE,
                message.getData()
        );

        try {
            this.server.sendMessageToAllExceptSender(sendPosition, connectionId);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return MessageType.UPDATE_OBSTACLE_TYPE;
    }
}
