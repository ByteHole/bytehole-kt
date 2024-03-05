package com.github.bytehole.channel

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.Executor

class UDPReceiver(private val port: Int, private val executor: Executor? = null, val bufferSize: Int = 1024) {

    @Volatile
    var isListening = false
        private set

    private var receiveListener: OnReceiveListener? = null

    private val task = Runnable {
        while (isListening) {
            val bytes = ByteArray(bufferSize)
            val packet = DatagramPacket(bytes, bytes.size)
            try {
                socket.receive(packet)
            } catch (e: Exception) {
                if (isListening) {
                    e.printStackTrace()
                } else {
                    return@Runnable
                }
            }

            val data = packet.data.copyOfRange(packet.offset, packet.offset + packet.length)
            receiveListener?.onReceive(data)
        }
    }

    private val socket by lazy { DatagramSocket(port) }

    fun listen(listener: OnReceiveListener) {
        if (isListening) {
            return
        }
        isListening = true
        receiveListener = listener
        if (executor == null) {
            Thread(task).start()
        } else {
            executor.execute(task)
        }
    }

    fun close() {
        if (isListening) {
            isListening = false
            socket.close()
        }
        receiveListener = null
    }

    interface OnReceiveListener {
        fun onReceive(data: ByteArray)
    }

}