package com.makarova.secondsemestrwork.listener;

import com.google.gson.Gson;
import com.makarova.secondsemestrwork.server.Server;

public abstract class AbstractEventListener implements ServerEventListener {
    protected boolean isInit = false;
    protected Server server;
    protected final Gson gson = new Gson();


    @Override
    public void init(Server server) {
        this.server = server;
        this.isInit = true;
    }
}