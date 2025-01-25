package com.makarova.secondsemestrwork.client;

import com.makarova.secondsemestrwork.exceptions.ClientException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class GameClientFer implements ClientExample{

    protected final InetAddress address;

    protected final int port;

    protected Socket socket;

    //запоминаем с кем и по какому порту соединиться
    public GameClientFer(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() { return address; }


    @Override
    public void connect() throws ClientException {
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            throw new ClientException("Can't connect. ", e);
        }
    }
}
