package com.sitecontroller.sitecontroller.net;

import java.io.Serializable;
import java.net.DatagramPacket;

public class UdpMessageDataEvent implements Serializable {

    private DatagramPacket packet;

    private static final long serialVersionUID = 3797664871537240911L;

    public UdpMessageDataEvent() {
    }

    public UdpMessageDataEvent(DatagramPacket packet) {
        this.packet = packet;
    }

    public DatagramPacket getPacket() {
        return packet;
    }
}
