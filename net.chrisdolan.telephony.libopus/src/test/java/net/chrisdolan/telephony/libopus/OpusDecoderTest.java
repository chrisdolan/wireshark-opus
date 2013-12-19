package net.chrisdolan.telephony.libopus;

import org.junit.Test;
import org.junit.Assert;

public class OpusDecoderTest {

    @Test
    public void test() throws Exception {
        OpusDecoder decoder = new OpusDecoder(SampleRate.Rate_8Khz, Channels.Mono);
        try {
            Assert.assertNotNull(decoder);
        } finally {
            decoder.destroy();
        }
    }
}
