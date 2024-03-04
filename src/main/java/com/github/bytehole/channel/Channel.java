package com.github.boybeak.bytehole.channel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Channel {

    private String ip;
    private int port;

    private OnReceiveListener onReceiveListener;

    private InetAddress ipAddress;
    private DatagramSocket socket;

    public Channel(String ip, int port) {
        this.ip = ip;
        this.port = port;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpened()) {
                    // 创建一个缓冲区，用于存储接收到的数据
                    byte[] buffer = new byte[1024];
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
            }
        }).start();
    }

    public void send(String message) throws Exception {
        if (!isOpened()) {
            throw new IllegalStateException("Call open() before send().");
        }
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
