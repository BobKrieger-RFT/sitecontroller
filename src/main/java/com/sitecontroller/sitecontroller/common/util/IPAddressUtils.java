package com.sitecontroller.sitecontroller.common.util;

import java.io.File;
import java.net.InetAddress;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.sitecontroller.sitecontroller.config.ConfigurationSettings;

public final class IPAddressUtils {

    private static final String APPLICATION_FOLDER_PATH_VAR = "$ApplicationFolderPath";
    private static final String DEFAULT_CATALINA_SERVICE_NAME = "Catalina";
    private static final String HTTP_PROTOCOL_NAME = "http";
    private static final String HTTPS_PROTOCOL_NAME = "https";
    private static final String SERVER_FILE = APPLICATION_FOLDER_PATH_VAR + File.separator + ".." + File.separator + ".." + File.separator + "conf" + File.separator + "server.xml";

    private static Log logger = LogFactory.getLog(IPAddressUtils.class);
    private static String httpProtocol;
    private static String secureHttpCertificateFileName;
    private static String secureHttpCertificatePassword;
    private static Integer httpPort;

    private IPAddressUtils() {
    }

    public static InetAddress[] getIPAddressesForHostName(final String hostName) {
        return null;
    }

    public static Integer getServerHttpPort() {
        if (httpPort == null) {
            String expression = String.format("/Server/Service[@name='%s']/Connector[1]/@port[1]", DEFAULT_CATALINA_SERVICE_NAME);
            String fullServerFilePath = SERVER_FILE.replace(APPLICATION_FOLDER_PATH_VAR, ConfigurationSettings.getInstance().getApplicationFolderPath());
            try {
                String lookupText = getDataContent(fullServerFilePath, expression);
                httpPort = ((lookupText != null) && !lookupText.isEmpty()) ? Integer.valueOf(lookupText) : null;
            } catch (Exception ex) {
                logger.error("Unable To Acquire Server Http Port From Server Configuration File", ex);
                httpPort = null;
            }
        }

        String debugPortTxt = "NULL";
        if (httpPort != null) {
            debugPortTxt = String.format("%d", httpPort.intValue());
        }

        logger.debug(String.format("Acquired HTTP Server Port %s", debugPortTxt));
        return httpPort;
    }

    public static String getServerHttpProtocol() {
        if (httpProtocol == null) {
            String expression = String.format("/Server/Service[@name='%s']/Connector[1]/@secure[1]", DEFAULT_CATALINA_SERVICE_NAME);
            String fullServerFilePath = SERVER_FILE.replace(APPLICATION_FOLDER_PATH_VAR, ConfigurationSettings.getInstance().getApplicationFolderPath());
            try {
                String lookupText = getDataContent(fullServerFilePath, expression);
                boolean isSecure = ((lookupText != null) && !lookupText.isEmpty()) ? Boolean.valueOf(
                        lookupText) : false;
                httpProtocol = isSecure ? HTTPS_PROTOCOL_NAME : HTTP_PROTOCOL_NAME;
            } catch (Exception ex) {
                logger.error("Unable To Acquire Server Http Protocol From Server Configuration File", ex);
                httpProtocol = null;
            }
        }

        logger.debug(String.format("Acquired HTTP Server Protocol %s", httpProtocol));
        return httpProtocol;
    }

    public static String getSecureHttpCertificateFileName() {
        if (secureHttpCertificateFileName == null) {
            String expression = String.format(
                    "/Server/Service[@name='%s']/Connector[1]/SSLHostConfig/Certificate/@certificateKeystoreFile[1]",
                    DEFAULT_CATALINA_SERVICE_NAME);
            String fullServerFilePath = SERVER_FILE.replace(APPLICATION_FOLDER_PATH_VAR,
                    ConfigurationSettings.getInstance().getApplicationFolderPath());
            try {
                String lookupText = getDataContent(fullServerFilePath, expression);
                secureHttpCertificateFileName = (lookupText != null) ? lookupText : "";
            } catch (Exception ex) {
                logger.error("Unable To Acquire Server secure Http Certificate Filename From Server Configuration File",
                        ex);
                secureHttpCertificateFileName = null;
            }
        }

        return secureHttpCertificateFileName;
    }

    public static String getSecureHttpCertificatePassword() {
        if (secureHttpCertificatePassword == null) {
            String expression = String.format(
                    "/Server/Service[@name='%s']/Connector[1]/SSLHostConfig/Certificate/@certificateKeystorePassword[1]",
                    DEFAULT_CATALINA_SERVICE_NAME);
            String fullServerFilePath = SERVER_FILE.replace(APPLICATION_FOLDER_PATH_VAR,
                    ConfigurationSettings.getInstance().getApplicationFolderPath());
            try {
                String lookupText = getDataContent(fullServerFilePath, expression);
                secureHttpCertificatePassword = (lookupText != null) ? lookupText : "";
            } catch (Exception ex) {
                logger.error("Unable To Acquire Server secure Http Certificate Password From Server Configuration File",
                        ex);
                secureHttpCertificatePassword = null;
            }
        }

        return secureHttpCertificatePassword;
    }

    /**
     * Acquires string data from an XML file using the given xpath expression.
     *
     * @param fullServerFilePath The XML filename to acquire data from.
     * @param expression         The xpath expression to identify the content to retrieve.
     * @return String The data content from the XML file.
     * @throws Exception If any parsing or file loading operations fail.
     */
    private static String getDataContent(final String fullServerFilePath, final String expression) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(fullServerFilePath);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile(expression);
        return (String) expr.evaluate(doc, XPathConstants.STRING);
    }
}
