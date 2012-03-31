import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import lejos.pc.comm.NXTConnector;
import lejos.util.Delay;


/**
 * Bluetooth remote control for NXJ robots.
 * @author Gabor Balazs (gbalazs@ualberta.ca)
 */
public class NXJBlueRC extends JFrame
{
    public static void main(String[] args)
    {
        NXJBlueRC rc = new NXJBlueRC();
        rc.run();
        System.exit(0);
    }

    //--------------------------------------------------------------------------

    public NXJBlueRC()
    {
        maxCodes = 2;
        try
        {
            btcFocusImg = ImageIO.read(new File("img/btc_focus.png"));
            btcNoFocusImg = ImageIO.read(new File("img/btc_nofocus.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        img = btcNoFocusImg;
        
        delay = 100;
        conn = null;
        pressedKeys = new HashSet<Integer>();
        
        setTitle("NXJ Bluetooth Remote Control");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        imgPanel = new ImgPanel();
        setContentPane(imgPanel);
        pack();
        setLocationRelativeTo(null); // center application on screen
        setVisible(true);
        
        addWindowListener(new RCWindowAdapter());
        KeyListener keyListener = new RCKeyListener();
        addKeyListener(keyListener);
        imgPanel.addKeyListener(keyListener);
    }

    public boolean isConnected()
    {
        return (null != conn);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                connect();            
                while (isConnected())
                {
                    sendKeys();
                    Delay.msDelay(delay);
                }
            }
            catch (IOException e) { Delay.msDelay(2000); }
        }
    }

    //--------------------------------------------------------------------------

    private void connect() throws IOException
    {
        String bthName = JOptionPane.showInputDialog(this,
                                                     "Name of NXJ robot : ",
                                                     "unknown");
        if (null == bthName)
        {
            System.exit(0); // canceled
        }
        
        conn = new NXTConnector();
        if (!conn.connectTo("btspp://" + bthName))
        {
            showError("Connection failed!");
            System.exit(-1);
        }
        Delay.msDelay(50);
        dis = conn.getDataIn();
        dos = conn.getDataOut();
        maxCodes = dis.readInt();
        
        img = btcFocusImg;
        imgPanel.repaint();        
    }

    private synchronized void disconnect()
    {
        img = btcNoFocusImg;
        imgPanel.repaint();
        if (!isConnected()) { return; }
        
        try
        {
            dos.writeInt(KeyEvent.VK_UNDEFINED); // sending shutdown event
            dos.flush();
        }
        catch (Exception e) {}
        
        try { dis.close(); } catch (Exception e) {}
        try { dos.close(); } catch (Exception e) {}
        try { conn.close(); } catch (Exception e) {}
        conn = null;
    }

    private synchronized void sendKeys()
    {
        try
        {
            int size = pressedKeys.size();
            if (size > maxCodes) size = maxCodes;
            
            dos.writeInt(size);
            for (Integer key : pressedKeys)
            {
                dos.writeInt(key);
                if (--size == 0) break;
            }
            dos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            disconnect();
        }
    }

    private synchronized void addKey(KeyEvent e)
    {
        pressedKeys.add(e.getKeyCode());
    }

    private synchronized void removeKey(KeyEvent e)
    {
        pressedKeys.remove(e.getKeyCode());
    }

    //--------------------------------------------------------------------------

    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this,
                                      message,
                                      "Error!",
                                      JOptionPane.ERROR_MESSAGE);        
    }

    private class ImgPanel extends JPanel
    {
        public ImgPanel()
        {
            setPreferredSize(new Dimension(300, 300));
        }
        
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            int x = (getWidth() - img.getWidth()) / 2;
            int y = (getHeight() - img.getHeight()) / 2;
            g.drawImage(img, x, y, this);
        }
        
        private static final long serialVersionUID = 1L;
    }

    //--------------------------------------------------------------------------

    private class RCKeyListener implements KeyListener
    {
        @Override
        public void keyPressed(KeyEvent e) { addKey(e); }
        
        @Override
        public void keyReleased(KeyEvent e) { removeKey(e); }
        
        @Override
        public void keyTyped(KeyEvent e) {}
    }

    //--------------------------------------------------------------------------

    private class RCWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            disconnect();
            System.exit(0);
        }
        
        @Override
        public void windowActivated(WindowEvent e)
        {
            super.windowActivated(e);
            img = isConnected() ? btcFocusImg : btcNoFocusImg;
            imgPanel.repaint();
        }

        @Override
        public void windowDeactivated(WindowEvent e)
        {
            super.windowDeactivated(e);
            img = btcNoFocusImg;
            imgPanel.repaint();
        }
    }

    //--------------------------------------------------------------------------

    private int delay; // transmission delay (milliseconds)
    private int maxCodes; // maximum number of key codes to be sent

    private NXTConnector conn;
    private DataInputStream dis;
    private DataOutputStream dos;

    private JPanel imgPanel;
    private BufferedImage img;
    private BufferedImage btcFocusImg;
    private BufferedImage btcNoFocusImg;

    private HashSet<Integer> pressedKeys;
    private static final long serialVersionUID = 1L;
}
