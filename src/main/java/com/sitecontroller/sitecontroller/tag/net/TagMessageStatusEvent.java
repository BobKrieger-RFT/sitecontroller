package com.sitecontroller.sitecontroller.tag.net;

import java.io.Serializable;

public class TagMessageStatusEvent implements Serializable {

    private String id;
    private String message;
    private boolean reconnect;

    private static final long serialVersionUID = 3045857086007145479L;

    public TagMessageStatusEvent() {
    }

    public TagMessageStatusEvent(final String id, final String message) {
        this(id, message, false);
    }

    public TagMessageStatusEvent(final String id, final String message, final boolean reconnect) {
        this.id = id;
        this.message = message;
        this.reconnect = reconnect;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReconnect() {
        return this.reconnect;
    }
}
