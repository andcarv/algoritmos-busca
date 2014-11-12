package archive;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;



import solucao.Solucao;

public class HyperPlaneReferenceArchiveRandom extends PreciseArchiver {
	
	public int m;
	public double[][] reference_points = null;
	public int reference_index = -1;
	public double[] reference_point = null;

	public HyperPlaneReferenceArchiveRandom(int m) {
		ID = "hyp_r";
		this.m = m;
		loadReferencePoints(m);
		selectRandomReferencePoint();
	}
	
	public void changeReferencePoint(int generation){
		double prob = 1.0/(generation+1);
		double randon = Math.random();
		if(randon<prob){
			selectRandomReferencePoint();
		}
		
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
	
	public void selectRandomReferencePoint(){
		reference_index =  (int) (Math.random()*reference_points.length);;
		reference_point = reference_points[reference_index];
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
