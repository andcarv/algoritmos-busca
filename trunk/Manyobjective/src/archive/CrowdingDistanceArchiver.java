package archive;

import java.util.ArrayList;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class CrowdingDistanceArchiver extends PreciseArchiver {
	
	
	private int m;
	
	
	public CrowdingDistanceArchiver(int m){
		this.m = m;
		ID = "crowd";
	}

	/**
	 * Removes the solution with worst Crowding Distance
	 */
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		

		front.add(new_solution);

		AlgoritmoAprendizado.calcularCrowdingDistance(front, m);

		double highCDValue = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.crowdDistance < highCDValue){
				highCDValue = solucao.crowdDistance;
				index = i;
			}
		}
		
		/*double highCDValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.crowdDistance > highCDValue){
				highCDValue = solucao.crowdDistance;
				index = i;
			}
		}*/

		front.remove(index);

		
	}

}
