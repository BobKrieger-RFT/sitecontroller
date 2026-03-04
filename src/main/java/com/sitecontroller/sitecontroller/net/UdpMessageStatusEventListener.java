package com.sitecontroller.sitecontroller.net;

public interface UdpMessageStatusEventListener {
    void connectionEstablished(UdpMessageStatusEvent event);
    void connectionError(UdpMessageStatusEvent event);
    void messageReceiveError(UdpMessageStatusEvent event);
    void connectionClosed(UdpMessageStatusEvent event);
}
