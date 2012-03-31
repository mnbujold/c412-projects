package visual;

import helper.Config;
import helper.MissingConfigException;

import java.io.File;
import java.io.IOException;

import linalg.Vector;

/**
 * View configuration.
 */
public final class ViewConfig extends Config
{
    public enum CameraLookAt {FREE, ROBOT, TABLE_CENTER}
    
    //--------------------------------------------------------------------------
    
    public ViewConfig(File file)
    throws IOException, MissingConfigException
    {
        super (file);
        
        String s = getStringConfig("camera-look-at").toUpperCase();
        camLookAt = CameraLookAt.valueOf(s);

        camX = camY = camZ = 0;
        
        Vector camRotation = getVectorConfig("camera-rotation");
        camRotPitch = camRotation.get(0);
        camRotYaw = camRotation.get(1);
        camRotDist = camRotation.get(2);
        
        Vector camChgRates = getVectorConfig("camera-chg-rates");
        camChgX = camChgRates.get(0);
        camChgY = camChgRates.get(1);
        camChgZ = camChgRates.get(2);
        camChgPitch = camChgRates.get(3);
        camChgYaw = camChgRates.get(4);
        camChgDist = camChgRates.get(5);
        
        fullscreen = getBooleanConfig("fullscreen");
    }
    
    public CameraLookAt cameraLookAt() { return camLookAt; }

    public double cameraX() { return camX; }
    public double cameraY() { return camY; }
    public double cameraZ() { return camZ; }
    
    public void setCameraX(double x) { camX = x; }
    public void setCameraY(double y) { camY = y; }
    public void setCameraZ(double z) { camZ = z; }
    
    public double cameraPitch() { return camRotPitch; }
    public double cameraYaw() { return camRotYaw; }
    public double cameraDistance() { return camRotDist; }
    
    public void setCameraPitch(double pitch) { camRotPitch = pitch; }
    public void setCameraYaw(double yaw) { camRotYaw = yaw; }
    public void setCameraDistance(double d) { camRotDist = d; }
    
    public double cameraCghRateX() { return camChgX; }
    public double cameraCghRateY() { return camChgY; }
    public double cameraCghRateZ() { return camChgZ; }
    public double cameraChgRatePitch() { return camChgPitch; }
    public double cameraChgRateYaw() { return camChgYaw; }
    public double cameraChgRateDistance() { return camChgDist; }
    
    public boolean fullscreen() { return fullscreen; }
    
    //--------------------------------------------------------------------------
    
    private CameraLookAt camLookAt;
    private double camX, camY, camZ;
    private double camRotPitch, camRotYaw, camRotDist;
    private double camChgX, camChgY, camChgZ;
    private double camChgPitch, camChgYaw, camChgDist;
    private boolean fullscreen;
}
