package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import linalg.Vector;

/**
 * Represents a general configuration.
 */
public class Config
{
    public Config(File file)
    throws IOException
    {
        props = new Properties();
        FileInputStream fis = new FileInputStream(file);
        try { props.load(fis); } finally { fis.close(); }
    }

    //--------------------------------------------------------------------------
    
    /**
     * @param name of the queried configuration entry
     * @return string configuration value
     */
    protected String getStringConfig(String cfgName)
    throws MissingConfigException
    {
        String cfgValue = props.getProperty(cfgName);
        if (null == cfgValue) { throw new MissingConfigException(cfgName); }
        return cfgValue;
    }
    
    /**
     * @param name of the queried configuration entry
     * @param defaultValue default value to be used if entry is missing
     * @return string configuration value
     */
    protected String getStringConfig(String cfgName, String defaultValue)
    {
        String cfgValue = props.getProperty(cfgName);
        return (null != cfgValue) ? cfgValue : defaultValue;
    }
    
    /**
     * @param name of the queried configuration entry
     * @return integer configuration value
     */
    protected int getIntegerConfig(String cfgName)
    throws MissingConfigException
    {
        return Integer.valueOf(getStringConfig(cfgName));
    }
    
    /**
     * @param name of the queried configuration entry
     * @return long configuration value
     */
    protected long getLongConfig(String cfgName)
    throws MissingConfigException
    {
        return Long.valueOf(getStringConfig(cfgName));
    }
    
    /**
     * @param name of the queried configuration entry
     * @return float configuration value
     */
    protected float getFloatConfig(String cfgName)
    throws MissingConfigException
    {
        return Float.valueOf(getStringConfig(cfgName));
    }
    
    /**
     * @param name of the queried configuration entry
     * @return double configuration value
     */
    protected double getDoubleConfig(String cfgName)
    throws MissingConfigException
    {
        return Double.valueOf(getStringConfig(cfgName));
    }

    /**
     * @param name of the queried configuration entry
     * @param defaultValue default value to be used if entry is missing
     * @return double configuration value
     */
    protected double getDoubleConfig(String cfgName, double defaultValue)
    {
        String valueStr = getStringConfig(cfgName, "");
        return 0 < valueStr.length() ? Double.valueOf(valueStr) : defaultValue;
    }
    
    /**
     * @param name of the queried configuration entry
     * @return boolean configuration value
     */
    protected boolean getBooleanConfig(String cfgName)
    throws MissingConfigException
    {
        String s = getStringConfig(cfgName).toLowerCase();
        return (s.equals("1") || s.equals("on") || s.equals("yes") ||
                s.equals("true") || s.equals("enabled"))
               ? true : false;
    }
    
    /**
     * @param name of the queried configuration entry
     * @return vector configuration value
     */
    protected Vector getVectorConfig(String cfgName)
    throws MissingConfigException
    {
        String s = getStringConfig(cfgName);
        if (s.isEmpty()) return Vector.create(0);
        
        String[] ss = s.split(" ");
        Vector v = Vector.create(ss.length);
        for (int i = 0; i < v.length(); ++i) v.set(i, Double.valueOf(ss[i]));
        return v;
    }
    
    /**
     * @param name of the queried configuration entry
     * @param defaultValue default value to be used if entry is missing
     * @return vector configuration value
     */
    protected Vector getVectorConfig(String cfgName, Vector defaultValue)
    {
        try { return getVectorConfig(cfgName); }
        catch (MissingConfigException e) { return defaultValue; }
    }
    
    //--------------------------------------------------------------------------

    /**
     * Raw configuration properties.
     */
    private Properties props;
}
