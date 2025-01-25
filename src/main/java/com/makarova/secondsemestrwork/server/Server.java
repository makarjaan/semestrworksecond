package com.makarova.secondsemestrwork.server;

import com.makarova.secondsemestrwork.listener.ServerEventListener;

import java.rmi.ServerException;

public interface Server {

    //сервер может получать от клиента разыне типы сообщений и чтобы в самом сервере не нарушать solid
    //мы можем в сервер записывать разные слушатели

    //сервер получает какое-то сообщение -> проходит по всем слушателям, которые передавали в registerListener
    //и будет спрашивать "готов ли обработать тип сообщения 1?" и будет вызывать их метод


    void registerListener(ServerEventListener listener) throws ServerException;

    public void start() throws ServerException;
}
