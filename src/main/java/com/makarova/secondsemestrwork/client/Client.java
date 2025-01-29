package com.makarova.secondsemestrwork.client;

import com.makarova.secondsemestrwork.exceptions.ClientException;
import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;
import com.makarova.secondsemestrwork.protocol.Message;

//соединяется с сервером и посылает сообщения
public interface Client{

    void connect() throws ClientException;

    public void sendMessage(Message message) throws ClientException, InvalidMessageException;

}
