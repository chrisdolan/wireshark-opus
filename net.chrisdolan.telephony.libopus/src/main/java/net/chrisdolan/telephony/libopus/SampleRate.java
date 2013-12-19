package net.chrisdolan.telephony.libopus;

public enum SampleRate {
    Rate_8Khz(8000),
    Rate_12Khz(12000),
    Rate_16Khz(16000),
    Rate_24Khz(24000),
    Rate_48Khz(48000);

    private final int m_rate;
    private SampleRate(int rate) {
        m_rate = rate;
    }
    public int getRate() {
        return m_rate;
    }
}