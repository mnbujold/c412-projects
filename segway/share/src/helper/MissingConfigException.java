package helper;

/**
 * Thrown when a necessary (not having default value)
 * configuration entry is missing. 
 */
public class MissingConfigException extends Exception
{
    public MissingConfigException(String configName)
    {
        super("Missing config: " + configName + "!");
        this.configName = configName;
    }
    
    public String getConfigName()
    {
        return configName;
    }
    
    //--------------------------------------------------------------------------
    
    private String configName;
    private static final long serialVersionUID = 1L;
}
