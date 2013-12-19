package net.chrisdolan.telephony.wireshark.opus;

import java.io.File;
import java.net.URL;

import net.chrisdolan.telephony.libopus.Channels;
import net.chrisdolan.telephony.libopus.SampleRate;

import org.junit.Assert;
import org.junit.Test;

public class OpusToWaveConverterTest {
    @Test
    public void test() throws Exception {
        URL pdmlUrl = RtpExtracterTest.class.getResource("sipcall.pdml");
        Assert.assertNotNull(pdmlUrl);
        File[] waveFiles = new File[2];
        for (int i=0; i<waveFiles.length; ++i)
            waveFiles[i] = File.createTempFile(OpusToWaveConverterTest.class.getSimpleName(), ".wav");
        try {
            new OpusToWaveConverter().convert(pdmlUrl, waveFiles, SampleRate.Rate_8Khz, Channels.Mono);
            new OpusToWaveConverter().convert(pdmlUrl, waveFiles, null, null);
        } finally {
            for (int i=0; i<waveFiles.length; ++i)
                waveFiles[i].delete();
        }
    }
}
