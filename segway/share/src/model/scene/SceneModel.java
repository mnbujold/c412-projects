package model.scene;

import geom3d.HalfLine;
import geom3d.Point3D;
import helper.Ratio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import localize.ParticleFilter;
import model.motion.MotionConfig;
import model.motion.State;
import model.sensor.DistanceSensorConfig;

/**
 * Geometric model of the scene.
 * 
 * Orientation:   +z|  /+y
 *                  | /
 *                  |/
 *                  o------
 *                       +x
 * 
 * Descartes coordinate system
 * x, y, z labels according to right-hand rule
 * 
 * The bottom left corner of the floor is placed at the origin.
 */
public class SceneModel
{
    public SceneModel(MotionConfig motionModelConfig,
                      File mapFile,
                      DistanceSensorConfig[] distCfg)
    throws IOException
    {
        assert (mapFile != null);
        this.distCfg = distCfg;
        
        startTime = -1;
        objects = new LinkedList<SceneModelObject>();
        objectsToHit = new LinkedList<SceneModelObject>();
        boxes = new LinkedList<Box>();
        fixedPoints = new LinkedList<FixedPoint>();
        selectedPoint = new DynamicPoint(this);
        
        background = new Color(75, 155, 200);
        load(mapFile);
        
        segway = new Segway(motionModelConfig.R * Ratio.M_TO_MM,
                            motionModelConfig.w * Ratio.M_TO_MM,
                            motionModelConfig.W * Ratio.M_TO_MM,
                            motionModelConfig.H * Ratio.M_TO_MM,
                            motionModelConfig.D * Ratio.M_TO_MM);
        leftWheel = new Wheel(segway, +1);
        rightWheel = new Wheel(segway, -1);

        add(floor);
        add(walls);
        if (carpet != null) add(carpet);
        for (Box box : boxes) add(box);
        for (FixedPoint p : fixedPoints) add(p);
        add(selectedPoint);
        add(segway);
        add(leftWheel);
        add(rightWheel);
        
        laserBeam = new LaserBeam[distCfg.length];
        laserBeamHitPoint = new LaserBeamHitPoint[distCfg.length];
        for (int i = 0; i < distCfg.length; ++i)
        {
            laserBeam[i] = new LaserBeam(distCfg[i], segway, this);
            add(laserBeam[i]);
            
            laserBeamHitPoint[i] = new LaserBeamHitPoint(laserBeam[i]);
            add(laserBeamHitPoint[i]);
        }
        particleCloud = null;
    }

    public List<SceneModelObject> objects() { return objects; }
    private void add(SceneModelObject obj)
    {
        objects.addLast(obj);
        if (obj.canBeHit()) objectsToHit.addLast(obj);
    }

    /** @return distance sensor configurations */
    public DistanceSensorConfig[] distCfg() { return distCfg; }
    
    /** @return elapsed time since the first update (sec) */
    public double elapsedTime() { return elapsedTime; }
    
    public Color background() { return background; }
    public Floor floor() { return floor; }
    public Carpet carpet() { return carpet; }
    public List<Box> boxes() { return boxes; }
    public List<FixedPoint> fixedPoints() { return fixedPoints; }
    public DynamicPoint selectedPoint() { return selectedPoint; }
    public Segway segway() { return segway; }
    public Wheel leftWheel() { return leftWheel; }
    public Wheel rightWheel() { return rightWheel; }
    public LaserBeam[] laserBeam() { return laserBeam; }

    public void addParticleFilter(ParticleFilter pf)
    {
        assert (particleCloud == null);
        particleCloud = new ParticleCloud(pf);
        add(particleCloud);
    }
    public ParticleCloud particleCloud() { return particleCloud; }
    
    public Drawer createDrawer()
    {
        Drawer d = new Drawer(this, carpet().height() + 0.5);
        add(d);
        return d;
    }
    
    //--------------------------------------------------------------------------
    
    /** Set the state of the scene model from the provided motion state. */
    public void update(State state)
    {
        double pitch = state.pitch();
        update(state.time(),
               state.x() * Ratio.M_TO_MM,
               state.y() * Ratio.M_TO_MM,
               pitch,
               state.yaw(),
               state.leftRoll() - pitch,
               state.rightRoll() - pitch);
    }
    
    /**
     * Set the state of the scene model.
     * @param time time (sec)
     * @param x x position of the robot (mm)
     * @param y y position of the robot (mm)
     * @param pitch robot body pitch angle (rad)
     * @param yaw robot body yaw angle (rad)
     * @param lRoll left wheel rotation counter (rad)
     * @param rRoll right wheel rotation counter (rad)
     */
    public synchronized void update(double time,
                                    double x, double y,
                                    double pitch, double yaw,
                                    double lRoll, double rRoll)
    {
        if (startTime < 0) startTime = time;
        elapsedTime = time - startTime;
        
        Point3D position = segway.position();
        position.setX(x);
        position.setY(y);
        position.setZ(isOnCarpet(x,y) ? carpet.height() : 0);
        segway.update(position, pitch, yaw);
        leftWheel.update(lRoll + pitch);
        rightWheel.update(rRoll + pitch);
        
        for (int i = 0; i < laserBeam.length; ++i)
        {
            laserBeam[i].update();
            laserBeamHitPoint[i].update();
        }
    }
    
    public boolean isOnFloor(double x, double y)
    {
        return 0.0 <= x && x <= floor.width() &&
               0.0 <= y && y <= floor.height();
    }
    
    public boolean isOnCarpet(double x, double y)
    {
        return carpet != null &&
               carpet.xMin() <= x && x <= carpet.xMax() &&
               carpet.yMin() <= y && y <= carpet.yMax();
    }
    
    //--------------------------------------------------------------------------
    
    /**
     * Representation of a distance measurement result.
     */
    public static final class DistanceResult
    {
        public DistanceResult()
        {
            hitPoint = new Point3D();
            hitPointTmp = new Point3D();
            hlineTmp = new HalfLine(Point3D.origin(), Point3D.unitX());
        }
        
        /** @return traveled ray distance (mm) */
        public double distance() { return distance; }
        
        /** @return hit point in the scene (mm), (valid when isHit() is true) */
        public Point3D hitPoint() { return hitPoint; }
        
        /** @return hit object in the scene (null if nothing is hit) */
        public SceneModelObject hitObject() { return hitObject; }
        
        /** @return true if the ray hits an object in the scene */
        public boolean isHit() { return (hitObject != null); }
        
        public void set(double distance,
                        Point3D hitPoint,
                        SceneModelObject hitObject)
        {
            this.distance = distance;
            this.hitObject = hitObject;
            if (hitPoint != this.hitPoint && isHit())
                hitPoint.copy(this.hitPoint);
        }
        
        private double distance;
        private final Point3D hitPoint;
        private SceneModelObject hitObject;
        
        // cache objects for calculations (internal use only)
        private final Point3D hitPointTmp;
        private final HalfLine hlineTmp;
    }
    
    /**
     * Calculate the ray hit point and return the traveled ray distance
     * of a distance sensor ray in the scene given the location and the
     * orientation of the segway.
     * @param distCfg distance sensor configuration
     * @param pos (x,y,z) position of the axle midpoint of the segway (mm)
     * @param pitch pitch angle of the segway (rad)
     * @param yaw yaw angle of the segway (rad)
     * @param result distance measurement result
     * @return "result"
     */
    public DistanceResult realDistance(DistanceSensorConfig distCfg,
                                       Point3D pos, double pitch, double yaw,
                                       DistanceResult result)
    {
        final double maxValue = distCfg.maxValue();
        final HalfLine hlineTmp = result.hlineTmp;
        final Point3D hitPointTmp = result.hitPointTmp;
        
        double distance = maxValue;
        Point3D hitPoint = result.hitPoint();
        SceneModelObject hitObject = null;
        
        distCfg.position().copy(hlineTmp.p());
        distCfg.axis().copy(hlineTmp.u());
        hlineTmp.rotateY(pitch).rotateZ(yaw);
        hlineTmp.translate(pos);
            
        double d;
        for (SceneModelObject obj : objectsToHit)
        {
            d = Math.min(maxValue, obj.hitAt(hlineTmp, hitPointTmp));
            if (d < distance && d > 0)
            {
                distance = d;
                hitObject = obj;
                hitPointTmp.copy(hitPoint);
            }
        }
        result.set(distance, hitPoint, hitObject);
        return result;
    }
    
    //--------------------------------------------------------------------------
    
    private final static String DELIM = " ";
    private final static String COMMENT = "#";
    
    /** Load the scene from a file. */
    public void load(File mapFile) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(mapFile));
        try
        {
            String line;
            while (null != (line = br.readLine()))
            {
                if (line.isEmpty()) continue;
                String[] tokens = line.split(DELIM);
                
                String type = tokens[0];
                if (type.startsWith(COMMENT)) continue;
                
                if (type.equalsIgnoreCase("table"))
                {
                    double sizeX = distance(tokens[1]);
                    double sizeY = distance(tokens[2]);
                    double height = distance(tokens[3]);
                    double thickness = distance(tokens[4]);
                    
                    floor = new Floor(sizeX, sizeY, thickness);
                    walls = new Walls(floor, height, thickness);
                }
                else if (type.equalsIgnoreCase("carpet"))
                {
                    double x = distance(tokens[1]);
                    double y = distance(tokens[2]);
                    double sizeX = distance(tokens[3]);
                    double sizeY = distance(tokens[4]);
                    double height = distance(tokens[5]);
                    
                    carpet = new Carpet(x, y, sizeX, sizeY, height);
                }
                else if (type.equalsIgnoreCase("box"))
                {
                    double x = distance(tokens[1]);
                    double y = distance(tokens[2]);
                    double sizeX = distance(tokens[3]);
                    double sizeY = distance(tokens[4]);
                    double height = distance(tokens[5]);
                    double elevation = distance(tokens[6]);
                    double pitch = angle(tokens[7]);
                    double yaw = angle(tokens[8]);
                    
                    boxes.add(new Box(new Point3D(x, y, elevation),
                                      sizeX, sizeY, height,
                                      pitch, yaw));
                }
                else if (type.equalsIgnoreCase("point"))
                {
                    double x = distance(tokens[1]);
                    double y = distance(tokens[2]);
                    double elevation = distance(tokens[3]);
                    
                    fixedPoints.add(new FixedPoint(new Point3D(x, y, elevation)));
                }
                else { System.err.println("Unknown type: " + type + "!"); }
            }
        }
        finally { br.close(); }
    }
    
    private double distance(String s)
    { return Double.valueOf(s); }
    
    private double angle(String s)
    { return Double.valueOf(s) * Ratio.DEG_TO_RAD; }
    
    //--------------------------------------------------------------------------
    
    private Color background;
    private Floor floor;
    private Walls walls;
    private Carpet carpet;
    private LinkedList<Box> boxes;
    private LinkedList<FixedPoint> fixedPoints;
    private DynamicPoint selectedPoint;
    
    private Segway segway;
    private Wheel leftWheel, rightWheel;
    
    private LaserBeam[] laserBeam;
    private LaserBeamHitPoint[] laserBeamHitPoint;
    private final DistanceSensorConfig[] distCfg;
    
    private ParticleCloud particleCloud;
    
    private double startTime, elapsedTime;
    private final LinkedList<SceneModelObject> objects;
    private final LinkedList<SceneModelObject> objectsToHit;
}
