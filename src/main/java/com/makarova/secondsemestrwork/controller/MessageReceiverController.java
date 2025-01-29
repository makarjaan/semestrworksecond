package com.makarova.secondsemestrwork.controller;

import com.makarova.secondsemestrwork.protocol.Message;

public interface MessageReceiverController {
    void receiveMessage(Message message);
}
