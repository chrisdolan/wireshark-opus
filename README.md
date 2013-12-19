wireshark-opus
==============

Parses a Wireshark PDML file that contains an RTP+Opus SIP call and outputs wave audio files


Why?
====

Republic Wireless calls over Wi-Fi use an authenticated but
unencrypted SIP/RTP/Opus session to send and receive audio. Wireshark
can decode those SIP sessions, but it doesn't know how to work with
Opus-encoded audio. So, this code decodes the Opus to Wave audio.

Overview
========

This is a *very rough prototype* that uses Java to parse an XML file
output from Wireshark. The Java code finds any RTP streams in the
session data. Then it assumes those streams are encoded with Opus, so
it passes the payload bytes packet-by-packet to libopus. That library
returns linear PCM audio. The Java code adds a WAVE header to the PCM
audio and writes it to files, one file per RTP stream.

The code assumes a perfect RTP stream. It doesn't handle jitter or
lost packets.

Building the code
=================

Yeah, this is ugly. It's hard-coded to the location of the JDK and
Fink on my Mac, but at least those are in standard locations.

I think I may have had to do something to get the 1.6 JDK installed on
this Mac, but it was a long time ago and I don't remember the
details...

1) Install Fink in /sw from finkproject.net

2) "fink install libopus0-dev libopus0-shlibs"

3) cd wireshark-opus

4) mvn

How to capture PDML
===================

This is how I did it. Your steps may differ.

I have a Republic Wireless Moto X phone, a desktop Mac running 10.8,
and a Wi-Fi DSL router. My phone usually connects directly to the
router via Wi-Fi, but I want to capture packets and my router sensibly
doesn't easily allow my Mac access to those packets. So I temporarily
turned on Internet Sharing in the Mac's System Preferences and made a
Wi-Fi hotspot.

I changed my phone to connect to the Mac's Wi-Fi SSID instead of the
router. I verified that the phone can connect to the internet through
the Mac by testing a few webpages. I used netstat to see that the
phone's bridged IPv4 address is 192.168.2.2.

I previously installed Wireshark 1.10 from wireshark.org. Now I opened
Wireshark and configured it as follows. I clicked Capture Options and
added a capture filter of "host 192.168.2.2". This keeps only IPv4
packets to/from the phone and ignores all others. Then I selected the
"bridge0" interface and started capturing. Immediately, I saw traffic
from the phone, connecting via HTTPS to Google and via SIP to
phonebooth.net, which is a VoIP service owned by Republic Wireless's
parent compant, Bandwidth.com.

I then placed a call from the phone. I ensured that the phone was
using "Wi-Fi" rather than "Cell" since my current setup can't capture
the latter. After checking the connection, I spoke a few words and
hung up -- total call time about 15 seconds. I stopped the capture.

I then selected File > Export Packet Dissection > as XML - PDML
file. I saved this as "sipcall.pdml", which is included in the test
resources of this project. For my privacy, I changed the phone numbers
in this file. I may have missed some private details, so I humbly ask
that you do not troll through the file looking for anything sensitive.

How to transcode the PDML
=========================

I tried not to hard-code too much about the PDML into the Java code,
but I surely made some unwarranted assumptions...

    % cd net.chrisdolan.telephony.wireshark-opus
    % java -cp ../net.chrisdolan.telephony.libopus/target/net.chrisdolan.telephony.libopus-0.0.1-SNAPSHOT.jar:target/net.chrisdolan.telephony.wireshark-opus-0.0.1-SNAPSHOT.jar net.chrisdolan.telephony.wireshark.opus.Main src/test/resources/net/chrisdolan/telephony/wireshark/opus/sipcall.pdml /tmp
    % open /tmp/rtp-opus*.wav

What's in the capture
=====================

There are several interesting details in the Wireshark capture besides
just the SIP call. Load into Wireshark the included file
src/test/resources/net/chrisdolan/telephony/wireshark/opus/sipcall.pdml

1) At frame 8 and 17+ the phone makes DNS and SSL calls to
googleapis.l.google.com. I've read (not sure where) that Android
decides whether it's WAN-connected by contacting Google. That may be
what this is, or it just might be random sync/notification behavior.

2) At frame 52 looks up a domain on sip.nocell.io which is really a
phonebooth.net server. I'll call this "RW".

3) In frames 54-63, the phone re-authorizes with RW. The credentials
are hashed with MD5 and a server-chosen nonce, which appears
random. Unfortunately the client "CNonce" is a hard-coded value of
"xyz" which is nearly worthless.

4) At frame 70-123 the phone asks DNS for "echo.republicwireless.com"
which resolves to an Amazon EC2 AWS server. The phone then sends a
bunch of empty UDP packets to that server, which are reflected
intact. I presume this is the phone's test of the Wi-Fi connection to
ensure latency is low enough for a good quality phone call.

5) In frames 124-128 and 133-135, the phone initiates the VoIP
call. Frame 133 is particularly important because it contains the
"SDP" or the SIP Description Protocol metadata about the codec that
will be used to encode the audio. Of note, I need the "8000" value
from this packet which is the audio sample rate in Hz. This is much
lower than the 44100 Hz value used for CD audio, but it qualifies as
"HD voice" because it's higher than 7 kHz. Non-HD voice calls are
often clipped at 3.4 kHz.

6) Frames 139-1543 contain the encoded audio data. About half of the
packets are outbound and the other half inbound. The SSRC tells which
stream each packet belongs to.
