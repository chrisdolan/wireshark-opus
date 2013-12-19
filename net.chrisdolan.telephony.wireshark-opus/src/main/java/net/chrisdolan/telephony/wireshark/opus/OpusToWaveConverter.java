package net.chrisdolan.telephony.wireshark.opus;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.charset.Charset;

import net.chrisdolan.telephony.libopus.Channels;
import net.chrisdolan.telephony.libopus.OpusDecoder;
import net.chrisdolan.telephony.libopus.SampleRate;
import net.chrisdolan.telephony.wireshark.opus.RtpExtracter.RtpPacket;
import net.chrisdolan.telephony.wireshark.opus.RtpExtracter.RtpStream;

public class OpusToWaveConverter {

    public void convert(URL pdmlUrl, File waveFileDir, SampleRate sampleRate, Channels channels) throws Exception {
        if (!waveFileDir.isDirectory())
            throw new IllegalArgumentException();
        Pdml pdml = new PdmlReader().unmarshall(pdmlUrl);
        RtpStream[] streams = new RtpExtracter().parse(pdml);
        for (int i=0; i<streams.length; ++i) {
            File waveFile = new File(waveFileDir, "rtp-opus-" + (i+1) + ".wav");
            decodeOneStream(waveFile, sampleRate, channels, streams[i]);
        }
    }

    public void convert(URL pdmlUrl, File waveFiles[], SampleRate sampleRate, Channels channels) throws Exception {
        Pdml pdml = new PdmlReader().unmarshall(pdmlUrl);
        RtpStream[] streams = new RtpExtracter().parse(pdml);
        if (waveFiles.length != streams.length)
            throw new IllegalArgumentException();
        for (int i=0; i<streams.length; ++i) {
            decodeOneStream(waveFiles[i], sampleRate, channels, streams[i]);
        }
    }

    private void decodeOneStream(File waveFile, SampleRate sampleRate, Channels channels, RtpStream stream) throws Exception {
        OpusDecoder decoder;
        if (channels == null)
            decoder = new OpusDecoder(stream.getSampleRate(), stream.getPackets()[0].getPayload());
        else
            decoder = new OpusDecoder(sampleRate, channels);
        try {
            RandomAccessFile f = new RandomAccessFile(waveFile, "rw");
            f.setLength(0);
            LittleEndianDataOutput outStream = new LittleEndianDataOutput(f);
            try {
                writeWaveHeader(decoder, outStream, 44);
                if (f.length() != 44)
                    throw new IllegalStateException("Wave header should be 44 bytes, not " + f.length());
                convert(decoder, stream, outStream);
                long fileSize = f.length();
                f.seek(0);
                writeWaveHeader(decoder, outStream, fileSize);
            } finally {
                f.close();
            }
        } finally {
            decoder.destroy();
        }
    }

    private void writeWaveHeader(OpusDecoder decoder, DataOutput os, long fileSize) throws IOException {

        // http://www.topherlee.com/software/pcm-tut-wavformat.html

        //        Positions     Sample Value     Description
        //        1 - 4     "RIFF"     Marks the file as a riff file. Characters are each 1 byte long.
        //        5 - 8     File size (integer)     Size of the overall file - 8 bytes, in bytes (32-bit integer). Typically, you'd fill this in after creation.
        //        9 -12     "WAVE"     File Type Header. For our purposes, it always equals "WAVE".
        //        13-16     "fmt "     Format chunk marker. Includes trailing null
        //        17-20     16     Length of format data as listed above
        //        21-22     1     Type of format (1 is PCM) - 2 byte integer
        //        23-24     2     Number of Channels - 2 byte integer
        //        25-28     44100     Sample Rate - 32 byte integer. Common values are 44100 (CD), 48000 (DAT). Sample Rate = Number of Samples per second, or Hertz.
        //        29-32     176400     (Sample Rate * BitsPerSample * Channels) / 8.
        //        33-34     4     (BitsPerSample * Channels) / 8.1 - 8 bit mono2 - 8 bit stereo/16 bit mono4 - 16 bit stereo
        //        35-36     16     Bits per sample
        //        37-40     "data"     "data" chunk header. Marks the beginning of the data section.
        //        41-44     File size (data)     Size of the data section.

        Charset UTF8 = Charset.forName("UTF-8");
        int sampleRate = decoder.getSampleRate();
        int channels = decoder.getChannels();
        int bitsPerSample = 16;
        os.write("RIFF".getBytes(UTF8));
        os.writeInt((int)fileSize - 8);
        os.write("WAVE".getBytes(UTF8));
        os.write("fmt ".getBytes(UTF8));
        os.writeInt(16);
        os.writeShort(1);
        os.writeShort(channels);
        os.writeInt(sampleRate);
        os.writeInt(sampleRate * bitsPerSample * channels / 8 );
        os.writeShort(bitsPerSample * channels / 8);
        os.writeShort(bitsPerSample);
        os.write("data".getBytes(UTF8));
        os.writeInt((int)fileSize - 44);
    }

    public void convert(OpusDecoder decoder, RtpStream stream, DataOutput outStream) throws Exception {
        for (RtpPacket packet : stream.getPackets()) {
            short[] pcm = decoder.decode(packet.getPayload());
            for (short sample : pcm)
                outStream.writeShort(sample);
        }
    }
}
