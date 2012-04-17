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
          	Double mean;
          	Double var;
          	//Read File Line By Line
          	while ((strLine = br.readLine()) != null) {
          		strs = strLine.split(" ");
          		if(strs.length==3){
          			 mean = new Double(strs[1]);
          			 var = new Double(strs[2]);
          			 realtoMean.add(mean);
          			 realtoVar.add(var);
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
    		return p;
    	}
    	return (sampleDistance == expected) ? 1.0 : 0.0;
    }
    private LinkedList<Double> realtoMean;
    private LinkedList<Double> realtoVar;

}
