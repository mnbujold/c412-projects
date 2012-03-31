package comm;

import java.io.IOException;
import java.util.LinkedList;

import run.SimulatedThread;

/**
 * Simulated communication channel using separate read/write buffers.
 */
public final class SimulatedChannel implements Channel
{
    /** Buffer type. */
    public static class Buffer extends LinkedList<Byte>
    { private static final long serialVersionUID = 1L; }
    
    //--------------------------------------------------------------------------
    
    public SimulatedChannel(Buffer buffR, Buffer buffW)
    {
        this.buffR = buffR;
        this.buffW = buffW;
        isClosed = false;
    }
    
    public void setThread(SimulatedThread thread)
    { this.thread = thread; }
    
    //--------------------------------------------------------------------------
    
    private int readByte0() throws IOException
    {
        synchronized (buffR)
        {
            if (buffR.isEmpty())
            {
                if (isClosed) throw new IOException(
                                    "Cannot read from a closed channel.");

                try
                {
                    thread.setEnabled(false);
                    buffR.wait();
                    thread.setEnabled(true);
                }
                catch (InterruptedException e) { throw new IOException(e); }
            }
            if (buffR.isEmpty()) return 0;
            return buffR.pollLast() & 0xFF;
        }
    }

    @Override
    public byte readByte() throws IOException
    {
        return (byte)readByte0();
    }
    
    @Override
    public short readShort() throws IOException
    {
        int v = readByte0();
        v = (v << 8) | readByte0();
        return (short)v;
    }
    
    @Override
    public int readInt() throws IOException
    {
        int v = readByte0();
        v = (v << 8) | readByte0();
        v = (v << 8) | readByte0();
        v = (v << 8) | readByte0();        
        return v;
    }
    
    @Override
    public long readLong() throws IOException
    {
        long v = readByte0();
        v = (v << 8) | readByte0();
        v = (v << 8) | readByte0();
        v = (v << 8) | readByte0();        
        v = (v << 8) | readByte0();
        v = (v << 8) | readByte0();
        v = (v << 8) | readByte0();        
        v = (v << 8) | readByte0();        
        return v;
    }
    
    @Override
    public float readFloat() throws IOException
    {
        int v = readInt();
        return Float.intBitsToFloat(v);
    }
    
    @Override
    public double readDouble() throws IOException
    {
        long v = readLong();
        return Double.longBitsToDouble(v);
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - --
    
    private void writeByte0(int v) throws IOException
    {
        synchronized (buffW)
        {
            if (isClosed) throw new IOException(
                                "Cannot write to a closed channel.");
        
            buffW.addFirst((byte)v);
            buffW.notifyAll();
        }
    }

    @Override
    public void writeByte(byte v) throws IOException
    {
        writeByte0(v);
    }
    
    @Override
    public void writeShort(short v) throws IOException
    {
        writeByte0(v >>> 8);
        writeByte0(v);
    }
    
    @Override
    public void writeInt(int v) throws IOException
    {
        writeByte0(v >>> 24);
        writeByte0(v >>> 16);
        writeByte0(v >>> 8);
        writeByte0(v);
    }
    
    @Override
    public void writeLong(long v) throws IOException
    {
        writeInt((int)(v >>> 32));
        writeInt((int)v);
    }
    
    @Override
    public void writeFloat(float v) throws IOException
    {
        writeInt(Float.floatToIntBits(v));
    }
    
    @Override
    public void writeDouble(double v) throws IOException
    {
        writeLong(Double.doubleToLongBits(v));
    }
    
    @Override
    public void flush() throws IOException
    {
    }

    //--------------------------------------------------------------------------
    
    @Override
    public void close()
    {
        isClosed = true;
        synchronized (buffR) { buffR.notifyAll(); }
        synchronized (buffW) { buffW.notifyAll(); }
    }
    
    //--------------------------------------------------------------------------

    private boolean isClosed;
    private SimulatedThread thread;
    private final Buffer buffR, buffW;
}
