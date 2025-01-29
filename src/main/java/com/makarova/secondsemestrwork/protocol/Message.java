package com.makarova.secondsemestrwork.protocol;


import javax.swing.plaf.PanelUI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Message {

    protected int type;
    protected byte[] data;

    public Message(int type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

}
