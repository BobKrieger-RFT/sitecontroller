package com.sitecontroller.sitecontroller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitecontroller.sitecontroller.common.util.IPAddressUtils;

public class UdpMessageManager {

    private DatagramSocket socket;
    private InetAddress ipAddress;
    private int port;
    private int timeout;
    private boolean retryOnError;
    private int retryIntervalSeconds;

    private ListenThread listenThread;
    private PublishThread publishThread;
    private ArrayList<DatagramPacket> publishEventList = new ArrayList<DatagramPacket>();
    private boolean running;
    private Object lockObject = new Object();

    private Vector<UdpMessageStatusEventListener> statusEventListeners = new Vector<UdpMessageStatusEventListener>();
    private Vector<UdpMessageDataEventListener> dataEventListeners = new Vector<UdpMessageDataEventListener>();

    private static Log logger = null;

    public UdpMessageManager() {
    }

    public UdpMessageManager(String hostName, int port, int timeout, boolean retryOnError, int retryIntervalSeconds) {
        this(IPAddressUtils.getIPAddressesForHostName(hostName)[0], port, timeout, retryOnError, retryIntervalSeconds);
    }

    public UdpMessageManager(InetAddress ipAddress, int port, int timeout, boolean retryOnError, int retryIntervalSeconds) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.timeout = timeout;
        this.retryOnError = retryOnError;
        this.retryIntervalSeconds = retryIntervalSeconds;
        if (logger == null) {
            logger = LogFactory.getLog(this.getClass());
        }
    }

    public void start() throws Exception {
        if (running) {
            return;
        }
        synchronized (lockObject) {
            if (running) {
                return;
            }
            try {
                running = true;

                listenThread = new ListenThread(this);
                listenThread.start();

                publishEventList.clear();
                publishThread = new PublishThread(this);
                publishThread.start();
            }
            catch (Exception ex) {
                running = false;
                if (listenThread != null) {
                    try {
                        listenThread.interrupt();
                    }
                    catch (Exception ex2) {
                    }
                }

                if (publishThread != null) {
                    try {
                        publishThread.interrupt();
                    }
                    catch (Exception ex2) {
                    }
                }

                throw ex;
            }
        }
    }

    public void stop() {
        if (!running) {
            return;
        }

        synchronized (lockObject) {
            if (!running) {
                return;
            }

            try {
                publishThread.interrupt();
            }
            catch (Exception ex) {
            }
            finally {
                publishThread = null;
            }

            try {
                listenThread.interrupt();
            }
            catch (Exception ex) {
            }
            finally {
                running = false;
                listenThread = null;
            }

            // Shutdown connection.
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (Exception ex) {
                }
                finally {
                    socket = null;
                    notifyConnectionClosed(new UdpMessageStatusEvent());
                }
            }
        }
    }

    private void stopInNewThread() {
        StopThread st = new StopThread(this);
        st.start();
    }

    // End data
    public void send(byte[] bytes, InetAddress ipAddress, int port) throws IOException {
        send(new DatagramPacket(bytes, bytes.length, ipAddress, port));
    }

    public void send(DatagramPacket packet) throws IOException {
        if (!this.running) {
            return;
        }

        /*synchronized (lockObject) {
            if (!running) {
                return;
            }*/
        if (this.socket != null) {
            this.socket.send(packet);
        }
        //}
    }

    // Event related methods.

    public void addEventListener(UdpMessageStatusEventListener listener) {
        if (!statusEventListeners.contains(listener)) {
            statusEventListeners.add(listener);
        }
    }

    public void addEventListener(UdpMessageDataEventListener listener) {
        if (!dataEventListeners.contains(listener)) {
            dataEventListeners.add(listener);
        }
    }

    public void removeEventListener(UdpMessageStatusEventListener listener) {
        if (statusEventListeners.contains(listener)) {
            statusEventListeners.remove(listener);
        }
    }

    public void removeEventListener(UdpMessageDataEventListener listener) {
        if (dataEventListeners.contains(listener)) {
            dataEventListeners.remove(listener);
        }
    }

    private void notifyConnectionEstablished(UdpMessageStatusEvent e) {
        for (UdpMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionEstablished(e);
        }
    }

    private void notifyConnectionError(UdpMessageStatusEvent e) {
        for (UdpMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionError(e);
        }
    }

    private void notifyConnectionClosed(UdpMessageStatusEvent e) {
        for (UdpMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionClosed(e);
        }
    }

    private void notifyMessageReceived(UdpMessageDataEvent e) {
        for (UdpMessageDataEventListener listener : dataEventListeners) {
            listener.messageReceived(e);
        }
    }

    private void notifyMessageReceiveError(UdpMessageStatusEvent e) {
        for (UdpMessageStatusEventListener listener : statusEventListeners) {
            listener.messageReceiveError(e);
        }
    }

    private static class StopThread extends Thread {
        private UdpMessageManager parent;
        public StopThread(UdpMessageManager parent) {
            this.parent = parent;
        }

        public void run() {
            parent.stop();
        }
    }

    private static class ListenThread extends Thread {
        private UdpMessageManager parent;

        public ListenThread(UdpMessageManager parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            while (parent.running) {
                try {
                    if (parent.socket == null) {
                        synchronized (parent.lockObject) {
                            if (!parent.running) {
                                break;
                            }

                            try {
                                if (parent.ipAddress == null) {
                                    parent.socket = new DatagramSocket(parent.port);
                                }
                                else {
                                    parent.socket = new DatagramSocket(parent.port, parent.ipAddress);
                                }

                                parent.notifyConnectionEstablished(new UdpMessageStatusEvent());
                            }
                            catch (Exception ex) {
                                parent.notifyConnectionError(new UdpMessageStatusEvent(ex.getMessage()));
                                if (parent.retryOnError) {
                                    parent.socket = null;
                                    Thread.sleep(parent.retryIntervalSeconds * 1000);
                                    continue;
                                }
                                else {
                                    parent.stopInNewThread();
                                    break;
                                }
                            }
                        }
                    }

                    // Receive packet from tag
                    byte[] buffer = new byte[4096];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    parent.socket.setSoTimeout(parent.timeout);
                    parent.socket.receive(packet);
                    synchronized (parent.publishEventList) {
                        logger.debug("UdpMessageManager: Adding packet to event list.");
                        parent.publishEventList.add(packet);
                        parent.publishEventList.notifyAll();
                    }
                }
                catch (InterruptedException iEx) {
                    break;
                }
                catch (SocketTimeoutException stEx) {
                    continue;
                }
                catch (Exception ex) {
                    parent.notifyMessageReceiveError(new UdpMessageStatusEvent(ex.getMessage()));
                    if (parent.retryOnError) {
                        try {
                            Thread.sleep(parent.retryIntervalSeconds * 1000);
                        }
                        catch (InterruptedException iEx) {
                            break;
                        }
                    }
                    else {
                        parent.stopInNewThread();
                        break;
                    }
                }
            }
        }
    }

    private static class PublishThread extends Thread {
        private UdpMessageManager parent;

        public PublishThread(UdpMessageManager parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            while (parent.running) {
                DatagramPacket[] publishEvents = null;
                synchronized (parent.publishEventList) {
                    if (!parent.publishEventList.isEmpty()) {
                        publishEvents = parent.publishEventList.toArray(new DatagramPacket[parent.publishEventList.size()]);
                        parent.publishEventList.clear();
                    }
                    else {
                        try {
                            parent.publishEventList.wait();
                        }
                        catch (InterruptedException iEx) {
                            break;
                        }

                        if (!parent.publishEventList.isEmpty()) {
                            publishEvents = parent.publishEventList.toArray(new DatagramPacket[parent.publishEventList.size()]);
                            parent.publishEventList.clear();
                        }
                        else {
                            continue;
                        }
                    }
                }

                logger.debug("UdpMessageManager:  Notifying message listeners of " + publishEvents.length + " event(s).");
                for (DatagramPacket publishEvent : publishEvents) {
                    parent.notifyMessageReceived(new UdpMessageDataEvent(publishEvent));
                }

                logger.debug("UdpMessageManager: Done notifying message listeners.");
            }
        }
    }
}
