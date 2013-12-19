package net.chrisdolan.telephony.libopus;

public enum Channels {
    Mono(1),
    Stereo(2);

    private final int m_count;
    private Channels(int count) {
        m_count = count;
    }
    public int getCount() {
        return m_count;
    }
}