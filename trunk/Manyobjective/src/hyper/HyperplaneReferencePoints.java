package hyper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;

public class HyperplaneReferencePoints {
	
	
	public double[][] reference_points = null;
	public int m;
	
	public final static int ALL = 0;
	public final static int EDGE = 1;
	public final static int MIDDLE = 2;
	public final static int EXTREME = 3;
	
	public final static double MAX_OBJ_VALUE = 0.6;
	
	public int lastSelectedIndex = -1;
	
	
	
	public HyperplaneReferencePoints(int m) {
		this.m = m;
		loadReferencePoints(m);
	}
	
	public void loadReferencePoints(int m){
		String reference_file = "ref/ref_"+m+".txt";
		try{
			int number_points = 0;
			BufferedReader reader = new BufferedReader(new FileReader(reference_file));
			
			while(reader.ready()){
				String line = reader.readLine();
				if(!line.equals(""))
					number_points++;
			}
			
			reader.close();
			reader = new BufferedReader(new FileReader(reference_file));
			
			reference_points = new double[number_points][m];
			
			int j = 0;
			while(reader.ready()){
				String line = reader.readLine();
				if(!line.equals("")){
					double[] reference_point = reference_points[j++];
					String line_values[] = line.split("\t");
					for (int i = 0; i < line_values.length; i++) {
						if(line_values[i].length()>5)
							reference_point[i] = new Double(line_values[i].substring(0,5));	
						else
							reference_point[i] = new Double(line_values[i]);
					}
				}
					
			}
			reader.close();
		} catch(IOException ex){ex.printStackTrace();}
	}
	
	/**
	 * Returns only the points that belongs to the edges of the hyperplane
	 * @return
	 */
	public double[][] pointsOnEdge(){
		ArrayList<double[]> edgePoints = new ArrayList<double[]>();
		for(int i = 0;i<reference_points.length;i++){
			double[] point = reference_points[i];
			int cont = 0;
			for(int j=0; j<point.length;j++){
				if(point[j]==0)
					cont++;
				if(cont>0)
					break;
			}
			
			if(cont > 0)
				edgePoints.add(point);
		}
		
		System.out.println(edgePoints.size());
		
		/*try{
		PrintStream edge = new PrintStream("edge.txt"); 
		for (Iterator iterator = edgePoints.iterator(); iterator.hasNext();) {
			double[] ds = (double[]) iterator.next();
			for(int i = 0; i< ds.length;i++)
				edge.print(ds[i] + "\t");
			edge.println();
		}
		
		}catch(IOException ex){ex.printStackTrace();}*/
		
		double edgePoints_double[][] = new double[edgePoints.size()][m];
		int i = 0;
		for (Iterator<double[]> iterator = edgePoints.iterator(); iterator.hasNext();) {
			double[] ds = iterator.next();
			edgePoints_double[i++] = ds;
		}
		return edgePoints_double;
	}
	
	/**
	 * Returns only the points in the middle of the Hyperplane.
	 * Points not on the edges and with no more than 0.6 of objective value
	 * @return
	 */
	public double[][] pointsOnMiddle(){
		ArrayList<double[]> middlePoints = new ArrayList<double[]>();
		for(int i = 0;i<reference_points.length;i++){
			double[] point = reference_points[i];
			int cont = 0;
			int cont2 = 0;
			for(int j=0; j<point.length;j++){
				if(point[j]==0)
					cont++;
				if(point[j]>MAX_OBJ_VALUE)
					cont2++;
				
				if(cont>0)
					break;
				if(cont2>0)
					break;
			}
			
			if(cont == 0 && cont2==0){
				middlePoints.add(point);
			}
		}
		
	
		/*try{
		PrintStream edge = new PrintStream("/home/andrebia/middle.txt"); 
		for (Iterator iterator = middlePoints.iterator(); iterator.hasNext();) {
			double[] ds = (double[]) iterator.next();
			for(int i = 0; i< ds.length;i++)
				edge.print(ds[i] + "\t");
			edge.println();
		}
		
		}catch(IOException ex){ex.printStackTrace();}*/
		
		double middlePoints_double[][] = new double[middlePoints.size()][m];
		int i = 0;
		for (Iterator<double[]> iterator = middlePoints.iterator(); iterator.hasNext();) {
			double[] ds = iterator.next();
			middlePoints_double[i++] = ds;
		}
		return middlePoints_double;
	}
	
	/**
	 * Return the points if max_dist from the extreme point of index "index"
	 * @param index Index of the extreme point
	 * @param max_dist maximum distance
	 * @return
	 */
	public double[][] pointsNearExtreme(int index, double max_dist ){
		
		double extremePoint[] = new double[m];
		
		extremePoint[index] = 1;
		
		ArrayList<double[]> points_near = new ArrayList<double[]>();
		
		for(int i = 0;i<reference_points.length;i++){
			double[] point = reference_points[i];
			double dist = AlgoritmoAprendizado.distanciaEuclidiana(point, extremePoint);
			if(dist<max_dist)
				points_near.add(point);
		}
		
		/*try{
			PrintStream edge = new PrintStream("/home/andrebia/near.txt"); 
			for (Iterator iterator = points_near.iterator(); iterator.hasNext();) {
				double[] ds = (double[]) iterator.next();
				for(int i = 0; i< ds.length;i++)
					edge.print(ds[i] + "\t");
				edge.println();
			}
		}catch(IOException ex){ex.printStackTrace();}*/
		double nearPoints_double[][] = new double[points_near.size()][m];
		int i = 0;
		for (Iterator<double[]> iterator = points_near.iterator(); iterator.hasNext();) {
			double[] ds = iterator.next();
			nearPoints_double[i++] = ds;
		}
		return nearPoints_double;
	}
	
	public double[] selectRandomReferencePoint(int region){
		
		double[][] selectedPoints = null;
		
		if(region==ALL){
			selectedPoints = reference_points;
		}
		if(region==EDGE){
			selectedPoints = pointsOnEdge();
		}
		if(region==MIDDLE){
			selectedPoints = pointsOnMiddle();
		}
		
		if(region==EXTREME){
			int random_extreme = (int)(Math.random()*m);
			if(m<8)
				selectedPoints =  pointsNearExtreme(random_extreme, 0.5);
			else
				selectedPoints = pointsNearExtreme(random_extreme, 0.9);
		}
		
		lastSelectedIndex =  (int) (Math.random()*selectedPoints.length);
		return selectedPoints[lastSelectedIndex];
	}
	
}
