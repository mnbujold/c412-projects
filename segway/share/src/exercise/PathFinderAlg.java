package exercise;

import geom3d.Point3D;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import model.scene.SceneModel;

public class PathFinderAlg {

	public PathFinderAlg(SceneModel scene) {
		// TODO Auto-generated constructor stub
		//insert scene.getpoints
		indexToPoints=new LinkedList<Point3D>();
		indexToPoints.add(new Point3D(500, 100, 4));
		indexToPoints.add(new Point3D(1000, 400, 4));
		
		
	}
	
	public void setGoal(Point3D g){goal=g;}
	public Point3D getGoal(){return goal;}
	
	public void computePath(Point3D origin){
		double min=1000000;
		double dist;
		int firstIDX=-1;
		for(int i=0; i<map.size(); i++){
			dist=origin.distance1(indexToPoints.get(i));
			if(dist<min){
				min=dist;
				firstIDX=i;
			}
		}
		assert(firstIDX>-1);
		
		min=1000000;
		int lastIDX=-1;
		for(int i=0; i<map.size(); i++){
			dist=goal.distance1(indexToPoints.get(i));
			if(dist<min){
				min=dist;
				lastIDX=i;
			}
		}
		assert(lastIDX>-1);
	}
	
	private void setUpPath(int firstIDX, int lastIDX){
		LinkedList<Point3D> openset=new LinkedList<Point3D>();
		Point3D first=indexToPoints.get(firstIDX);
		openset.add(first);
		LinkedList<Point3D> closedset=new LinkedList<Point3D>();
		
		//LinkedList<Double> indexToGscore=
		
	}
	
	public Point3D nextPoint(){
		return steps.poll();
	}
	
	private Point3D goal;
	private LinkedList<Point3D> indexToPoints;
	private LinkedList<Edge> map;
	private Queue<Point3D> steps;
	
	private class Edge {
		public Point3D p1;
		public Point3D p2;
		public double weight;
		public Edge(Point3D p, Point3D d){
			p1=p;
			p2=d;
			weight=p.distance1(d);
		}
	}
}

