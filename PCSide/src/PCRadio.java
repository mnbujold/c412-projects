/*
 * PCRadio - PC-side half-duplex transmit/recieve program for
 * initiation of IR test and receiving data arrays back from 
 * NXT-side program upon completion. It also packs the data
 * into a plain-text file and timestamps it using ms since
 * January 1, 1970, to prevent overwrites of existing data.
 * by Mike Bujold 
 */
import lejos.pc.comm.*;
import java.io.*;
import java.util.Date;

public class PCRadio {
	public static void main(String[] args){
		int dataPoints = 1800;
		long ts = new Date().getTime();
		String outputFileName = "ir_output" + ts + ".txt";
		NXTConnector conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){
			public void logEvent(String message){
				System.out.println("PCRadio Log.listener: " + message);
			}
			
			public void logEvent(Throwable throwable){
				System.out.println("PC Log.listener - stack trace: ");
				throwable.printStackTrace();
			}			
		} );
		
		boolean connected = conn.connectTo("btspp://");
		
		if(!connected){
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}
		
		int test = 0;
		DataOutputStream dos = conn.getDataOut();
//		DataOutputStream dos = (DataOutputStream) conn.getOutputStream();
		DataInputStream dis = conn.getDataIn();
//		DataInputStream dis = (DataInputStream) conn.getInputStream();
		
		while(test != -1){
			try{
				test = dis.readInt();							
			} catch(IOException ioe){
				System.out.println("IO exception reading bytes: ");
				System.out.println(ioe.getMessage());
			}
		}
		
		/* 
		 * When outside of loop, the test has finished...do stuff here now,
		 * such as read all the glorious data we have to provide!! 
		 * */
		int actualDist[] = new int[dataPoints];
		int irDist[] = new int[dataPoints];
		for(int i=0; i<dataPoints; i++){
			try{
				actualDist[i] = dis.readInt();
				irDist[i] = dis.readInt();
			} catch(IOException ioe){
				System.out.println("IO exception reading bytes: ");
				System.out.println(ioe.getMessage());
			}
		}
	
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outputFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<dataPoints;i++){
			System.out.println(actualDist[i] + " " + irDist[i]);
			out.println(actualDist[i] + " " + irDist[i]);
		}
		out.close();
		
		try{
			dis.close();
			dos.close();
			conn.close();
			
		} catch(IOException ioe){
			System.out.println("IOException closing connection: ");
			System.out.println(ioe.getMessage());
		}	
	}
}
