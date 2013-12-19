package net.chrisdolan.telephony.libopus;

import java.io.File;

// javah -classpath target/classes -d src/main/cpp net.chrisdolan.telephony.libopus.OpusDecoder

public class OpusDecoder {
    private long nativeDecoder = 0;
    private final int sampleRate;
    private final int channels;
    private final short[] buffer;

    public OpusDecoder(int sampleRate, byte[] firstPacket) throws Exception {
        loadLibrary();
        this.sampleRate = sampleRate;
        if (sampleRate <= 0)
            throw new IllegalArgumentException("Invalid sample rate");
        this.channels = nativeParseChannels(firstPacket);
        if (channels < 1)
            throw new Exception("Failed to parse channels count from the first packet");
        this.buffer = buildDecoder();
    }
    public OpusDecoder(SampleRate sampleRate, Channels channels) throws Exception {
        //System.out.println("java.library.path=" + System.getProperty("java.library.path"));
        loadLibrary();
        this.sampleRate = sampleRate.getRate();
        this.channels = channels.getCount();
        this.buffer = buildDecoder();
    }
    private short[] buildDecoder() throws Exception {
        short[] buffer = new short[5760 * this.channels];
        nativeDecoder = nativeCreate(this.sampleRate, this.channels);
        if (nativeDecoder == 0)
            throw new Exception();
        return buffer;
    }
    private void loadLibrary() {
        try {
            System.loadLibrary("opus");
        } catch (UnsatisfiedLinkError e) {
            try {
                System.load(new File("target/native/libopus.jnilib").getAbsolutePath());
            } catch (UnsatisfiedLinkError e2) {
                System.load(new File("target/dependency/libopus.jnilib").getAbsolutePath());
            }
        }
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public short[] decode(byte[] packet) throws Exception {
        int decoded = nativeDecode(nativeDecoder, packet, buffer, 5760);
        if (decoded <= 0)
            throw new Exception();
        int samples = decoded * channels;
        short[] out = new short[samples];
        System.arraycopy(buffer, 0, out, 0, samples);
        return out;
    }

    public void destroy() {
        synchronized (this) {
            if (nativeDecoder != 0) {
                nativeDestroy(nativeDecoder);    
                nativeDecoder = 0;
            }
        }
    }

    public void finalize() {
        destroy();
    }

    private native long nativeCreate(int samplerate, int channels);
    private native int nativeParseChannels(byte[] packet);
    private native int nativeDecode(long nativeDecoder, byte[] packet, short[] buffer, int nframes);
    private native void nativeDestroy(long nativeDecoder);
}
