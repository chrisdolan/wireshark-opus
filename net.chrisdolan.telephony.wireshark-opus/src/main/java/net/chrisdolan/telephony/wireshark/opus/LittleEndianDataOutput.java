package net.chrisdolan.telephony.wireshark.opus;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class LittleEndianDataOutput implements DataOutput {

    private ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
    private DataOutputStream d = new DataOutputStream(baos);
    private DataOutput out;

    public LittleEndianDataOutput(DataOutput out) {
        this.out = out;
    }

    private void swapOut() throws IOException {
        byte[] b = baos.toByteArray();
        for (int i=b.length-1; i>=0; --i)
            out.write(b[i]);
        baos.reset();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        d.writeBoolean(v);
        swapOut();
    }

    @Override
    public void writeByte(int v) throws IOException {
        out.write(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        d.writeBytes(s);
        swapOut();
    }

    @Override
    public void writeChar(int v) throws IOException {
        d.writeChar(v);
        swapOut();
    }

    @Override
    public void writeInt(int v) throws IOException {
        d.writeInt(v);
        swapOut();
    }

    @Override
    public void writeChars(String s) throws IOException {
        for (int i=0; i<s.length(); ++i) {
            d.writeChar(s.charAt(i));
            swapOut();
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        d.writeDouble(v);
        swapOut();
    }

    @Override
    public void writeFloat(float v) throws IOException {
        d.writeFloat(v);
        swapOut();
    }

    @Override
    public void writeLong(long v) throws IOException {
        d.writeLong(v);
        swapOut();
    }

    @Override
    public void writeShort(int v) throws IOException {
        d.writeShort(v);
        swapOut();
    }

    @Override
    public void writeUTF(String s) throws IOException {
        d.writeUTF(s);
        out.write(baos.toByteArray());
        baos.reset();
    }
}
