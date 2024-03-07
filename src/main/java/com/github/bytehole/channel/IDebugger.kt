package com.github.bytehole.channel

import com.github.bytehole.channel.contact.Contact

interface IDebugger {

    fun onBroadcastReady()
    fun onBroadcastReceived(fromIp: String, data: ByteArray)

    fun onMessageReady()
    fun onMessageReceived(fromIp: String, data: ByteArray)

    fun onContactAdd(contact: Contact)
    fun onContactRemove(contact: Contact)

}