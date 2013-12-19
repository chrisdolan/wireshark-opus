package net.chrisdolan.telephony.wireshark.opus;

import java.net.URL;

import net.chrisdolan.telephony.wireshark.opus.RtpExtracter.RtpStream;

import org.junit.Test;
import org.junit.Assert;

public class RtpExtracterTest {

    @Test
    public void test() throws Exception {
        URL pdmlUrl = RtpExtracterTest.class.getResource("sipcall.pdml");
        Assert.assertNotNull(pdmlUrl);
        Pdml pdml = new PdmlReader().unmarshall(pdmlUrl);
        Assert.assertNotNull(pdml);
        RtpStream[] streams = new RtpExtracter().parse(pdml);
        Assert.assertNotNull(streams);
        Assert.assertEquals(2, streams.length);
        Assert.assertEquals("ce8947be", streams[0].getSsrc());
        Assert.assertEquals("607f30aa", streams[1].getSsrc());
        Assert.assertEquals(700, streams[0].getPackets().length);
        Assert.assertEquals(707, streams[1].getPackets().length);
        Assert.assertArrayEquals(new byte[] {0x08, 0x0b, (byte) 0xe4, 0x53, 0x14, 0x7e, 0x10}, streams[0].getPackets()[0].getPayload());
//        
//        
//        Assert.assertEquals(6, pdml.packets[300].protocols.length);
//        Assert.assertEquals("rtp", pdml.packets[300].protocols[5].name);
//        Assert.assertEquals(12, pdml.packets[300].protocols[5].fields.length);
//        Assert.assertEquals("rtp.payload", pdml.packets[300].protocols[5].fields[11].name);
//        Assert.assertEquals("080b88fef3bc8a9f4c55fe46a1281fbeb1a4b4ee9ac01ff880", pdml.packets[300].protocols[5].fields[11].value);
    }
}
