package model.sensor;

import java.io.File;
import java.io.IOException;

import helper.Config;
import helper.MissingConfigException;

/**
 * Configuration parameters of the rate gyroscope model.
 */
public class GyroSensorConfig extends Config
{
    public GyroSensorConfig(File cfgFile)
    throws IOException, MissingConfigException
    {
        super (cfgFile);
        
        c = getStringConfig("class");
        
        a1 = getDoubleConfig("a1");
        a2 = getDoubleConfig("a2");
        b1 = getDoubleConfig("b1");
        
        B = getDoubleConfig("B");
        std = getDoubleConfig("std");
    }
    
    //--------------------------------------------------------------------------

    /** @return gyroscope sensor class */
    public String sensorClass() { return c; }
    
    // bias parameter
    public final double B;
    
    // Gaussian noise standard deviation
    public final double std;
    
    // parameters of rate gyroscope dynamics
    public final double a1, a2, b1;
    
    //--------------------------------------------------------------------------
    
    private final String c; 
}
