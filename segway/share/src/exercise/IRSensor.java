package exercise;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

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
          	FileInputStream fstream = new FileInputStream("../share/cfg/model.txt");
          	DataInputStream in = new DataInputStream(fstream);
          	realtoMean=new LinkedList<Double>();
          	realtoVar=new LinkedList<Double>();
          	BufferedReader br = new BufferedReader(new InputStreamReader(in));
          	String strLine;
          	String[] strs;
          	Double real;
          	Double mean;
          	Double var;
          	//Read File Line By Line
          	while ((strLine = br.readLine()) != null) {
          		strs = strLine.split(" ");
          		//System.out.println(strLine);
          		if(strs.length==3){
          			 //System.out.println(strs[0]+strs[1]+strs[2]);
          			 real = new Double(strs[0]);
          			 mean = new Double(strs[1]);
          			 var = new Double(strs[2]);
          			 //Object j =realtoMean.put(real, mean);
          			 realtoMean.add(mean);
          			 realtoVar.add(var);
          			 //j=realtoVariance.put(real, var);
//            			 System.out.println("here2");
          		}
          	}
          	in.close();
          }catch (Exception e){//Catch exception if any
          	System.err.println("Error: " + e.getMessage());
          }

    }

    @Override
    public double sample(double realDistance, SceneModelObject hitObj)
    {
    	int rounded = (new Double(realDistance)).intValue();
    	if (rounded>800)
    		rounded=800;
    	if (rounded<1)
    		rounded=1;
    	double sampleDist =(realtoMean.get(rounded-1).doubleValue())
    		+ rng().nextGaussian()*(realtoVar.get(rounded-1).doubleValue());
        // TODO modify according to your IR sensor model
    	if(sampleDist>800)
    		sampleDist=800;
    	if(sampleDist<1)
    		sampleDist=1;
    	//System.out.println(sampleDist+"\t"+realDistance + "\t"+rounded);
        return sampleDist;
    }

    @Override
    public double pdf(double sampleDistance,
                      double realDistance,
                      SceneModelObject hitObj)
    {
    	int rounded = (new Double(realDistance)).intValue();
    	if (rounded>800)
    		rounded=800;
    	if (rounded<1)
    		rounded=1;
    	double expected= realtoMean.get(rounded-1).doubleValue();
    	double varExpected=realtoVar.get(rounded-1).doubleValue();
    	double std_dev=Math.sqrt(varExpected);
    	if(std_dev>0){
    		double p=Gaussian.phi(sampleDistance, expected, std_dev);
    		//System.out.println(p+"\t :"+sampleDistance+" "+realDistance);
    		return p;
    	}
    	//if(sampleDistance==expected)
    		//System.out.println(sampleDistance+" "+realDistance);
    	return (sampleDistance == expected) ? 1.0 : 0.0;
        //return (sampleDistance == realDistance) ? 1.0 : 0.0;
    }
    private LinkedList<Double> realtoMean;
    private LinkedList<Double> realtoVar;

}
