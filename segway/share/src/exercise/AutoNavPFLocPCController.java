package exercise;

import geom3d.HalfLine;
import geom3d.Point3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.MouseInputAdapter;

import localize.Particle;
import model.motion.MotionConfig;
import model.scene.Color;
import model.scene.Drawer;
import model.scene.SceneModel;
import run.PC;
import visual.View;

import comm.CommunicatorLogic;

import control.PCController;

/**
* An autonomously navigating controller
* based on a particle filtering localization.
*/
public class AutoNavPFLocPCController extends PCController
{
    /** Bluetooth name of the NXT robot. */
    public static final String ROBOT_NAME = "suzy";
    
    //--------------------------------------------------------------------------
    
    public AutoNavPFLocPCController(PC pc,
                                    MotionConfig motionCfg,
                                    SceneModel scene,
                                    View view) throws IOException
    {
        super (pc, motionCfg, scene, view);
        mouseListener = new MouseListenerImpl();
        commLogic = new CommunicatorLogicImpl();
        dist = null;
        pathing = false;
        arrived =false;
        tooClose=false;
        freaked=false;
        goToPoint=new Point3D(-1,-1,-1);
        
        pf = new ParticleFilterAlg(19, motionCfg, scene);
        scene.addParticleFilter(pf);
        
        LinkedList<Point3D> corners = getBoxCoord(false);
        LinkedList<Point3D> offsets = getBoxCoord(true);
        LinkedList<Point3D> waypoints = checkBoxes(corners, offsets, scene.boxes().size());
        //System.out.println(waypoints);
        
        this.wPoints = waypoints;
        //this.wPoints = offsets;
        drawer = scene.createDrawer();
        
        // You can disable particle cloud visualization by
        // scene.particleCloud().setEnabled(false);
        
        pather = new PathFinderAlg(scene, waypoints);
    }
    
    private LinkedList<Point3D> checkBoxes(LinkedList<Point3D> corners,
    		LinkedList<Point3D> offsets, int size) {
    	LinkedList<Point3D> waypoints=new LinkedList<Point3D>();
    	Point3D current;
    	boolean nearBox=false;
		for (int i=0; i<offsets.size(); i++){
			//for each offset
			current = offsets.get(i);
			nearBox=false;
			if (!scene().isOnFloor(current.x(), current.y()))
				continue;
			for (int b=0; b<size; b++){
				if(pnpoly(corners.subList(4*b, 4*(b+1)-1), current)){
					nearBox=true;
					break;
				}
			}
			//checks 8 directions for boxes and walls
			if(!nearBox){
				Point3D result=new Point3D();
				Point3D dir1=new Point3D(0, 1, 0);
				Point3D dir2=new Point3D(1, 0, 0);
				Point3D dir3=new Point3D(0, -1, 0);
				Point3D dir4=new Point3D(-1, 0, 0);
				Point3D dir5=new Point3D(1, 1, 0);
				Point3D dir6=new Point3D(1, -1, 0);
				Point3D dir7=new Point3D(-1, -1, 0);
				Point3D dir8=new Point3D(-1, 1, 0);
				HalfLine ray1=new HalfLine(current, dir1);
				HalfLine ray2=new HalfLine(current, dir2);
				HalfLine ray3=new HalfLine(current, dir3);
				HalfLine ray4=new HalfLine(current, dir4);
				HalfLine ray5=new HalfLine(current, dir5);
				HalfLine ray6=new HalfLine(current, dir6);
				HalfLine ray7=new HalfLine(current, dir7);
				HalfLine ray8=new HalfLine(current, dir8);
				for(int b=0; b<scene().boxes().size(); b++){
					if(scene().boxes().get(b).hitAt(ray1, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray2, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray3, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray4, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray5, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray6, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray7, result)<WP_BOX_DIST
							|| scene().boxes().get(b).hitAt(ray8, result)<WP_BOX_DIST){
						nearBox=true;
					}
				}
				/*
				if(scene().floor().hitAt(ray1, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray2, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray3, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray4, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray5, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray6, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray7, result)<WP_BOX_DIST
						|| scene().floor().hitAt(ray8, result)<WP_BOX_DIST)
					nearBox=true;*/
			}
			
			if(!nearBox)
				waypoints.add(current);
		}
		return waypoints;
	}
    
    //Implementation adapted from
    //http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
    private boolean pnpoly(List<Point3D> vertices, Point3D test){
      boolean c=false;
      int j = vertices.size()-1;
      for (int i=0; i<vertices.size(); j=i++){
    	  if ( ((vertices.get(i).y()>test.y()) != (vertices.get(j).y()>test.y())) &&
    		    	 (test.x() < (vertices.get(j).x()-vertices.get(i).x()) * 
    		    			 (test.y()-vertices.get(i).y()) / 
    		    			 (vertices.get(j).y()-vertices.get(i).y()) + vertices.get(i).x()) )
    		  c = !c;
      }
      return c;
    }

    //calculates box corners if offset false; else, calculates offsets
	private LinkedList<Point3D> getBoxCoord(boolean offset) {
		LinkedList<Point3D> list = new LinkedList<Point3D>();
    	for(int i = 0; i<scene().boxes().size(); i++){
        	Point3D origin = new Point3D();
        	origin.setX(scene().boxes().get(i).parallelepipedObjects()[0].p().x());
        	origin.setY(scene().boxes().get(i).parallelepipedObjects()[0].p().y());
        	origin.setZ(5);
        	
        	Point3D cornerA = origin.copy();
        	
        	Point3D cornerB = new Point3D();
        	cornerB.setX(scene().boxes().get(i).parallelepipedObjects()[0].u().x() + origin.x()); //+ bufferZone.x;
        	cornerB.setY((int) scene().boxes().get(i).parallelepipedObjects()[0].u().y() + origin.y()); //- bufferZone.y;
        	cornerB.setZ(5);
        	
        	Point3D cornerC = new Point3D();
        	cornerC.setX(scene().boxes().get(i).parallelepipedObjects()[0].v().x() + origin.x()); //- bufferZone.x;
        	cornerC.setY(scene().boxes().get(i).parallelepipedObjects()[0].v().y() + origin.y()); //+ bufferZone.y;
        	cornerC.setZ(5);
        	
        	Point3D cornerD = new Point3D();
        	cornerD.setX(origin.x() + scene().boxes().get(i).parallelepipedObjects()[0].u().x() 
        		+ scene().boxes().get(i).parallelepipedObjects()[0].v().x()); //+ bufferZone.x;
        	cornerD.setY(origin.y() + (int) scene().boxes().get(i).parallelepipedObjects()[0].u().y() 
    			+ scene().boxes().get(i).parallelepipedObjects()[0].v().y()); //+ bufferZone.y;
        	cornerD.setZ(5);
        	/*
        	System.out.print( "Box " + i + " corners at: ");
        	System.out.print( "(" + cornerA.x + ","+ cornerA.y + ")" );
        	System.out.print( "(" + cornerB.x + ","+ cornerB.y + ")" );
        	System.out.print( "(" + cornerC.x + ","+ cornerC.y + ")" );
        	System.out.print( "(" + cornerD.x + ","+ cornerD.y + ")" );
        	System.out.println();*/
        	
        	if(offset){
        		double dist_ad=cornerA.distance1(cornerD);
        		double dist_bc=cornerB.distance1(cornerC);
        		
        		cornerA.setX(cornerA.x()+ (WP_OFFSET*(cornerA.x()-cornerD.x())/dist_ad));
        		cornerA.setY(cornerA.y()+ (WP_OFFSET*(cornerA.y()-cornerD.y())/dist_ad));
        		
        		cornerB.setX(cornerB.x()+ (WP_OFFSET*(cornerB.x()-cornerC.x())/dist_bc));
        		cornerB.setY(cornerB.y()+ (WP_OFFSET*(cornerB.y()-cornerC.y())/dist_bc));
        		
        		cornerC.setX(cornerC.x()+ (WP_OFFSET*(cornerC.x()-cornerB.x())/dist_bc));
        		cornerC.setY(cornerC.y()+ (WP_OFFSET*(cornerC.y()-cornerB.y())/dist_bc));
        		
        		cornerD.setX(cornerD.x()+ (int)(WP_OFFSET*(cornerD.x()-cornerA.x())/dist_ad));
        		cornerD.setY(cornerD.y()+ (int)(WP_OFFSET*(cornerD.y()-cornerA.y())/dist_ad));
        	}
        	
        	list.add(cornerA);
        	list.add(cornerB);
        	list.add(cornerC);
        	list.add(cornerD);
        }
		return list;
	}

	@Override
    public void initialize() throws Exception
    {
        super.initialize();
        pf.init();
        
        // showing the true state in simulation
        if (pc().isSimulated())
            scene().update(pc().simDynState());
        view().updateCanvas();
        
        pc().createCommunicator(ROBOT_NAME, commLogic);
    }
    
    @Override
    public void control() throws Exception
    {
        double pitch = 0;
        int dMrcL = 0, dMrcR = 0;
        boolean isNewData = false;
        synchronized (commLogic)
        {
            isNewData = commLogic.isNewData;
            if (isNewData)
            {
                dMrcL = commLogic.dMrcL;
                dMrcR = commLogic.dMrcR;
                commLogic.dMrcL = commLogic.dMrcR = 0;
                
                if (dist == null) dist = new int[commLogic.dist.length];
                for (int i = 0; i < dist.length; ++i)
                    dist[i] = commLogic.dist[i];
                
                pitch = commLogic.pitch;
            }
        }
        
        if (isNewData)
        {        	
            pf.next(pitch, dMrcL, dMrcR, dist);
            
            //check that robot is not too close to anything
            tooClose=false;
            for (int i : dist){
            	if(i<FREAK_OUT){
            		tooClose=true;
            		freaked=true;
            	}
            }
            
            if (tooClose){
            	commLogic.sendCommand(KeyEvent.VK_DOWN);
            }
            else if (freaked){
            	//check if we know where we are, turn until we find ourselves
            	if(pf.lost())
            		commLogic.sendCommand(KeyEvent.VK_LEFT);
            	else
            		freaked=false;
            }
            
            //if the dynamic point has been set, commence pathfinding to here
            else if(!pathing && scene().isOnFloor(scene().selectedPoint().position().x(), 
            		scene().selectedPoint().position().y())){
                //System.out.println(scene().selectedPoint().position().x()+" "+
                		//scene().selectedPoint().position().y());
            	Particle likely = pf.getHighestWeight();
                if(scene().selectedPoint().position().distance1(new Point3D(likely.x(), likely.y(), 4))
                		>=DIST_BUFFER){
                	arrived=false;
                	pathing=true;
                	pather.setGoal(scene().selectedPoint().position());

                	pather.computePath(new Point3D(likely.x(), likely.y(), 4));
                	System.out.println(pather.getPath());
                	goToPoint=pather.nextPoint();
                	System.out.println(goToPoint);
                }
            }
            
            if(pathing && !tooClose){
            	Particle likely = pf.getHighestWeight();
            	if(goToPoint.distance1(new Point3D(likely.x(), likely.y(), 4))<DIST_BUFFER){
            		//System.out.println("done step");
            		goToPoint=pather.nextPoint();
            		if(goToPoint==null){
            			arrived=true;
            			pathing=false;
            			//System.out.println("done pathing");
            		}
            		else
            			System.out.println(goToPoint);
            	}
            	if(!arrived){
            		double yaw=pf.yaw(likely);
            		double t= Math.floor(yaw/(2*Math.PI));
            		yaw=yaw-t*2*Math.PI;
            		//used the formula from the following to calculate the desire angle
            		//http://www.euclideanspace.com/maths/algebra/vectors/angleBetween/index.htm
            		double wanted_yaw=Math.atan2(0.0,1.0) - 
            			Math.atan2(goToPoint.y()-likely.y(),goToPoint.x()-likely.x());
            		wanted_yaw=-1*wanted_yaw;
            		double diff=wanted_yaw-yaw;
            		t= Math.floor(Math.abs(diff)/(2*Math.PI));
            		if (diff<0)
            			diff=diff+(t+1)*2*Math.PI;
            		else
            			diff=diff-t*2*Math.PI;
            		//checks if pointing in appropriate direction
            		if(Math.abs(diff)<=RADIAN_BUFFER || Math.abs(diff)>=2*Math.PI-RADIAN_BUFFER){
            			commLogic.sendCommand(KeyEvent.VK_UP);
            			if(pf.lost())
                			commLogic.sendCommand(KeyEvent.VK_RIGHT);
            				
            		}
            		else if(diff<Math.PI){
            			commLogic.sendCommand(KeyEvent.VK_LEFT);
            		}
            		else{
            			commLogic.sendCommand(KeyEvent.VK_RIGHT);
            		}
            	}
            	
            }
            //if lost, turn around!
            if (!pathing && pf.lost()){
            	commLogic.sendCommand(KeyEvent.VK_LEFT);
            }
            
            if (pc().isSimulated())
                scene().update(pc().simDynState());
        }
        
        drawer.clear();
        for(int i=0; i<wPoints.size(); i++)
        	//draws waypoints
            drawer.drawRect(wPoints.get(i).x()-10, wPoints.get(i).y()-10,
                    wPoints.get(i).x()+10, wPoints.get(i).y()+10, red, true);
        if(pathing){
        	//draws goal
        	drawer.drawRect(pather.getGoal().x()-20, pather.getGoal().y()-20,
        			pather.getGoal().x()+20, pather.getGoal().y()+20, red, true);
        	//draw current destination
        	drawer.drawRect(goToPoint.x()-20, goToPoint.y()-20,
        			goToPoint.x()+20, goToPoint.y()+20, green, true);
        }
        
        drawer.swapBuffers();
        
        view().updateCanvas();
        pc().msDelay(200);
    }
    
    //--------------------------------------------------------------------------
    
    private class CommunicatorLogicImpl extends CommunicatorLogic
    {
        public CommunicatorLogicImpl() { dist = null; }
        
        @Override
        public void initalize() throws Exception
        {
            super.initalize();
            maxKeyCodes = channel().readByte();
            int len = channel().readByte();
            synchronized (this)
            {
                dist = new short[len];
                distTmp = new short[len];
                dMrcL = dMrcR = 0;
                pitch = 0.0;
                isNewData = false;
                nextComm=-1;
            }
            isControlRound = true;
        }
        
        public void sendCommand(int command){
        	nextComm=command;
        }
        
        @Override
        public void logic() throws Exception
        {
            if (isControlRound)
            {
            	//gives controls to robot
            	if(nextComm!=-1){
            		channel().writeByte((byte)1);
            		channel().writeShort((short)nextComm);
            		channel().flush();
            		nextComm=-1;
            	}
            	
                Set<Short> activeKeys = activeKeys();
                synchronized (activeKeys)
                {
                    if (activeKeys.contains((short)KeyEvent.VK_ESCAPE))
                    {
                        channel().writeByte((byte)-1);
                        terminate();
                    }
                    else
                    {
                        byte size = (byte) activeKeys.size();
                        if (size > maxKeyCodes) size = maxKeyCodes;
                        
                        channel().writeByte(size);
                        for (short keycode : activeKeys)
                        {
                            channel().writeShort(keycode);
                            if (--size == 0) break;
                        }
                    }
                }
                channel().flush();
            }
            else
            {
                channel().writeByte((byte)-2);
                channel().flush();
                
                float pitch = channel().readFloat();
                short dL = channel().readShort();
                short dR = channel().readShort();
                for (int i = 0; i < distTmp.length; ++i)
                    distTmp[i] = channel().readShort();
                
                synchronized (this)
                {
                    this.pitch = pitch;
                    dMrcL += dL;
                    dMrcR += dR;
                    for (int i = 0; i < dist.length; ++i)
                        dist[i] = distTmp[i];
                    isNewData = true;
                }
            }
            isControlRound = !isControlRound;
            msDelay(50);
        }
        
        private int nextComm;
        private short[] dist, distTmp;
        private int dMrcL, dMrcR;
        private double pitch;
        private boolean isControlRound, isNewData;
        private byte maxKeyCodes;
    }
    
    //--------------------------------------------------------------------------
    
    private class MouseListenerImpl extends MouseInputAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            view().getPoint(e.getX(), e.getY(), p);
            if (scene().isOnFloor(p.x(), p.y()))
            {
                p.setZ(scene().isOnCarpet(p.x(), p.y())
                       ? (1+scene().carpet().height()) : 1);
                scene().selectedPoint().setPosition(p);
            }
            else scene().selectedPoint().setPosition(null);
        }
        
        private Point3D p = new Point3D();
    }
    
    @Override
    public MouseListener mouseListener() { return mouseListener; }
    
    //--------------------------------------------------------------------------
    
    private int[] dist;
    private Drawer drawer;
    private final Color red = new Color(250f/255, 0f/255, 0f/255);
    private final Color green = new Color(0f/255, 250f/255, 0f/255);
    private LinkedList<Point3D> wPoints;

    
    private final double RADIAN_BUFFER = 0.2;
    private final double DIST_BUFFER = 100;
    private final int WP_OFFSET = 180;
    private final int WP_BOX_DIST=WP_OFFSET-50;
    
    private final int FREAK_OUT=120;
    
    private boolean freaked;
    private boolean tooClose;
    private boolean pathing;
    private boolean arrived;
    private Point3D goToPoint;
    private final ParticleFilterAlg pf;
    private final CommunicatorLogicImpl commLogic;
    private final MouseListenerImpl mouseListener;
    private final PathFinderAlg pather;
}