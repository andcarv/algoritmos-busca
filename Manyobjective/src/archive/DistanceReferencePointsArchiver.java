package archive;

import java.util.ArrayList;

import problema.Problema;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class DistanceReferencePointsArchiver extends PreciseArchiver {

	
	private Problema problema;
	
	public DistanceReferencePointsArchiver(Problema prob, String id) {
		problema = prob;
		ID = id;
	}
	
	//Remove the solution with higher distance to one of the reference points
	//Reference points are the points in the extremes of each objective and the point closer to the ideal point
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		ArrayList<ArrayList<Solucao>> extremos =AlgoritmoAprendizado. obterSolucoesExtremasIdeais(front, true, problema);
		AlgoritmoAprendizado.definirDistanciasSolucoesProximasIdeais(extremos, front, ID, problema);
		
		front.add(new_solution);
		
		double highDistanceValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.menorDistancia > highDistanceValue){
				highDistanceValue = solucao.menorDistancia;
				index = i;
			}
		}
		
		front.remove(index);

	}

}
