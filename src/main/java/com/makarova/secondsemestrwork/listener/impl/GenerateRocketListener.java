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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GenerateRocketListener extends AbstractEventListener {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int rocketGenerationInterval = 2;

    @Override
    public void handle(int connectionId, Message message) throws ServerEventListenerException, InvalidMessageException {
        System.out.println("Переключился на создание ракет по одному");
        String request = new String(message.getData(), StandardCharsets.UTF_8);
        RocketDto rocketDto = gson.fromJson(request, RocketDto.class);
        scheduler.scheduleAtFixedRate(() -> generateRocketPeriodically(rocketDto),
                0, rocketGenerationInterval, TimeUnit.SECONDS);
    }

    private void generateRocketPeriodically(RocketDto rocketDto) {
        System.out.println("Запуск генерации ракеты");  // Выводим сразу перед запуском

        RocketDto newRocket = new RocketDto((int) (Math.random() * rocketDto.getX()), (int) (Math.random() * rocketDto.getY()));
        String response = gson.toJson(newRocket);
        System.out.println("одна ракета " + response);

        // Печатаем, если задача выполняется
        try {
            // Создание сообщения
            Message rocketGenerationMessage = MessageFactory.create(
                    MessageType.GENERATE_ROCKET_TYPE,
                    response.getBytes(StandardCharsets.UTF_8)
            );

            // Отправка сообщения
            this.server.sendBroadcastMessage(rocketGenerationMessage);
            System.out.println("Сообщение о новой ракете отправлено: " + response);  // Это должно сработать

        } catch (InvalidMessageException e) {
            System.err.println("Ошибка с сообщением: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ServerException e) {
            System.err.println("Ошибка с сервером: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getType() {
        return 0;
    }
}
