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

/*
 * Balance.java - Simply to test if the robot can stand on it's own two feet...
 */
public class Balance {
		
	public static void main(String[] args){
        LCD.drawString("Waiting...",0,0);
		LCD.refresh();
		
//		SensorPort sp = SensorPort.getInstance(3);
		SensorPort sp = SensorPort.S4;
		GyroSensor gs = new GyroSensor(sp);
		MotorPort mp_left = MotorPort.C;
		NXTMotor motor_left = new NXTMotor(mp_left);
		MotorPort mp_right = MotorPort.B;
		NXTMotor motor_right = new NXTMotor(mp_right);

		MySegway segway = new MySegway(motor_left, motor_right, gs, 5.6);
				
	}
}
	
