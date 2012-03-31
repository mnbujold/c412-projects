import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.addon.keyboard.KeyEvent;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.util.Delay;

/**
 * Receiver base class of the NXJ bluetooth remote control (robot side).
 * One can control this by the <code>NXJBlueRC</code> program.
 */
public abstract class NXJBlueReceiver extends Thread
{
    /**
     * Maximum number of key codes to be received.
     */
    public static final int MAX_KEY_CODES = 4;
    
    //--------------------------------------------------------------------------
    
    public NXJBlueReceiver()
    {
        keyCodes = new int[MAX_KEY_CODES];
        setDaemon(true);
    }

    @Override
    public void run()
    {
        while (true)
        {
            BTConnection btc = Bluetooth.waitForConnection();
            Delay.msDelay(200);

            DataInputStream dis = btc.openDataInputStream();
            DataOutputStream dos = btc.openDataOutputStream();
            
            try
            {
                dos.writeInt(MAX_KEY_CODES);
                dos.flush();
                
                connected();
                recv: while (true)
                {
                    int num = dis.readInt();
                    if (0 < num)
                    {
                        for (int i = 0; i < num; ++i)
                        {
                            keyCodes[i] = dis.readInt();
                            if (KeyEvent.VK_UNDEFINED == keyCodes[i])
                            {
                                dis.close();
                                dos.close();
                                break recv;
                            }
                        }
                        handleKeyEvents(keyCodes, num);
                    }
                    else
                    {
                        handleKeyEvents(EMPTY, 0);
                    }
                }
            }
            catch (Exception e) {}
            finally
            {
                btc.close();
                try { disconnected(); } catch (Exception e) {}
            }
        }
    }

    /**
     * Handler of the key events.
     * The key codes are <code>KeyEvent.VK_...</code> values.
     * @param keyCodes <code>KeyEvent.VK_...</code> values
     * @param nKeyCodes number of received key codes
     *        in the <code>keyCodes</code> array
     */
    protected abstract void handleKeyEvents(int[] keyCodes, int nKeyCodes)
    throws Exception;

    /**
     * Called when the remote control is connected.
     */
    protected void connected() throws Exception
    {        
    }

    /**
     * Called when the remote control is disconnected.
     */
    protected void disconnected() throws Exception
    {
    }
    
    //--------------------------------------------------------------------------
    
    private int[] keyCodes;
    private final static int[] EMPTY = new int[]{};
}
