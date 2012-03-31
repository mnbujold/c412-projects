package run;

import helper.Config;
import helper.MissingConfigException;

import java.io.File;
import java.io.IOException;

import linalg.Vector;


/**
 * General running configuration.
 */
public class RunConfig extends Config
{
    public RunConfig(File file)
    throws IOException, MissingConfigException
    {
        super (file);
        
        robotControllerClassName = getStringConfig("robot-controller");
        pcControllerClassName = getStringConfig("pc-controller");
        sceneMapFile = new File(getStringConfig("map-file"));
        
        String gyroSensorConfigPrefix =
            getStringConfig("gyro-sensor-config-prefix");
        gyroSensorConfig = new File(gyroSensorConfigPrefix + ".cfg");
        
        String distSensorConfigPrefix =
            getStringConfig("dist-sensor-config-prefix");
        Vector distSensorConfigIndices =
            getVectorConfig("dist-sensor-config-indices", Vector.create(0));
        distSensorConfig = new File[distSensorConfigIndices.length()];
        for (int i = 0; i < distSensorConfig.length; ++i)
            distSensorConfig[i] =
                new File(distSensorConfigPrefix + "-"
                         + (int)distSensorConfigIndices.get(i) + ".cfg");
    }
    
    //--------------------------------------------------------------------------
    
    /** @return class name of the robot controller */
    public String robotControllerClassName()
    { return robotControllerClassName; }
    
    /** @return class name of the pc controller */
    public String pcControllerClassName()
    { return pcControllerClassName; }
    
    /** @return scene map file */
    public File mapFile()
    { return sceneMapFile; }
    
    /** @return gyroscope sensor configuration file */
    public File gyroSensorConfig()
    { return gyroSensorConfig; }
    
    /** @return distance sensor configuration files */
    public File[] distSensorConfigs()
    { return distSensorConfig; }
    
    //--------------------------------------------------------------------------
    
    private final String robotControllerClassName;
    private final String pcControllerClassName;
    private final File sceneMapFile;
    
    private final File gyroSensorConfig;
    private final File[] distSensorConfig;
}
