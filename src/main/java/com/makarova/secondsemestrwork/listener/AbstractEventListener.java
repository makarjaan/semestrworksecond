package com.makarova.secondsemestrwork.listener;

import com.makarova.secondsemestrwork.server.Server;

public abstract class AbstractEventListener implements ServerEventListener {
    protected boolean isInit = false;
    protected Server server;

    @Override
    public void init(Server server) {
        this.server = server;
        this.isInit = true;
    }
}