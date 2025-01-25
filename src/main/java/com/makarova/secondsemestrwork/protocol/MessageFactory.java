package com.makarova.secondsemestrwork.protocol;

import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;

public class MessageFactory {

    public static final int TYPE1 = 1;
    public static final int TYPE2 = 2;

    public static final int MAX_LENGTH = 100 * 1024;

    public static Message create(int messageType, byte[] data) throws InvalidMessageException {
        if(data.length > MAX_LENGTH){
            throw new InvalidMessageException("Message can't be " + data.length
                    + " bytes length. Maximum is " + MAX_LENGTH + "."
            );
        }
        if (messageType != TYPE1 && messageType != TYPE2) {
            throw new InvalidMessageException("Wrong message type");
        }
        return new Message(messageType, data);
    }
}
