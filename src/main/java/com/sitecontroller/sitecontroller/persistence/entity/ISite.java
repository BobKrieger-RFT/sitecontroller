package com.sitecontroller.sitecontroller.persistence.entity;

////////import com.rft.pinpoint.persistence.entity.ISoftDeleteEntity;

public interface ISite { //////extends ISoftDeleteEntity {
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    String getCustomerId();
    void setCustomerId(String customerId);
    String getSystemId();
    void setSystemId(String systemId);

    boolean isArchiveInProgress();

    void setArchiveInProgress(boolean archiveInProgress);

    int getArchiveContentsOlderThanDays();

    void setArchiveContentsOlderThanDays(int archiveContentsOlderThanDays);

    String getEmailWhenArchiveComplete();

    void setEmailWhenArchiveComplete(String emailWhenArchiveComplete);
}
