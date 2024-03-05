package com.github.bytehole.channel;

import com.github.bytehole.channel.Channel;
import org.junit.jupiter.api.Test;

public class TestChannel {
    Channel channel = new Channel("255.255.255.255", 7777);

    @Test
    public void testReopen() throws Exception {
        channel.open();
        channel.close();
        channel.setOnReceiveListener(System.out::println);
        channel.open();
        channel.send("XYZ");
    }
}
