package com.sitecontroller.sitecontroller.tag.message;

import com.sitecontroller.sitecontroller.common.enums.MessageSource;
import com.sitecontroller.sitecontroller.common.enums.MessageType;

public interface IMessage {
    MessageSource getSource();
    MessageType getType();
    String getMacAddress();
}
