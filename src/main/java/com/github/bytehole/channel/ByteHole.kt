package com.github.bytehole.channel

import com.alibaba.fastjson2.JSONObject
import com.github.bytehole.channel.handler.IHandler
import com.github.bytehole.channel.handler.SimpleHandler
import java.util.UUID

open class ByteHole {

    companion object {
        private const val BROADCAST_IP = "255.255.255.255"

        private val BROADCAST_PREPARE_PORTS = arrayOf(1123, 5813, 2134, 5589, 3141)
        private val MESSAGE_PREPARE_PORTS = arrayOf(3211, 3185, 4312, 9855, 1413)
    }

    open val debugger: IDebugger? = null

    private val broadcastListener = object : UDPReceiver.OnReceiveListener {
        override fun onReady() {
            debugger?.onBroadcastReady()
            trySayHiToAll()
        }

        override fun onReceive(fromIp: String, data: ByteArray) {
            debugger?.onBroadcastReceived(fromIp, data)
            dispatchReceive(fromIp, data) { event, jsonObj ->
                when(event) {
                    EVENT_HI2A -> {
                        val messagePort = jsonObj.getIntValue("messagePort")
                        udpSender.send(fromIp, messagePort, hi2You(byteHoleId, broadcastReceiver.port, messageReceiver.port))
                    }
                }
            }
        }
    }
    private val messageListener = object : UDPReceiver.OnReceiveListener {
        override fun onReady() {
            debugger?.onMessageReady()
            trySayHiToAll()
        }

        override fun onReceive(fromIp: String, data: ByteArray) {
            debugger?.onMessageReceived(fromIp, data)
            val text = String(data)
            println("messageReceive $text")
        }
    }

    private lateinit var broadcastReceiver: UDPReceiver
    private lateinit var messageReceiver: UDPReceiver

    open val handler: IHandler by lazy { SimpleHandler().also { it.start() } }

    private val udpSender by lazy { UDPSender() }

    private val byteHoleId = UUID.randomUUID().toString()

    @Volatile
    private var isReady = false

    fun setup() {
        for (port in BROADCAST_PREPARE_PORTS) {
            val receiver = UDPReceiver(port)
            if (receiver.listen(broadcastListener)) {
                if (!::broadcastReceiver.isInitialized) {
                    broadcastReceiver = receiver
                    break
                }
            }
        }
        for (port in MESSAGE_PREPARE_PORTS) {
            val receiver = UDPReceiver(port)
            if (receiver.listen(messageListener)) {
                if (!::messageReceiver.isInitialized) {
                    messageReceiver = receiver
                    break
                }
            }
        }
        if (!::broadcastReceiver.isInitialized || !::messageReceiver.isInitialized) {
            throw IllegalStateException("No legal ports available.")
        }

    }

    @Synchronized
    private fun trySayHiToAll() {
        if (isReady) {
            return
        }
        if (!broadcastReceiver.isReady || !messageReceiver.isReady) {
            return
        }
        handler.post {
            for (port in BROADCAST_PREPARE_PORTS) {
                udpSender.send(
                    BROADCAST_IP, port,
                    hi2All(byteHoleId, broadcastReceiver.port, messageReceiver.port)
                )
            }
        }

        isReady = true
    }

    private fun dispatchReceive(ip: String, data: ByteArray, handler: (event: String, jsonObj: JSONObject) -> Unit) {
        val text = String(data)
        val jsonObj = JSONObject.parseObject(text)
        val byteHoleId = jsonObj.getString("byteHoleId")
        if (this.byteHoleId == byteHoleId) {
            return
        }
        val event = jsonObj.getString("event")
        handler.invoke(event, jsonObj)
    }

}