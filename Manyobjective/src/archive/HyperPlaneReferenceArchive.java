package archive;

import hyper.HyperplaneReferencePoints;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;



import solucao.Solucao;

public class HyperPlaneReferenceArchive extends PreciseArchiver {
	
	public int m;
	
	//public int reference_index = -1;
	public double[] reference_point = null;
	
	HyperplaneReferencePoints hyper = null;

	public HyperPlaneReferenceArchive(int m, int region) {
		
		this.m = m;
		hyper = new HyperplaneReferencePoints(m);
		
		if(region==HyperplaneReferencePoints.ALL){
			ID = "hyp_a";
		}
		if(region==HyperplaneReferencePoints.EDGE){
			ID = "hyp_ed";
		}
		if(region==HyperplaneReferencePoints.MIDDLE){
			ID = "hyp_m";
		}
		
		if(region==HyperplaneReferencePoints.EXTREME){
			ID = "hyp_ex";
		}
		
		
		reference_point = hyper.selectRandomReferencePoint(region);
		
		System.out.println("Reference Index: " + hyper.lastSelectedIndex);
		System.out.print("Reference Point:");
		
		
		for(int i = 0; i<m; i++){
			System.out.print("\t" + reference_point[i]);
		}
		System.out.println();
	}
	
	//Removes the solution with worst distance to the reference point
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {	
		
		front.add( new_solution);
		
		//For each solution on the front, it calculates the distance to the reference point
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(reference_point, solucao.objetivos);
			//Round up the distance
			BigDecimal b = new BigDecimal(solucao.menorDistancia);		 
			solucao.menorDistancia = (b.setScale(5, BigDecimal.ROUND_UP)).doubleValue();
		}

		double highDistanceValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.menorDistancia >= highDistanceValue){
				highDistanceValue = solucao.menorDistancia;
				index = i;
			}
		}
		try{
			front.remove(index);
		}	catch(ArrayIndexOutOfBoundsException ex){ex.printStackTrace();}

	}
	
	
	
	
	
	

	
	
	
	
	
	
	
	/**
	 * Removes from the archive the solutions in the most crowded regions of the hyperplane
	 * @param front
	 */
	/*public void filterHyperplane(ArrayList<Solucao> front, ArrayList<ArrayList<Solucao>> extremes, int m){
		//Checks if the archive has more solutions than the maximum allowed
		if(front.size()>archiveSize){
			double extremes_translated[][] = new double [m][m];
			Solucao ideal = extremes.get(m).get(0);
			
			for(int i = 0; i<extremes.size()-1; i++){
				ArrayList<Solucao> extremes_i = extremes.get(i);
				int random_index = (int) (Math.random() * extremes_i.size());
				Solucao extreme_selected = extremes_i.get(random_index);
				extremes_translated[i] = new double[m];
				for(int j = 0; j<m; j++){
					extremes_translated[i][j] = extreme_selected.objetivos[j] - ideal.objetivos[j];
				}
			}
			
			int p = 3;
			double[][] reference_points = AlgoritmoAprendizado.getReferencePointsHyperPlane(m, p, extremes_translated);
			System.out.println();
			
		}
	}
	*/
	

}
