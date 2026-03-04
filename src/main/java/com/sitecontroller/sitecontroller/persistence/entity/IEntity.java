package com.sitecontroller.sitecontroller.persistence.entity;

import java.util.Date;

public interface IEntity extends Cloneable {
    String getId();
    void setId(String id);
    void setLastUpdate(Date lastUpdate);
    Date getLastUpdate();
    Object clone();
    void markDirty();
}
