package net.chrisdolan.telephony.wireshark.opus;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class PdmlReader {
    public Pdml unmarshall(URL pdmlUrl) throws JAXBException {
        JAXBContext c = JAXBContext.newInstance(Pdml.class);
        Object o = c.createUnmarshaller().unmarshal(pdmlUrl);
        if (o instanceof Pdml)
            return (Pdml)o;
        throw new JAXBException("Wrong unmarshalled type");
    }
}
