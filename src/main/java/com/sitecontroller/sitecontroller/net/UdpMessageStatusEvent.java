package com.sitecontroller.sitecontroller.net;

import java.io.Serializable;

public class UdpMessageStatusEvent implements Serializable {

    private String message;

    private static final long serialVersionUID = 5183375588994080347L;

    public UdpMessageStatusEvent() {
    }

    public UdpMessageStatusEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
