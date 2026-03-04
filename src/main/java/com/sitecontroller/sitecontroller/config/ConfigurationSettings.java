package com.sitecontroller.sitecontroller.config;

public final class ConfigurationSettings {

    private static ConfigurationSettings instance;
    private String applicationFolderPath;
    private String importFolderPath;
    private String exportFolderPath;
    private String licenseFile;

    private ConfigurationSettings() {
    }

    public static ConfigurationSettings getInstance() {
        if (instance == null) {
            instance = new ConfigurationSettings();
        }

        return instance;
    }

    public String getApplicationFolderPath() {
        return applicationFolderPath;
    }

    public void setApplicationFolderPath(final String applicationFolderPath) {
        this.applicationFolderPath = applicationFolderPath;
    }

    public String getImportFolderPath() {
        return importFolderPath;
    }

    public void setImportFolderPath(final String importFolderPath) {
        this.importFolderPath = importFolderPath;
    }

    public String getExportFolderPath() {
        return exportFolderPath;
    }

    public void setExportFolderPath(final String exportFolderPath) {
        this.exportFolderPath = exportFolderPath;
    }

    public String getLicenseFile() {
        return licenseFile;
    }

    public void setLicenseFile(final String licenseFile) {
        this.licenseFile = licenseFile;
    }
}
