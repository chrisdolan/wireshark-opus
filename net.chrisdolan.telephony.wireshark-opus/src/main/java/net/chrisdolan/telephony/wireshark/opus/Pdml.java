package net.chrisdolan.telephony.wireshark.opus;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Pdml {
    @XmlElement(name="packet")
    public Packet[] packets;
    
    public static class Packet {
        @XmlElement(name="proto")
        public Protocol[] protocols;

        public Map<String, Protocol> asMap() {
            Map<String, Protocol> out = new HashMap<String, Protocol>();
            for (Protocol protocol : protocols)
                out.put(protocol.name, protocol);
            return out;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("packet");
            for (Protocol pr : protocols) {
                sb.append(".").append(pr.name);
            }
            return sb.toString();
        }
    }

    public static class Protocol {
        @XmlElement(name="field")
        public Field[] fields;
        @XmlAttribute(name="name")
        public String name;

        public Map<String,Field> asMap() {
            Map<String, Field> out = new HashMap<String, Field>();
            for (Field field : fields)
                out.put(field.name, field);
            return out;
        }

        public String toString() {
            return "protocol=" + name;
        }
    }

    public static class Field {
        @XmlElement(name="proto")
        public Protocol[] protocols;
        @XmlElement(name="field")
        public Field[] fields;
        @XmlAttribute(name="name")
        public String name;
        @XmlAttribute(name="value")
        public String value;
        @XmlAttribute(name="show")
        public String show;

        public Map<String, Protocol> asProtocolMap() {
            Map<String, Protocol> out = new HashMap<String, Protocol>();
            for (Protocol protocol : protocols)
                out.put(protocol.name, protocol);
            return out;
        }

        public Map<String, Field> asMap() {
            Map<String, Field> out = new HashMap<String, Field>();
            for (Field field : fields)
                out.put(field.name, field);
            return out;
        }

        public String toString() {
            return "field=" + name + "/" + value;
        }
    }
}
