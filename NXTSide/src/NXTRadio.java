/*
 * NXTRadio - NXT-side half-duplex transmit/recieve program for
 * reading IR sensor data and sending data back to the PC-side
 * program upon completion.
 * by Mike Bujold
 */

import lejos.nxt.*;
//import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.*;
import java.io.*;

public class NXTRadio {
	
	public static void main(String [] args) throws Exception{
		int dataPoints = 1800;
		System.out.println("Starting Program...");
		String connected = "Connected";
		String waiting = "Waiting...";
		String closing = "Closing...";
		/* 
		 * Important Details for Testing... 
		 * */
		/* Equate 1 step == ?? mm IRL */
		/* Take 1 step, sample distance for X ms, repeat */
		/* Store in array: Dist from wall (totalDist), measuredDist,  */
		/* 1800 would equate to approx 360mm */
		int actualDist[] = new int[dataPoints];
		int irDist[] = new int[dataPoints];
				
		int realStartDist = 526; // Actual RL starting distance from wall (say, in mm)
		int realStepDist = 1; // Actual length of 1 step (in mm)
		int numValues = 5; // Number of values to read while in one spot

		// Steps to take when moving (size is ms, actually)
		int stepPwr = 20;
		int stepSize = 110;
		int stepDelay = 2000; // Time between steps

		int totalDist = 0;		// Dist travelled so far
		int measuredDist = 0; // From IR sensor
		
//		while(!Button.ESCAPE.isDown()){

		LCD.drawString(waiting, 0, 0);
		LCD.refresh();
		BTConnection btc = Bluetooth.waitForConnection();
		
		LCD.clear();
		LCD.drawString(connected,0,0);
		LCD.refresh();
			
		DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
			
			
		SensorPort sp = SensorPort.S2;
		OpticalDistanceSensor ir = new OpticalDistanceSensor(sp);
		
		LCD.drawString("IR Test v1.0",0,1);

		LCD.drawString("Start Dist: ", 0, 3);
		LCD.drawString("IR Value:   ", 0, 4);
		LCD.drawString("Step Size:  ", 0, 5);
		
		LCD.drawString("Index:      ", 0, 6);
//		LCD.drawString("Motor B:    ",0,6);
//		LCD.drawString("Motor C:    ", 0, 7);
		LCD.drawString("DO NOT TOUCH ME!", 0, 7);

		
		LCD.drawInt(realStartDist, 11, 3);
		LCD.drawInt(stepSize, 11, 5);
		LCD.refresh();
		
		/* Start your engines... */
		int index = 0;
		totalDist = realStartDist;
		
		/* Loop until 'ESCAPE' is pressed... */
		while(!Button.ESCAPE.isPressed() && index < dataPoints){
			
			measuredDist = ir.getDistance();
			LCD.drawInt(measuredDist, 11, 4);
			LCD.drawInt(index, 11, 6);
//			LCD.drawInt(Motor.B.getTachoCount(), 11, 6);
//			LCD.drawInt(Motor.C.getTachoCount(), 11, 7);
			
			LCD.refresh();
			// Send data to PC side
			dos.writeInt(measuredDist);
			dos.flush();
			
			Thread.sleep(stepDelay); // 2 sec??
			
			/* 
			 * Sent back a series of IR values for one static point
			 * */
			
			for(int j=0; j<numValues; j++){
				actualDist[index] = totalDist;
				irDist[index] = measuredDist;
				index++;
			}
		
			MotorPort.C.setPWMMode(BasicMotorPort.BACKWARD);
			MotorPort.B.setPWMMode(BasicMotorPort.BACKWARD);
			MotorPort.C.controlMotor(stepPwr, BasicMotorPort.BACKWARD);
			MotorPort.B.controlMotor(stepPwr, BasicMotorPort.BACKWARD);
			Thread.sleep(stepSize);
			MotorPort.B.controlMotor(0, BasicMotorPort.STOP);
			MotorPort.C.controlMotor(0, BasicMotorPort.STOP);
			/* Increment Step */
			totalDist += realStepDist; 
			
		}
		/*
		 * Send -1 to PC to terminate read 
		 * */
		dos.writeInt(-1);
		dos.flush();
		
		/* 
		 * Send back arrays full of bountiful data!
		 * */
		for(int i=0; i<dataPoints; i++){
			dos.writeInt(actualDist[i]);
			dos.flush();
			dos.writeInt(irDist[i]);
			dos.flush();
		}
		
		
		/* 
		 * 'Home' robot...
		 * */
		while(Motor.B.getTachoCount() < 0 && Motor.C.getTachoCount() < 0){
			MotorPort.C.setPWMMode(BasicMotorPort.FORWARD);
			MotorPort.B.setPWMMode(BasicMotorPort.FORWARD);
			MotorPort.C.controlMotor(stepPwr*2, BasicMotorPort.FORWARD);
			MotorPort.B.controlMotor(stepPwr*2, BasicMotorPort.FORWARD);
			
		}
		// Shutdown connection
		dis.close();
		dos.close();
		Thread.sleep(100); // Wait for data to drain
		LCD.clear();
		LCD.drawString(closing,0,0);
		LCD.refresh();
		btc.close();
		LCD.clear();
		
	}

}
