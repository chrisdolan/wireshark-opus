package net.chrisdolan.telephony.wireshark.opus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.chrisdolan.telephony.wireshark.opus.Pdml.Field;
import net.chrisdolan.telephony.wireshark.opus.Pdml.Packet;
import net.chrisdolan.telephony.wireshark.opus.Pdml.Protocol;

public class RtpExtracter {
    public RtpStream[] parse(Pdml pdml) {
        Map<String,RtpStream> out = new LinkedHashMap<String,RtpStream>();
        for (Packet packet : pdml.packets) {
            for (Protocol protocol : packet.protocols) {
                if ("rtp".equals(protocol.name)) {
                    Map<String, Field> fields = protocol.asMap();
                    if (fields.containsKey("rtp.ssrc")) {
                        String ssrc = fields.get("rtp.ssrc").value;
                        RtpStream stream = out.get(ssrc);
                        if (null == stream) {
                            stream = new RtpStream(ssrc);
                            out.put(ssrc, stream);
                            if (fields.containsKey("rtp.setup")) {
                                int setupFrame = Integer.parseInt(fields.get("rtp.setup").asMap().get("rtp.setup-frame").show);
                                stream.setSetupPacket(pdml.packets[setupFrame-1]);
                            }
                        }
                        stream.add(new RtpPacket(fields));
                    }
                }
            }
        }
        return out.values().toArray(new RtpStream[out.size()]);
    }

    public static class RtpStream {
        private String ssrc;
        private List<RtpPacket> packets = new ArrayList<RtpPacket>();
        private Packet setupPacket;

        public RtpStream(String ssrc) {
            this.ssrc = ssrc;
        }

        private void setSetupPacket(Packet setupPacket) {
            this.setupPacket = setupPacket;
        }

        private void add(RtpPacket packet) {
            packets.add(packet);
        }
        public String getSsrc() {
            return ssrc;
        }
        public RtpPacket[] getPackets() {
            return packets.toArray(new RtpPacket[packets.size()]);
        }

        public int getSampleRate() {
            if (setupPacket == null)
                throw new IllegalStateException("No setup packet found");
            Protocol sip = setupPacket.asMap().get("sip");
            Field body = sip.asMap().get("sip.msg_body");
            Protocol sdp = body.asProtocolMap().get("sdp");
            //Field media = sdp.asMap().get("sdp.media");
            Map<String, Integer> sampleRatesByMediaCode = new HashMap<String, Integer>();
            for (Field field : sdp.fields) {
                if ("sdp.media_attr".equals(field.name) && field.asMap().containsKey("sdp.sample_rate")) {
                    int sampleRate = Integer.parseInt(field.asMap().get("sdp.sample_rate").show);
                    String mediaCode = field.asMap().get("sdp.media.format").show;
                    sampleRatesByMediaCode.put(mediaCode, sampleRate);
                }
            }

            RtpPacket first = packets.get(0);
            String mediaCode = first.fields.get("rtp.p_type").show;
            return sampleRatesByMediaCode.get(mediaCode);
        }
    }
    public static class RtpPacket {
        private final Map<String, Field> fields;
        public RtpPacket(Map<String, Field> fields) {
            this.fields = fields;
        }
        public Map<String, Field> getFields() {
            return fields;
        }
        public byte[] getPayload() {
            String s = fields.get("rtp.payload").value;
            int len = s.length();
            byte[] b = new byte[len/2];
            for (int i=0; i<b.length; ++i) {
                b[i] = (byte) Integer.parseInt(s.substring(i*2, i*2+2), 16);
            }
            return b;
        }
    }
}
