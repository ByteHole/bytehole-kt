package com.github.bytehole.channel

import org.junit.jupiter.api.Test

class TestUDPReceiver {

    @Test
    fun testListen() {
        val udpReceiver = UDPReceiver(8888)
        udpReceiver.listen(object : UDPReceiver.OnReceiveListener {
            override fun onReady() {
                val sender = UDPSender()
                sender.send("255.255.255.255", 8888, "testListen -->")
            }

            override fun onReceive(data: ByteArray) {
                println(String(data))
            }
        })
    }

    @Test
    fun testMultiReceiverListenerSamePort() {
        val listener = object : UDPReceiver.OnReceiveListener {
            override fun onReady() {

            }

            override fun onReceive(data: ByteArray) {
            }
        }

        val udpReceiver1 = UDPReceiver(8888)
        val listen1 = udpReceiver1.listen(listener)

        val udpReceiver2 = UDPReceiver(8888)
        val listen2 = udpReceiver2.listen(listener)

        assert(listen1 && !listen2)
    }

}