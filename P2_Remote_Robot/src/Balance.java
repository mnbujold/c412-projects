import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/*
 * Balance.java - Simply to test if the robot can stand on it's own two feet...
 * A bit of added functionality has it wondering around based on input from 
 * the IR sensors...
 */
public class Balance {
		
	public static void main(String[] args) throws Exception{
		int SPEED = 80;
        LCD.drawString("Waiting...",0,0);
		LCD.refresh();
		
		SensorPort sp1 = SensorPort.S1;
		OpticalDistanceSensor ir1 = new OpticalDistanceSensor(sp1);
		SensorPort sp2 = SensorPort.S2;
		OpticalDistanceSensor ir2 = new OpticalDistanceSensor(sp2);
		SensorPort sp3 = SensorPort.S3;
		OpticalDistanceSensor ir3 = new OpticalDistanceSensor(sp3);
		
//		SensorPort sp = SensorPort.getInstance(3);
		SensorPort sp = SensorPort.S4;
		GyroSensor gs = new GyroSensor(sp);
		MotorPort mp_left = MotorPort.C;
		NXTMotor motor_left = new NXTMotor(mp_left);
		MotorPort mp_right = MotorPort.B;
		NXTMotor motor_right = new NXTMotor(mp_right);

		MySegway segway = new MySegway(motor_left, motor_right, gs, 5.6);
		
		boolean ObstacleFront = false;
		boolean ObstacleLeft = false;
		boolean ObstacleRight = false;
		
		
		while(true){
			segway.wheelDriver(SPEED, SPEED);
			
			if (ir1.getDistance() < 100)
				ObstacleRight = true;
			else if (ir2.getDistance() < 100)
				ObstacleFront = true;
			else if (ir3.getDistance() < 100)
				ObstacleLeft = true;
			
			if (ObstacleFront == true){
				/* Reverse a bit */
				ObstacleFront = false;
				segway.wheelDriver(-SPEED, -SPEED);
				Thread.sleep(100);
				segway.wheelDriver(-SPEED, SPEED);
				Thread.sleep(100);
				segway.wheelDriver(0, 0);
							}
			if (ObstacleLeft == true){
				/* Turn right */
				ObstacleLeft = false;
				segway.wheelDriver(SPEED, -SPEED);
				Thread.sleep(100);
				segway.wheelDriver(0, 0);
			}
			if (ObstacleRight == true){
				/* Turn left */
				ObstacleRight = false;
				segway.wheelDriver(-SPEED, SPEED);
				Thread.sleep(100);
				segway.wheelDriver(0, 0);
			}
		}
		
	}
	
}
	
