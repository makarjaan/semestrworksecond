package com.makarova.secondsemestrwork.protocol;

import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;

import java.nio.ByteBuffer;

public class MessageFactory {

    public static final int MAX_LENGTH = 100 * 1024;

    public static Message create(int messageType, byte[] data) throws InvalidMessageException {
        if (data.length > MAX_LENGTH) {
            throw new InvalidMessageException("Message can't be " + data.length
                    + " bytes length. Maximum is " + MAX_LENGTH + "."
            );
        }
        if (!MessegeType.getAllTypes().contains(messageType)) {
            throw new InvalidMessageException("Wrong message type");
        }
        return new Message(messageType, data);
    }

}
