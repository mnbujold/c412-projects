package comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTConnector;

/**
 * NXT communication channel (PC side).
 */
public class NXTPCChannel implements Channel
{
    public NXTPCChannel(NXTConnector conn)
    {
        dis = conn.getDataIn();
        dos = conn.getDataOut();
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public byte readByte() throws IOException
    { return dis.readByte(); }
    
    @Override
    public short readShort() throws IOException
    { return dis.readShort(); }
    
    @Override
    public int readInt() throws IOException
    { return dis.readInt(); }

    @Override
    public long readLong() throws IOException
    { return dis.readLong(); }
    
    @Override
    public float readFloat() throws IOException
    { return dis.readFloat(); }
    
    @Override
    public double readDouble() throws IOException
    { return dis.readDouble(); }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void writeByte(byte v) throws IOException
    { dos.writeByte(v); }
    
    @Override
    public void writeShort(short v) throws IOException
    { dos.writeShort(v); }
    
    @Override
    public void writeInt(int v) throws IOException
    { dos.writeInt(v); }
    
    @Override
    public void writeLong(long v) throws IOException
    { dos.writeLong(v); }
    
    @Override
    public void writeFloat(float v) throws IOException
    { dos.writeFloat(v); }
    
    @Override
    public void writeDouble(double v) throws IOException
    { dos.writeDouble(v); }
    
    @Override
    public void flush() throws IOException
    { dos.flush(); }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void close()
    {
        try { if (dos != null) dos.close(); } catch (IOException e) {}
        dos = null;
        
        try { if (dis != null) dis.close(); } catch (IOException e) {}
        dis = null;
    }
    
    //--------------------------------------------------------------------------
    
    private DataInputStream dis;
    private DataOutputStream dos;
}
