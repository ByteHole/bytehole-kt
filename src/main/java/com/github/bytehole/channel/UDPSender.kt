package com.github.bytehole.channel

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class UDPSender(private val executor: Executor = Executors.newSingleThreadExecutor()) {

    var isOpen = false
        private set

    private val socket by lazy {
        isOpen = true
        DatagramSocket()
    }

    fun send(ip: String, port: Int, data: ByteArray) {
        val packet = DatagramPacket(data, data.size, InetAddress.getByName(ip), port)
        socket.send(packet)
    }

    fun send(ip: String, port: Int, message: String) {
        val task = Runnable {
            send(ip, port, message.toByteArray())
        }
        executor.execute(task)
    }

    fun close() {
        if (isOpen) {
            isOpen = false
            socket.close()
        }
    }

}