package com.sitecontroller.sitecontroller.persistence.entity;

import java.util.Date;

public interface ISoftDeleteEntity extends IEntity {
    void setDeleted(boolean deleted);
    boolean isDeleted();
    void setDeleteDate(Date deleteDate);
    Date getDeleteDate();
    // this is probably a weird place for this, but there are cases where subclasses of soft delete entities
    // are not soft delete entities - one example is the change and changeMerge classes
    boolean disableSoftDelete();
}
