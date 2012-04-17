package exercise;

import geom3d.HalfLine;
import geom3d.Point3D;

import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

//consider http://code.google.com/p/a-star/source/browse/trunk/java/AStar.java?r=8?

import model.scene.Box;
import model.scene.SceneModel;

public class PathFinderAlg {

	@SuppressWarnings("unchecked")
	public PathFinderAlg(SceneModel scene, LinkedList<Point3D> waypoints) {
		this.scene=scene;
		goal=null;
		indexToPoints=waypoints;
		adjacency = new Vector<Vector<Double>>();
		Vector<Double> temp= new Vector<Double>();
		for (int i=0; i<indexToPoints.size(); i++){
			temp.add(new Double(INF));
		}
		for (int i=0; i<indexToPoints.size(); i++){
			adjacency.add(temp);
		}
		Point3D u, v = new Point3D();
		double distance=-1;
		for (int s=0; s<indexToPoints.size(); s++){
			for (int t=0; t<indexToPoints.size(); t++){
				if(s==t){
					temp=adjacency.elementAt(s);
					temp.set(s, 0.0);
					adjacency.set(s, (Vector<Double>) temp.clone());
				}
				else if(s<t){
					u=indexToPoints.get(s);
					v=indexToPoints.get(t);
					//if they don't go too close to a box
					distance=calcDist(u, v);
					temp=adjacency.elementAt(s);
					temp.set(t, distance);
					adjacency.set(s, (Vector<Double>) temp.clone());
					temp=adjacency.elementAt(t);
					temp.set(s, distance);
					adjacency.set(t, (Vector<Double>) temp.clone());
				}
				else{
					//s>t, do nothing, already covered
				}
			}
		}
		LinkedList<Integer> remove=new LinkedList<Integer>();
		boolean other=false;
		for(int i=0; i<indexToPoints.size(); i++){
			for(int j=0; j<adjacency.get(i).size(); j++){
				if(adjacency.get(i).get(j).doubleValue()!=0 
						&& adjacency.get(i).get(j).doubleValue()<INF)
					other=true;
			}
			if(!other)
				remove.add(i);
		}
		for(int i=remove.size()-1; i>=0; i--){
			indexToPoints.remove(i);
			adjacency.remove(i);
			for(int j=0; j<adjacency.size(); j++){
				adjacency.get(j).remove(i);
			}
		}
		//printAdj();
	}
	
	private double calcDist(Point3D u, Point3D v) {
		double distance=u.distance1(v);
		Point3D dir=new Point3D(v.x()-u.x(), v.y()-u.y(), 10);
		HalfLine ray=new HalfLine(u, dir);
		List<Box> boxs=scene.boxes();
		Point3D result=new Point3D(-1,-1,-1);
		for(int i=0; i<boxs.size(); i++){
			if(boxs.get(i).hitAt(ray, result)<distance)
				return INF;
		}
		
		Point3D n_u=u.copy();
		Point3D n_v=v.copy();
		n_u.setX(u.x()-RADIUS);
		n_v.setX(v.x()-RADIUS);
		dir=new Point3D(n_v.x()-n_u.x(), n_v.y()-n_u.y(), 10);
		ray=new HalfLine(n_u, dir);
		for(int i=0; i<boxs.size(); i++){
			if(boxs.get(i).hitAt(ray, result)<distance){
				return INF;
			}
		}
		
		n_u.setX(u.x()+RADIUS);
		n_v.setX(v.x()+RADIUS);
		dir=new Point3D(n_v.x()-n_u.x(), n_v.y()-n_u.y(), 10);
		ray=new HalfLine(n_u, dir);
		for(int i=0; i<boxs.size(); i++){
			if(boxs.get(i).hitAt(ray, result)<distance){
				return INF;
			}
		}
		
		n_u=u.copy();
		n_v=v.copy();
		n_u.setY(u.y()-RADIUS);
		n_v.setY(v.y()-RADIUS);
		dir=new Point3D(n_v.x()-n_u.x(), n_v.y()-n_u.y(), 10);
		ray=new HalfLine(n_u, dir);
		for(int i=0; i<boxs.size(); i++){
			if(boxs.get(i).hitAt(ray, result)<distance){
				return INF;
			}
		}
		
		n_u.setX(u.y()+RADIUS);
		n_v.setX(v.y()+RADIUS);
		dir=new Point3D(n_v.x()-n_u.x(), n_v.y()-n_u.y(), 10);
		ray=new HalfLine(n_u, dir);
		for(int i=0; i<boxs.size(); i++){
			if(boxs.get(i).hitAt(ray, result)<distance){
				return INF;
			}
		}
		
		return distance;
	}

	public void setGoal(Point3D g){goal=g;}
	public Point3D getGoal(){return goal;}
	
	//for debugging
	public void printAdj(){
		for (int i=0; i<indexToPoints.size(); i++){
			for (int j=0; j<indexToPoints.size(); j++){
				System.out.print(i+" "+j+ ": ");
				System.out.println((adjacency.get(j)).get(i));
			}
		}
		System.out.println(adjacency);
		
	}
	
	//check to see if we can go straight to the goal from the origin
	private boolean checkStraight(Point3D origin){
		if (calcDist(goal, origin)<INF){
			return true;
		}
		return false;
	}
	
	public void computePath(Point3D origin){
		if(goal==null)
			System.out.println("error!!!");
		if(!checkStraight(origin))
			startPathGen(origin);
		else{
			path=new LinkedList<Point3D>();
			path.add(goal);
		}
	}
	
	//find the closest graphed point to the origin and the goal
	private void startPathGen(Point3D origin){
		double min1=1000000;
		double min2=1000000;
		double dist1, dist2;
		int firstIDX=-1; 
		int lastIDX=-1;
		Point3D current;
		for(int i=0; i<indexToPoints.size(); i++){
			current=indexToPoints.get(i);
			dist1=origin.distance1(current);
			dist2=goal.distance1(current);
			if(dist1<min1){
				min1=dist1;
				firstIDX=i;
			}
			if(dist2<min2){
				min2=dist2;
				lastIDX=i;
			}
		}
		assert(firstIDX>-1 && lastIDX>-1);
		//set up the steps for this path
		//setUpPath(firstIDX, lastIDX); this was using A*
		setUpPathDijkstras(firstIDX, lastIDX);
	}
	
	//Core of Dijkstra's!
	private void setUpPathDijkstras(int firstIDX, int lastIDX){
		LinkedList<Double> distance = new LinkedList<Double>();
		LinkedList<Integer> fromNode = new LinkedList<Integer>();
		Set<Integer> unvisited = new HashSet<Integer>();
		for(int i=0; i<indexToPoints.size(); i++){
			distance.add(new Double( INF));
			fromNode.add(new Integer(-1));
			unvisited.add(new Integer(i));
		}
		distance.set(firstIDX, 0.0);
		int current=firstIDX;
		
		double dist=-1;
		int nei=-1;
		double min=INF;
		double temp=-1;
		boolean found=false;
		while(!unvisited.isEmpty()){
			unvisited.remove(new Integer(current));
			for(Integer neighbor : unvisited){
				nei=neighbor.intValue();
				dist=adjacency.get(current).get(nei)
					+ distance.get(current).doubleValue();
				if(dist<INF){
					distance.set(nei, new Double(dist));
					fromNode.set(nei, new Integer(current));
				}
			}
			if(current==lastIDX){
				found=true;
				break;
			}
			min=INF;
			for(Integer lowest : unvisited){
				temp=distance.get(lowest.intValue());
				if(temp < min){
					min=temp;
					current=lowest.intValue();
				}
			}	
		}
		path = new LinkedList<Point3D>();
		if(found){
			path.add(goal);
			path.add(indexToPoints.get(lastIDX));
			current=lastIDX;
			while(current!=firstIDX){
				System.out.println(current+" to "+fromNode.get(current).intValue()+ ": "+
						adjacency.get(current).get(fromNode.get(current).intValue()));
				current=fromNode.get(current).intValue();
				path.add(indexToPoints.get(current));
			}
		}
		else
			System.out.println("dijk err");
	}
	
	//set up the steps for this path from firstIDX and last IDX
	//this is the broken A* code
	@SuppressWarnings("unused")
	private void setUpPath(int firstIDX, int lastIDX){
		Set<Integer> openset = new HashSet<Integer>();
		Point3D first=indexToPoints.get(firstIDX);
		openset.add(firstIDX);
		Set<Integer> closedset=new HashSet<Integer>();
		
		Vector<Integer> cameFrom= new Vector<Integer>();
		for(int i=0; i<indexToPoints.size(); i++){
			cameFrom.add(new Integer(-1));
		}
		Vector<Double> gScore = new Vector<Double>();
		for (int i=0; i<indexToPoints.size(); i++){
			gScore.add(new Double(0));
		}
		Vector<Double> hScore = new Vector<Double>();
		for (int i=0; i<indexToPoints.size(); i++){
			hScore.add(new Double(-1));
		}
		hScore.set(firstIDX, new Double(0));
		Vector<Double> fScore = new Vector<Double>();
		for (int i=0; i<indexToPoints.size(); i++){
			fScore.add(first.distance1(indexToPoints.get(i)));
		}
		
		boolean found=false;
		
		boolean tentGbetter=false;
		int cur=-1;
		double min=2*INF+1;
		double temp, tentG=0;
		int lowest=-1;
		while(!openset.isEmpty()){
			cur=-1;
			min=2*INF+1;
			lowest=-1;
			for (Iterator<Integer> it=openset.iterator(); it.hasNext(); ){
				cur=it.next().intValue();
				temp=fScore.get(cur);
				if (temp<=min){
					min=temp;
					lowest=cur;
				}
			}
			assert(lowest>=0);
			if(lowest==lastIDX){
				found=true;
				break;//find path
			}
			assert(lowest>=0);
			openset.remove(new Integer(lowest));
			closedset.add(new Integer(lowest));
			System.out.println(lowest);
			for(int neighbor=0; neighbor<indexToPoints.size(); neighbor++){
				if(closedset.contains(new Integer(neighbor)) || neighbor==lowest)
					continue;
				/*if(adjacency.get(neighbor).get(lowest)>=INF){
					openset.remove(new Integer(neighbor));
					closedset.add(new Integer(neighbor));
					continue;
				}*/
				tentG=gScore.elementAt(neighbor)+adjacency.get(neighbor).get(lowest);
				tentGbetter=false;
				if(!openset.contains(new Integer(neighbor))){
					openset.add(new Integer(neighbor));
					hScore.set(neighbor, indexToPoints.get(neighbor).distance1(goal));
					tentGbetter=true;
				}
				else if(tentG<gScore.get(neighbor).doubleValue()){
					tentGbetter=true;
				}
				//else, tentGbetter will be false
				if(tentGbetter){
					cameFrom.set(neighbor, new Integer(lowest));
					gScore.set(neighbor, new Double(tentG));
					fScore.set(neighbor, new Double(tentG+hScore.get(neighbor).doubleValue()));
				}
			}
		}
		path = new LinkedList<Point3D>();
		if(found){
			path.add(goal);
			path.add(indexToPoints.get(lastIDX));
			cur=lastIDX;
			while(cur!=firstIDX){
				/*System.out.println(cur+" to "+cameFrom.get(cur).intValue()+ ": "+
						adjacency.get(cur).get(cameFrom.get(cur).intValue()));*/
				cur=cameFrom.get(cur).intValue();
				path.add(indexToPoints.get(cur));
			}
		}
		else
			System.out.println("A* error");
	}
	
	public Point3D nextPoint(){
		return path.pollLast();
	}
	public LinkedList<Point3D> getPath(){return path;}
	
    private final SceneModel scene;
	private Point3D goal;
	private LinkedList<Point3D> indexToPoints;
	private Vector< Vector<Double> > adjacency;
	private LinkedList<Point3D> path;
	private static double INF = 1000000;
	private static double RADIUS = 100;
}

