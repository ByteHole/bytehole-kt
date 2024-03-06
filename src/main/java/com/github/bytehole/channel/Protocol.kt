package com.github.bytehole.channel

import com.alibaba.fastjson2.JSONObject
import java.util.UUID

const val EVENT_HI2A = "hi2a"
const val EVENT_HI2U = "hi2u"
const val EVENT_BYE2A = "bye2a"

const val KEY_EVENT = "event"
const val KEY_EVENT_ID = "eventId"
const val KEY_BYTE_HOLE_ID = "byteHoleId"
const val KEY_BROADCAST_PORT = "broadcastPort"
const val KEY_MESSAGE_PORT = "messagePort"

fun hi2All(byteHoleId: String, broadcastPort: Int, messagePort: Int): String {
    return JSONObject().apply {
        put(KEY_EVENT, EVENT_HI2A)
        put(KEY_EVENT_ID, UUID.randomUUID().toString())
        put(KEY_BYTE_HOLE_ID, byteHoleId)
        put(KEY_BROADCAST_PORT, broadcastPort)
        put(KEY_MESSAGE_PORT, messagePort)
    }.toString()
}

fun bye2All(byteHoleId: String): String {
    return JSONObject().apply {
        put(KEY_EVENT, EVENT_BYE2A)
        put(KEY_EVENT_ID, UUID.randomUUID().toString())
        put(KEY_BYTE_HOLE_ID, byteHoleId)
    }.toString()
}

fun hi2You(byteHoleId: String, broadcastPort: Int, messagePort: Int): String {
    return JSONObject().apply {
        put(KEY_EVENT, EVENT_HI2U)
        put(KEY_EVENT_ID, UUID.randomUUID().toString())
        put(KEY_BYTE_HOLE_ID, byteHoleId)
        put(KEY_BROADCAST_PORT, broadcastPort)
        put(KEY_MESSAGE_PORT, messagePort)
    }.toString()
}