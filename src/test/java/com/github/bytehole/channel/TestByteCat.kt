package com.github.bytehole.channel

import org.junit.jupiter.api.Test

class TestByteCat {
    @Test
    fun testSetup() {
        val byteCat = ByteCat()
        byteCat.startup()
    }
}