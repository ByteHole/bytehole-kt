package com.github.bytehole.channel

import org.junit.jupiter.api.Test

class TestUDPSender {

    companion object {
        private const val BROADCAST_IP = "255.255.255.255"
    }

    @Test
    fun testSend() {

        val channel = Channel(BROADCAST_IP, 8888)
        channel.open()
        channel.setOnReceiveListener {
            println(it)
            channel.close()
        }

        val udpSender = UDPSender()
        udpSender.send(BROADCAST_IP, 8888, "TEST-AAA")
    }
}