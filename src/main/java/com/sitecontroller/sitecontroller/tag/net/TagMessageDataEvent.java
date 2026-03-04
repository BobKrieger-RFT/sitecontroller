package com.sitecontroller.sitecontroller.tag.net;

import java.io.Serializable;
import java.net.DatagramPacket;

import com.sitecontroller.sitecontroller.tag.message.IMessage;

public class TagMessageDataEvent implements Serializable {

    private IMessage message;
    private DatagramPacket packet;

    private static final long serialVersionUID = -7226722790609174917L;

    public TagMessageDataEvent() {
    }

    public TagMessageDataEvent(IMessage message, DatagramPacket packet) {
        this.message = message;
        this.packet = packet;
    }

    public IMessage getMessage() {
        return message;
    }

    public DatagramPacket getPacket() {
        return packet;
    }
}
