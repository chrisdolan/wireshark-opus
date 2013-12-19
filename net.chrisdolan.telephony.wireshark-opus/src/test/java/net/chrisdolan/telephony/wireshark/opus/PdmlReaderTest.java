package net.chrisdolan.telephony.wireshark.opus;

import java.net.URL;

import org.junit.Test;
import org.junit.Assert;

public class PdmlReaderTest {

    @Test
    public void test() throws Exception {
        URL pdmlUrl = PdmlReaderTest.class.getResource("sipcall.pdml");
        Assert.assertNotNull(pdmlUrl);
        Pdml pdml = new PdmlReader().unmarshall(pdmlUrl);
        Assert.assertNotNull(pdml);
        Assert.assertEquals(1571, pdml.packets.length);
        Assert.assertEquals(6, pdml.packets[300].protocols.length);
        Assert.assertEquals("rtp", pdml.packets[300].protocols[5].name);
        Assert.assertEquals(12, pdml.packets[300].protocols[5].fields.length);
        Assert.assertEquals("rtp.payload", pdml.packets[300].protocols[5].fields[11].name);
        Assert.assertEquals("080b88fef3bc8a9f4c55fe46a1281fbeb1a4b4ee9ac01ff880", pdml.packets[300].protocols[5].fields[11].value);
    }
}
