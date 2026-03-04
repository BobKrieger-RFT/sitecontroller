package com.sitecontroller.sitecontroller.tag.net;

public interface TagMessageStatusEventListener {
    void connectionEstablished(TagMessageStatusEvent e);
    void connectionError(TagMessageStatusEvent e);
    void messageReceiveError(TagMessageStatusEvent e);
    void connectionClosed(TagMessageStatusEvent e);
}
