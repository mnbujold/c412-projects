import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;


public class Remote_Control {
	
	public static final int START_SIGNAL = 0;
	public static final int STOP_SIGNAL = -1;
	public static final int FORWARD_SIGNAL = 1;
	public static final int BACKWARD_SIGNAL = 2;
	public static final int LEFT_SIGNAL = 3;
	public static final int RIGHT_SIGNAL = 4;
	public static final int PAUSE_SIGNAL = 5;
	public static final int SUCCEED = 1;
	public static final int FAIL = 0;
	
	public static final int SPEED = 100;
	
	public static void main(String[] args){
		String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        LCD.drawString(waiting,0,0);
		LCD.refresh();

        BTConnection btc = Bluetooth.waitForConnection();
		DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
		
//		SensorPort sp = SensorPort.getInstance(3);
		SensorPort sp = SensorPort.S4;
		GyroSensor gs = new GyroSensor(sp);
		MotorPort mp_left = MotorPort.C;
		NXTMotor motor_left = new NXTMotor(mp_left);
		MotorPort mp_right = MotorPort.B;
		NXTMotor motor_right = new NXTMotor(mp_right);
		boolean ifStop;
		int signal;
		int[][] y_array;
		while(true){
			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();
			try {
//				if(dis.readInt()== Remote_Control.START_SIGNAL){
				if(true){	
					dos.writeInt(SUCCEED);
					dos.flush();
					MySegway segway = new MySegway(motor_left, motor_right, gs, 5.6);
					ifStop = false;
					while(!ifStop){
						signal = dis.readInt();
						switch (signal){
							case Remote_Control.FORWARD_SIGNAL:
								segway.wheelDriver(SPEED, SPEED);
								dos.writeInt(SUCCEED);
								dos.flush();
								break;
							case Remote_Control.BACKWARD_SIGNAL:
								segway.wheelDriver(-SPEED, -SPEED);
								dos.writeInt(SUCCEED);
								dos.flush();
								break;
							case Remote_Control.LEFT_SIGNAL:
								segway.wheelDriver(-SPEED, SPEED);
								dos.writeInt(SUCCEED);
								dos.flush();
								break;
							case Remote_Control.RIGHT_SIGNAL:
								segway.wheelDriver(SPEED, -SPEED);
								dos.writeInt(SUCCEED);
								dos.flush();
								break;
							case Remote_Control.PAUSE_SIGNAL:
								segway.wheelDriver(0, 0);
								dos.writeInt(SUCCEED);
								dos.flush();
								break;
							case Remote_Control.STOP_SIGNAL:
								dos.writeInt(SUCCEED);
								dos.flush();
//								y_array = segway.y_array;
//								dos.writeDouble(segway.gOffset);
//								for(int i=0;i<segway.ARRAY_LENGTH;i++){
//									dos.writeInt(y_array[0][i]);
//								}
//								for(int i=0;i<segway.ARRAY_LENGTH;i++){
//									dos.writeInt(y_array[1][i]);
//								}
//								dos.flush();
								segway.stop();
								ifStop = true;
								break;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
