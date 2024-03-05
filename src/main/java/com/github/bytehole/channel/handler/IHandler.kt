package com.github.bytehole.channel.handler

interface IHandler {
    fun post(task: Runnable)
}