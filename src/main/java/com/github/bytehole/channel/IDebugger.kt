package com.github.bytehole.channel

interface IDebugger {

    fun onBroadcastReady()
    fun onBroadcastReceived(fromIp: String, data: ByteArray)

    fun onMessageReady()
    fun onMessageReceived(fromIp: String, data: ByteArray)

}