import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class KeyControl extends Frame implements KeyListener {
	TextField t1;
	Label l1;
	public static final int START_SIGNAL = 0;
	public static final int STOP_SIGNAL = -1;
	public static final int FORWARD_SIGNAL = 1;
	public static final int BACKWARD_SIGNAL = 2;
	public static final int LEFT_SIGNAL = 3;
	public static final int RIGHT_SIGNAL = 4;
	public static final int PAUSE_SIGNAL = 5;
	public static final int SUCCEED = 1;
	public static final int FAIL = 0;
	private static final int interval = 5;
	public static final int ARRAY_LENGTH = 30*1000/interval;
	DataOutputStream dos ;
	DataInputStream dis;
	
	
	public KeyControl(String s) {
		super(s);
		
		JPanel p = new JPanel(new FlowLayout());
		
		JPanel left = new JPanel(new FlowLayout());
		
		JButton upButton = new JButton("^");
		JButton dnButton = new JButton("V");
		JButton ltButton = new JButton("<");
		JButton rtButton = new JButton(">");
		
		left.add(upButton);
		left.add(dnButton);
		left.add(ltButton);
		left.add(rtButton);
		
		p.add(left);
		
		
		l1 = new Label("Key Listener!");
		l1.setBounds(0, 0, 300, 200);
//		p.add(l1);
		
		JButton start_button = new JButton("START");
		start_button.setBounds(300, 0, 100, 100);
		start_button.setFocusable(false);
		start_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				sendAction(START_SIGNAL);	
			}
			
		});
		
		JButton stop_button = new JButton("STOP");
		stop_button.setFocusable(false);
		stop_button.setBounds(300, 100, 100, 100);
		stop_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				sendAction(STOP_SIGNAL);
//				System.out.println("----------GyroOffset-------------");
//				try {
//					System.out.println(dis.readDouble());
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				System.out.println("----------TimeIndex-------------");
//				for(int i=0;i<ARRAY_LENGTH;i++){
//					try {
//						System.out.println(dis.readInt());
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				System.out.println("----------GyroOutput-------------");
//				for(int i=0;i<ARRAY_LENGTH;i++){
//					try {
//						System.out.println(dis.readInt());
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
			}
		});
		
//		p.add(start_button);
//		p.add(stop_button);

		add(p);
		
		addKeyListener(this);
		setSize(400, 200);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
/* Commented out connection part...for testing GUI...
		NXTConnector conn = new NXTConnector();

		conn.addLogListener(new NXTCommLogListener() {
			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: " + message);
			}
			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				throwable.printStackTrace();
			}
		});
		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://");
		dos = conn.getDataOut();
		dis = conn.getDataIn();
		
		try {
			while(true){
			dos.writeInt(START_SIGNAL);
			dos.flush();
			if(dis.readInt() == SUCCEED)
				return;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
*/

	}

	public void keyTyped(KeyEvent e) {
//		l1.setText("Key Typed");

//		l1.setText("" + e.getKeyCode());
	}

	public void keyPressed(KeyEvent e) {
		// l1.setText ( "Key Pressed" ) ;
//		l1.setText("" + e.getKeyCode());
		int action = PAUSE_SIGNAL;
		switch(e.getKeyCode()){
		case 37:
			action = LEFT_SIGNAL;
			break;
		case 38:
			action = FORWARD_SIGNAL;
			break;
		case 39:
			action = RIGHT_SIGNAL;
			break;
		case 40:
			action = BACKWARD_SIGNAL;
			break;
		}
		l1.setText(sendAction(action));
	}

	public void keyReleased(KeyEvent e) {
//		l1.setText("Key Released");
		l1.setText(sendAction(PAUSE_SIGNAL));
	}
	
	private String sendAction(int action){
		try {
			dos.writeInt(action);
			dos.flush();
			if(dis.readInt()==SUCCEED){
				switch(action){
				case	FORWARD_SIGNAL:
					return "Going forward.";
				case	BACKWARD_SIGNAL:
					return "Going backward.";
				case	LEFT_SIGNAL:
					return "Turning left.";
				case RIGHT_SIGNAL:
					return "Turning right.";
				case PAUSE_SIGNAL:
					return "Keep standing.";
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		return "Failed";
	}

	public static void main(String[] args) {
		new KeyControl("Key Listener Tester");
	}
}