package com.makarova.secondsemestrwork.protocol;


import com.makarova.secondsemestrwork.exceptions.InvalidMessageException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


//серилизация и десирилизация данных, типы сообщения и общая логика проткола
public class MessageProtocol {


    public static final int MAX_LENGTH = 100 * 1024;
    protected static final byte[] START_BYTES = new byte[]{0x0, 0x1};


    public static Message readMessage(InputStream in) throws InvalidMessageException {
        byte[] buffer = new byte[MAX_LENGTH];
        try {
            //контроль корректности передачи данных
            in.read(buffer, 0, START_BYTES.length);
            if (!Arrays.equals(
                    Arrays.copyOfRange(buffer, 0, START_BYTES.length),
                    START_BYTES )) {
                throw new InvalidMessageException("Message first bytes must be " + Arrays.toString(START_BYTES)
                );
            }

            //читает тип сообщения
            in.read(buffer, 0, 4);
            int messageType = ByteBuffer.wrap(buffer, 0, 4).getInt();
            if (!MessegeType.getAllTypes().contains(messageType)) {
                throw new InvalidMessageException("Wrong message type: " + messageType + ".");
            }

            //проверка длины сообщения
            in.read(buffer, 0, 4);
            int messageLength = ByteBuffer.wrap(buffer, 0, 4).getInt();
            if (messageLength > MAX_LENGTH) {
                throw new InvalidMessageException(
                        "Message can't be " + messageLength + " bytes length. Maximum is " + MAX_LENGTH + ".");
            }

            buffer = new byte[messageLength];
            in.read(buffer, 0, messageLength);

            /*
            Object o = (new ObjectInputStream(
                        new ByteArrayInputStream(buffer)
                       )
                    ).readObject();

            if (messageType == TYPE1) {
                ChatMessage m = (ChatMessage) o;
            }
             */
            return MessageFactory.create(messageType, buffer);
        } catch (IOException e) {
            throw new InvalidMessageException("Can't read message", e);
        }
    }

    public static byte[] getBytes(Message message) {
        int rawMessageLength = START_BYTES.length + 4 + 4 + message.getData().length;
        byte[] rawMessage = new byte[rawMessageLength];
        //версия проткола
        int j = 0;
        for (int i = 0; i < START_BYTES.length; i++) {
            rawMessage[j++] = START_BYTES[i];
        }
        //тип сообщения
        byte[] type = ByteBuffer.allocate(4).putInt(message.getType()).array();
        for(int i = 0; i < type.length; i++){
            rawMessage[j++] = type[i];
        }
        //длина сообщения
        byte[] length = ByteBuffer.allocate(4).putInt(message.getData().length).array();
        for(int i = 0; i < length.length; i++){
            rawMessage[j++] = length[i];
        }
        //само сообщение
        byte[] data = message.getData();
        for(int i = 0; i < data.length; i++){
            rawMessage[j++] = data[i];
        }
        return rawMessage;
    }

    public static String toString(Message message){
        StringBuilder sb = new StringBuilder();
        String delimeter = " ";
        String nl = System.getProperty("line.separator");
        byte[] bytes = MessageProtocol.getBytes(message);
        sb.append("First bytes: ");
        for(int i = 0; i < START_BYTES.length; i++){
            sb.append(bytes[i]);sb.append(delimeter);
        }
        sb.append(nl);
        sb.append("Type: ");
        sb.append(ByteBuffer.wrap(bytes, 2, 4).getInt());
        sb.append(nl);
        sb.append("Length: ");
        sb.append(ByteBuffer.wrap(bytes, 6, 4).getInt());
        sb.append(nl);
        sb.append("Data: ");
        for(int i = 10; i < bytes.length; i++){
            sb.append(bytes[i]);
            sb.append(delimeter);
        }
        return sb.toString();
    }

    public static String toReadableString(Message message) {
        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();

        byte[] messageData = message.getData();

        if (message.getType() == MessegeType.PLAYER_CONNECTION_TYPE && messageData.length == 4) {
            int value = ByteBuffer.wrap(messageData).getInt();
            sb.append("Всего игроков: ").append(value).append(nl);
        } else {
            sb.append(new String(messageData, StandardCharsets.UTF_8)).append(nl);
        }

        return sb.toString();
    }

}
