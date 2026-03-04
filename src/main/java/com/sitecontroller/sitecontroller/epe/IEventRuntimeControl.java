package com.sitecontroller.sitecontroller.epe;

public interface IEventRuntimeControl {
    void init() throws Exception;
    void activate() throws Exception;
    void deactivate();
    void destroy();
}
