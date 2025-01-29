package com.makarova.secondsemestrwork.listener.impl;

import com.makarova.secondsemestrwork.entity.RocketDto;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.listener.AbstractEventListener;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.protocol.MessageFactory;
import com.makarova.secondsemestrwork.protocol.MessageType;

import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

public class InitRocketListener extends AbstractEventListener {

    @Override
    public void handle(int connectionId, Message message) throws InvalidMessageException {
        String request = new String(message.getData(), StandardCharsets.UTF_8);
        RocketDto rocketDto = gson.fromJson(request, RocketDto.class);
        List<RocketDto> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(new RocketDto((int) (Math.random() * rocketDto.getX()), (int) (Math.random() * rocketDto.getY())));
        }
        String response = gson.toJson(list);
        Message responceGeneresteRocketMessage = MessageFactory.create(
                MessageType.INIT_ROCKET_TYPE,
                response.getBytes(StandardCharsets.UTF_8)
        );
        try {
            this.server.sendBroadcastMessage(responceGeneresteRocketMessage);
            System.out.println("Сообщение о ракетах отправлено: " + response);

        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return MessageType.INIT_ROCKET_TYPE;
    }
}
