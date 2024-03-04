package com.github.bytehole.channel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class Channel {

    private static final int BUFFER_SIZE_DEFAULT = 1024;

    private final String ip;
    private final int port;

    private OnReceiveListener onReceiveListener;

    private InetAddress ipAddress;
    private DatagramSocket socket;

    private final Executor sendExecutor;
    private final Executor receiveExecutor;

    private int bufferSize;

    Runnable receiveTask = () -> {
        while (isOpened()) {
            // 创建一个缓冲区，用于存储接收到的数据
            byte[] buffer = new byte[bufferSize];
            // 创建一个数据包，用于接收数据
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            // 接收数据包
            try {
                socket.receive(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(packet.getAddress().getHostAddress());

            String msg = new String(packet.getData(), 0, packet.getLength());

            if (onReceiveListener != null) {
                onReceiveListener.onReceive(msg);
            }
        }
    };

    public Channel(String ip, int port, int bufferSize, Executor sendExecutor, Executor receiveExecutor) {
        this.ip = ip;
        this.port = port;
        if (bufferSize <= 0) {
            bufferSize = BUFFER_SIZE_DEFAULT;
        }
        this.bufferSize = bufferSize;
        this.sendExecutor = sendExecutor;
        this.receiveExecutor = receiveExecutor;
    }

    public Channel(String ip, int port) {
        this(ip, port, BUFFER_SIZE_DEFAULT, null, null);
    }

    public boolean isOpened() {
        return socket != null;
    }

    public void open() throws Exception {
        if (isOpened()) {
            throw new IllegalStateException("The channel has already opened.");
        }
        ipAddress = InetAddress.getByName(ip);
        socket = new DatagramSocket(port);

        startReceive();
    }

    private void startReceive() {
        if (!isOpened()) {
            return;
        }

        if (receiveExecutor == null) {
            new Thread(receiveTask).start();
        } else  {
            receiveExecutor.execute(receiveTask);
        }
    }

    public void send(String message) throws Exception {
        if (!isOpened()) {
            throw new IllegalStateException("Call open() before send().");
        }
        if (sendExecutor != null) {
            sendExecutor.execute(() -> {
                try {
                    doSend(message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            doSend(message);
        }
    }

    private void doSend(String message) throws Exception {
        InetSocketAddress sendTo = new InetSocketAddress(ipAddress, port);
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), sendTo);
        socket.send(packet);
    }

    public void setOnReceiveListener(OnReceiveListener listener) {
        onReceiveListener = listener;
    }

    public void close() {
        if (isOpened()) {
            socket.close();
            socket = null;
        }
        onReceiveListener = null;
    }

    public interface OnReceiveListener {
        void onReceive(String message);
    }
}
