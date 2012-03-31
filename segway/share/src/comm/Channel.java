package comm;

import java.io.IOException;

/**
 * Interface of a bidirectional communication channel
 * between the robot and the PC.
 */
public interface Channel
{
    byte readByte() throws IOException;
    short readShort() throws IOException;
    int readInt() throws IOException;
    long readLong() throws IOException;
    float readFloat() throws IOException;
    double readDouble() throws IOException;
    
    void writeByte(byte v) throws IOException;
    void writeShort(short v) throws IOException;
    void writeInt(int v) throws IOException;
    void writeLong(long v) throws IOException;
    void writeFloat(float v) throws IOException;
    void writeDouble(double v) throws IOException;
    void flush() throws IOException;
    
    void close();
}
