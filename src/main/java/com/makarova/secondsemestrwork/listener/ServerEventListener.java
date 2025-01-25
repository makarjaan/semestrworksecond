package com.makarova.secondsemestrwork.listener;

import com.makarova.secondsemestrwork.exceptions.ServerEventListenerException;
import com.makarova.secondsemestrwork.protocol.Message;
import com.makarova.secondsemestrwork.server.Server;

public interface ServerEventListener {

    void init(Server server);

    void handle(int connectionId, Message message) throws ServerEventListenerException;

    int getType();
}
