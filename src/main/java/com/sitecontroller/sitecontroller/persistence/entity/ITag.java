package com.sitecontroller.sitecontroller.persistence.entity;

import java.util.Date;
import java.util.List;

import com.sitecontroller.sitecontroller.persistence.entity.ISoftDeleteEntity;

public interface ITag extends ISoftDeleteEntity {
    String getMacAddress();
    void setMacAddress(String macAddress);
    byte getType();
    void setType(byte type);
    void setSite(ISite site);
    ISite getSite();
    boolean isSiteLoaded();

    /* TO DO
    void setLastTagCheckin(ITagCheckin lastTagCheckin);
    ITagCheckin getLastTagCheckin();
    boolean isLastTagCheckinLoaded();
    void setLastTagLocation(ITagLocation lastTagLocation);
    ITagLocation getLastTagLocation();
    boolean isLastTagLocationLoaded();
    ITagAlert getLastTagAlert();
    void setLastTagAlert(ITagAlert lastTagAlert);
    boolean isLastTagAlertLoaded();

    IProfile getLastTagProfile();
    void setLastTagProfile(IProfile profile);
    boolean isLastTagProfileLoaded();
    List<ITagCheckin> getTagCheckins();
    void setTagCheckins(List<ITagCheckin> tagCheckins);
    boolean isTagCheckinsLoaded();
    List<ITagLocation> getTagLocations();
    void setTagLocations(List<ITagLocation> tagLocations);
    boolean isTagLocationsLoaded();
    List<ITagAlert> getTagAlerts();
    void setTagAlerts(List<ITagAlert> tagAlerts);
    boolean isTagAlertsLoaded();

    List<ITagCheckinNotificationEmail> getTagCheckinNotificationEmails();
    void setTagCheckinNotificationEmails(List<ITagCheckinNotificationEmail> tagCheckinNotificationEmails);
    boolean isTagCheckinNotificationEmailsLoaded();
    boolean isFlagNextAlarmAsTest();
    void setFlagNextAlarmAsTest(boolean flagNextAlarmAsTest);
    Date getFlagNextAlarmAsTestExpiration();
    void setFlagNextAlarmAsTestExpiration(Date flagNextAlarmAsTestExpiration);
    IPerson getPerson();
    void setPerson(IPerson person);
    IAsset getAsset();
    void setAsset(IAsset asset);
    IReferencePoint getReferencePoint();
    void setReferencePoint(IReferencePoint referencePoint);
    */
}
