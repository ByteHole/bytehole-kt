package com.github.bytehole.channel

import org.junit.jupiter.api.Test

class TestUDPReceiver {

    @Test
    fun testListen() {
        val udpReceiver = UDPReceiver(8888)
        udpReceiver.listen(object : UDPReceiver.OnReceiveListener {
            override fun onReceive(data: ByteArray) {
                println(String(data))
            }
        })
        val sender = UDPSender()
        sender.send("255.255.255.255", 8888, "testListen -->")
    }
}