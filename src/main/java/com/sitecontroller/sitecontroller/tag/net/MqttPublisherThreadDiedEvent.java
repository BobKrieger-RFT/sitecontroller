package com.sitecontroller.sitecontroller.tag.net;

import java.io.Serializable;
import java.util.List;

import com.sitecontroller.sitecontroller.tag.message.IMessage;

public class MqttPublisherThreadDiedEvent implements Serializable {

    private List<IMessage> messageList;

    private static final long serialVersionUID = -7349167790609174917L;

    public MqttPublisherThreadDiedEvent() {
    }

    public MqttPublisherThreadDiedEvent(final List<IMessage> messageList) {
        this.messageList = messageList;
    }

    public List<IMessage> getMessageList() {
        return messageList;
    }
}
