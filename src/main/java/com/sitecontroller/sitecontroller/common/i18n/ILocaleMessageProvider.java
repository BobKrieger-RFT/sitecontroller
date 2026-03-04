package com.sitecontroller.sitecontroller.common.i18n;

import java.util.Locale;

public interface ILocaleMessageProvider {
    Locale getCurrentLocale();
    String getCurrentLocaleMessage(String name);
    String getMessageForLocale(String name, Locale locale);
}
