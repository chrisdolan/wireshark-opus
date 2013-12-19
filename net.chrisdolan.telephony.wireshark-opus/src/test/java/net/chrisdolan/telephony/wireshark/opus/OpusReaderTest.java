package net.chrisdolan.telephony.wireshark.opus;

import java.net.URL;

import net.chrisdolan.telephony.libopus.Channels;
import net.chrisdolan.telephony.libopus.OpusDecoder;
import net.chrisdolan.telephony.libopus.SampleRate;

import org.junit.Test;
import org.junit.Assert;

public class OpusReaderTest {

    @Test
    public void test() throws Exception {
        OpusDecoder decoder = new OpusDecoder(SampleRate.Rate_8Khz, Channels.Mono);
        try {
            Assert.assertNotNull(decoder);
            URL pdml = OpusReaderTest.class.getResource("sipcall.pdml");
            Assert.assertNotNull(pdml);
            
        } finally {
            decoder.destroy();
        }
    }
}
