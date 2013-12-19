package net.chrisdolan.telephony.wireshark.opus;

import java.io.File;
import java.net.URL;

// ( cd net.chrisdolan.telephony.wireshark-opus ; java -cp ../net.chrisdolan.telephony.libopus/target/net.chrisdolan.telephony.libopus-0.0.1-SNAPSHOT.jar:target/net.chrisdolan.telephony.wireshark-opus-0.0.1-SNAPSHOT.jar net.chrisdolan.telephony.wireshark.opus.Main src/test/resources/net/chrisdolan/telephony/wireshark/opus/sipcall.pdml /tmp )

public class Main {

    public static void main(String[] args) throws Exception {
        URL pdmlUrl = new File(args[0]).toURI().toURL();
        File waveDir = new File(args[1]);
        new OpusToWaveConverter().convert(pdmlUrl, waveDir, null, null);
    }
}
