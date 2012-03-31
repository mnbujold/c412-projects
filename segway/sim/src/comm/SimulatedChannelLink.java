package comm;


/**
 * Simulated communication channel.
 */
public final class SimulatedChannelLink
{
    public SimulatedChannelLink()
    {
        SimulatedChannel.Buffer buffA = new SimulatedChannel.Buffer();
        SimulatedChannel.Buffer buffB = new SimulatedChannel.Buffer();
        
        channelA = new SimulatedChannel(buffA, buffB);
        channelB = new SimulatedChannel(buffB, buffA);
    }
    
    public SimulatedChannel channelA() { return channelA; }
    public SimulatedChannel channelB() { return channelB; }
    
    //--------------------------------------------------------------------------
    
    private final SimulatedChannel channelA, channelB;
}
