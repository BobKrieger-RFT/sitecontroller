package com.sitecontroller.sitecontroller.tag.net;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import com.sitecontroller.sitecontroller.net.UdpMessageDataEvent;
import com.sitecontroller.sitecontroller.net.UdpMessageDataEventListener;
import com.sitecontroller.sitecontroller.net.UdpMessageManager;
import com.sitecontroller.sitecontroller.net.UdpMessageStatusEvent;
import com.sitecontroller.sitecontroller.net.UdpMessageStatusEventListener;

////import com.sitecontroller.sitecontroller.tag.message.IMessage;
////import com.sitecontroller.sitecontroller.tag.message.MessageConverter;
////import com.sitecontroller.sitecontroller.tag.message.report.IReportMessage;
////import com.sitecontroller.sitecontroller.util.HexUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TagMessageManager implements UdpMessageStatusEventListener, UdpMessageDataEventListener {

    private static Log logger = LogFactory.getLog(TagMessageManager.class);
    private static final int DEFAULT_PORT = 9001;

    private InetAddress ipAddress = null;
    private UdpMessageManager udpMessageManager;
    private MessageParseModeType messageParseMode = MessageParseModeType.TRY_PARSE_FULL_THEN_PARSE_HEADER;
    private Pattern macAddressFilterRegex;
    private String id;
    private String macAddressFilter;
    private int port = DEFAULT_PORT;
    private boolean udpMessageManagerRunning;
    private boolean broadcastCommandMessages;
    private Object lockObject = new Object();

    private Vector<TagMessageStatusEventListener> statusEventListeners = new Vector<TagMessageStatusEventListener>();
    
    /////private Vector<TagMessageDataEventListener> dataEventListeners = new Vector<TagMessageDataEventListener>();

    public enum MessageParseModeType {
        ALWAYS_PARSE_FULL_MESSAGE((byte) 0),
        ALWAYS_PARSE_HEADER_ONLY((byte) 1),
        TRY_PARSE_FULL_THEN_PARSE_HEADER((byte) 2);

        private byte value;
        private static Map<Byte, MessageParseModeType> map;

        static {
            map = new HashMap<Byte, MessageParseModeType>();
            for (MessageParseModeType mpmt : MessageParseModeType.values()) {
                map.put(Byte.valueOf(mpmt.value()), mpmt);
            }
        }

        MessageParseModeType(byte value) {
            this.value = value;
        }

        public byte value() {
            return this.value;
        }

        public static MessageParseModeType lookup(byte value) {
            return map.get(Byte.valueOf(value));
        }
    }

    public TagMessageManager() {
    }

    public TagMessageManager(final String id, final InetAddress ipAddress, final int port) {
        this(id, ipAddress, port, null, null);
    }

    public TagMessageManager(final String id, final InetAddress ipAddress, final int port, final String macAddressFilter) {
        this(id, ipAddress, port, macAddressFilter, null);
    }

    public TagMessageManager(final String id, final InetAddress ipAddress, final int port, final Pattern macAddressFilterRegex) {
        this(id, ipAddress, port, null, macAddressFilterRegex);
    }

    private TagMessageManager(final String id, final InetAddress ipAddress, final int port, final String macAddressFilter, final Pattern macAddressFilterRegex) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
        this.macAddressFilter = macAddressFilter;
        this.macAddressFilterRegex = macAddressFilterRegex;
    }

    public String getId() {
        return id;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) throws Exception {
        if (ipAddress == null) {
            throw new IllegalArgumentException("ipAddress");
        } else if (!ipAddress.equals(this.ipAddress)) {
            this.ipAddress = ipAddress;
            if (udpMessageManagerRunning) {
                restart();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) throws Exception {
        if (port != this.port) {
            this.port = port;
            if (udpMessageManagerRunning) {
                restart();
            }
        }
    }

    public MessageParseModeType getMessageParseMode() {
        return messageParseMode;
    }

    public void setMessageParseMode(MessageParseModeType messageParseMode) {
        this.messageParseMode = messageParseMode;
    }

    public String getMacAddressFilter() {
        return macAddressFilter;
    }

    public void setMacAddressFilter(String macAddressFilter) {
        this.macAddressFilter = macAddressFilter;
        if (this.macAddressFilter != null && this.macAddressFilter.length() > 0) {
            // Only support mac address filter or mac address filter regex, not both.
            this.macAddressFilterRegex = null;
        }
    }

    public Pattern getMacAddressFilterRegex() {
        return macAddressFilterRegex;
    }

    public void setMacAddressFilterRegex(Pattern macAddressFilterRegex) {
        this.macAddressFilterRegex = macAddressFilterRegex;
        if (this.macAddressFilterRegex != null) {
            // Only support mac address filter or mac address filter regex, not both.
            this.macAddressFilter = null;
        }
    }

    public boolean isBroadcastCommandMessages() {
        return broadcastCommandMessages;
    }

    public void setBroadcastCommandMessages(boolean broadcastCommandMessages) {
        this.broadcastCommandMessages = broadcastCommandMessages;
    }

    public boolean isRunning() {
        return udpMessageManagerRunning;
    }

    // Start/Stop related members.
    public void start() throws Exception {
        if (this.udpMessageManagerRunning) {
            return;
        }

        synchronized (lockObject) {
            if (this.udpMessageManagerRunning) {
                return;
            }

            this.udpMessageManagerRunning = true;

            // mw:make the timeout, retry, etc. configurable
            this.udpMessageManager = new UdpMessageManager(this.ipAddress, this.port, 5000, true, 15);
            this.udpMessageManager.addEventListener((UdpMessageStatusEventListener) this);
            this.udpMessageManager.addEventListener((UdpMessageDataEventListener) this);
            this.udpMessageManager.start();
            // System.Diagnostics.Debug.WriteLine(string.Format("[{0}] TagMessageManager started.", DateTime.Now.ToString("HH:mm:ss.fff")));
        }
    }

    public void stop() {
        if (!this.udpMessageManagerRunning) {
            return;
        }

        synchronized (lockObject) {
            if (!this.udpMessageManagerRunning) {
                return;
            }

            try {
                this.udpMessageManager.stop();
            } catch (Exception ex) {

            } finally {
                this.udpMessageManagerRunning = false;
                this.udpMessageManager = null;
            }
        }
    }

    private void restart() throws Exception {
        stop();
        start();
    }

    @Override
    public void connectionClosed(final UdpMessageStatusEvent e) {
        notifyConnectionClosed(new TagMessageStatusEvent(id, e.getMessage()));
    }

    @Override
    public void connectionError(final UdpMessageStatusEvent e) {
        notifyConnectionError(new TagMessageStatusEvent(id, e.getMessage()));
    }

    @Override
    public void connectionEstablished(final UdpMessageStatusEvent e) {
        notifyConnectionEstablished(new TagMessageStatusEvent(id, e.getMessage()));
    }

    @Override
    public void messageReceiveError(final UdpMessageStatusEvent e) {
        notifyMessageReceiveError(new TagMessageStatusEvent(id, e.getMessage()));
    }

    @Override
    public void messageReceived(final UdpMessageDataEvent e) {
        try {
            //BIG TO DO
            /*
            // Always get the header first.
            IMessage message = MessageConverter.convertToMessage(e.getPacket().getData(), true);

            // Apply filter, if necessary.
            // First get the variables, because they could change while we are working with them.
            String macAddressFilter = this.macAddressFilter;
            Pattern macAddressFilterRegex = this.macAddressFilterRegex;
            if (macAddressFilter != null && macAddressFilter.length() > 0) {
                if (!macAddressFilter.equalsIgnoreCase(message.getMacAddress())) {
                    // Not a match.
                    return;
                }
            } else if (macAddressFilterRegex != null) {
                if (!macAddressFilterRegex.matcher(message.getMacAddress()).matches()) {
                    // Not a match.
                    return;
                }
            }

            // Parse full message, if specified.
            if (messageParseMode == MessageParseModeType.ALWAYS_PARSE_HEADER_ONLY) {
                // Do nothing.
            } else if (messageParseMode == MessageParseModeType.TRY_PARSE_FULL_THEN_PARSE_HEADER) {
                try {
                    message = MessageConverter.convertToMessage(e.getPacket().getData(), message);
                } catch (Exception ex) {
                }
            } else {
                message = MessageConverter.convertToMessage(e.getPacket().getData(), message);
            }
            */
            /*
            if (message != null && (message instanceof IReportMessage || broadcastCommandMessages)) {
                // System.Diagnostics.Debug.WriteLine(string.Format("[{0}] Message Received: {1}", DateTime.Now.ToString("HH:mm:ss.fff"), args.Data.ToHexString()));
                logger.debug("Received:  " + HexUtils.convertToHexString(e.getPacket().getData()));
                notifyMessageReceived(new TagMessageDataEvent(message, e.getPacket()));
            }
            */
        } catch (Exception ex) {
            notifyMessageReceiveError(new TagMessageStatusEvent(id, ex.getMessage()));
        }
    }

    // Event related methods.
    public void addEventListener(final TagMessageStatusEventListener listener) {
        if (!statusEventListeners.contains(listener)) {
            statusEventListeners.add(listener);
        }
    }

    /*
    public void addEventListener(final TagMessageDataEventListener listener) {
        if (!dataEventListeners.contains(listener)) {
            dataEventListeners.add(listener);
        }
    }
    */

    public void removeEventListener(final TagMessageStatusEventListener listener) {
        if (statusEventListeners.contains(listener)) {
            statusEventListeners.remove(listener);
        }
    }

    /*
    public void removeEventListener(final TagMessageDataEventListener listener) {
        if (dataEventListeners.contains(listener)) {
            dataEventListeners.remove(listener);
        }
    }*/

    private void notifyConnectionEstablished(final TagMessageStatusEvent e) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionEstablished(e);
        }
    }

    private void notifyConnectionError(final TagMessageStatusEvent e) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionError(e);
        }
    }

    private void notifyConnectionClosed(final TagMessageStatusEvent e) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionClosed(e);
        }
    }

    /*
    private void notifyMessageReceived(final TagMessageDataEvent e) {
        for (TagMessageDataEventListener listener : dataEventListeners) {
            listener.messageReceived(e);
        }
    }
    */

    private void notifyMessageReceiveError(final TagMessageStatusEvent e) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.messageReceiveError(e);
        }
    }

    // Send.
    /*
    public void sendTagCommandMessage(final IMessage message) throws Exception {
        sendTagCommandMessage(message, DEFAULT_PORT, this.ipAddress);
    }

    public void sendTagCommandMessage(final IMessage message, final int port) throws Exception {
        sendTagCommandMessageRaw(MessageConverter.convertToBytes(message), port, this.ipAddress);
    }

    public void sendTagCommandMessage(final IMessage message, final int port, final InetAddress ipAddress) throws Exception {
        sendTagCommandMessageRaw(MessageConverter.convertToBytes(message), port, ipAddress);
    }

    public void sendTagCommandMessageRaw(final byte[] messageBytes) throws Exception {
        sendTagCommandMessageRaw(messageBytes, DEFAULT_PORT);
    }

    public void sendTagCommandMessageRaw(final byte[] messageBytes, final int port) throws Exception {
        sendTagCommandMessageRaw(messageBytes, port, this.ipAddress);
    }

    public void sendTagCommandMessageRaw(final byte[] messageBytes, final int port, final InetAddress ipAddress) throws Exception {
    	logger.debug("Sending:  " + HexUtils.convertToHexString(messageBytes));
        this.udpMessageManager.send(messageBytes, ipAddress, port);
        // System.out.printLine(String.format("[{0}] Message Sent: {1}", DateTime.Now.ToString("HH:mm:ss.fff"), messageBytes.ToHexString()));
    }
        */
}
