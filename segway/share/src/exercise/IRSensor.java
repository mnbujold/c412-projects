package exercise;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import model.scene.SceneModel;
import model.scene.SceneModelObject;
import model.sensor.DistanceSensor;
import model.sensor.DistanceSensorConfig;

/**
 * Model of an infrared sensor.
 * 
 * Use the rng() method to access the random number generator.
 */
public class IRSensor extends DistanceSensor
{
    public IRSensor(DistanceSensorConfig cfg, SceneModel scene)
    {
        super (cfg, scene);
        
        try{
        	  // Open the file that is the first 
        	  // command line parameter
        	  //FileInputStream fstream = new FileInputStream("model.txt");
        	  // Get the object of DataInputStream
        	  //DataInputStream in = new DataInputStream(fstream);
          	FileInputStream fstream = new FileInputStream("../share/cfg/model.txt");
          	DataInputStream in = new DataInputStream(fstream);
          	
//        	File aFile=new File("../share/cfg/model.txt");
          	BufferedReader br = new BufferedReader(new InputStreamReader(in));
          	String strLine;
          	String[] strs;
          	Double real;
          	Double mean;
          	Double var;
          	//Read File Line By Line
          	while ((strLine = br.readLine()) != null) {
          		strs = strLine.split(" ");
          		System.out.println(strLine);
          		if(strs.length==3){
          			 System.out.println(strs[0]+strs[1]+strs[2]);
          			 real = new Double(strs[0]);
          			 mean = new Double(strs[1]);
          			 var = new Double(strs[2]);
          			 //Object j =realtoMean.put(real, mean);
          			 //realtoMean.add(mean);
          			 //j=realtoVariance.put(real, var);
//            			 System.out.println("here2");
          		}
          	}
        	  //Close the input stream
          	in.close();
          }catch (Exception e){//Catch exception if any
          	System.err.println("Error: " + e.getMessage());
          }

    }
    
    @Override
    public double sample(double realDistance, SceneModelObject hitObj)
    {
        // TODO modify according to your IR sensor model
        return realDistance;
    }

    @Override
    public double pdf(double sampleDistance,
                      double realDistance,
                      SceneModelObject hitObj)
    {
        // TODO modify according to your IR sensor model
        return (sampleDistance == realDistance) ? 1.0 : 0.0;
    }
}
