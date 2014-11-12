package archive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import problema.Problema;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class IdealArchiver extends PreciseArchiver {

	private Problema problem;
	
	public IdealArchiver(Problema prob){
		problem =prob;
		ID = "ideal";
	}
	
	//Removes the solutio with worst distance to the ideal point
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		//Obtains the ideal solution from the current front
		Solucao ideal = AlgoritmoAprendizado.obterSolucoesExtremasIdeais(front, false, problem).get(problem.m).get(0);
		
		front.add( new_solution);
		//Para cada solucao calcula sua distancia em relacao a solucao ideal
		//For each solution on the front, it calculates the distance to the ideal point
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(ideal.objetivos, solucao.objetivos);
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

}
