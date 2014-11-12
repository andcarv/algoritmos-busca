package seeds;

import hyper.HyperplaneReferencePoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;
import kernel.mopso.MOPSO;
import problema.Problema;
import solucao.ComparetorDistancia;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class InitializeHyperplaneReset extends InitializeSwarms {

	
	public HyperplaneReferencePoints hyperplane = null;
	
	public InitializeHyperplaneReset(MOPSO[] s, Problema p){
		super(s,p);
		ID = "hypr";
		
		hyperplane = new HyperplaneReferencePoints(problema.m);
	}
	
	public void initializeSwarms(ArrayList<Solucao> solutions, double box_range) {
		//Initializes the solutions of each swarm
		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];
			
			double[][] new_limits = new double[2][problema.n];
			
			//Selects a radom point from all hyperplane
			double[] reference_point =  hyperplane.selectRandomReferencePoint(HyperplaneReferencePoints.ALL);
			
			//Calculate the euclidian distance between each solution of the front to the reference point
			for (Iterator<Solucao> iterator = solutions.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(solucao.objetivos, reference_point);
			}
			
			//Sort the initial front according to the distance to the reference ponto
			ComparetorDistancia comp = new ComparetorDistancia();
			Collections.sort(solutions,comp);
			
			double centroid[] = ((SolucaoNumerica)solutions.get(0)).getVariaveis();
			
			for(int j = 0; j<problema.n; j++){
				//Defines the lower and upper limits of the new search space. The values can't overcome the maximum values
				//of the search space of the problem
				new_limits[0][j] = Math.max(centroid[j] - box_range, problema.MIN_VALUE);
				new_limits[1][j] = Math.min(centroid[j] + box_range, problema.MAX_VALUE);
				
			}
			
			//Tries to update the archive with the solutions closer to the reference point
			for(int j = 1; j< swarm.archiveSize; j++){		
				if(j<solutions.size()){
					Solucao random_solution = solutions.get(j);
					if(!swarm.pareto.getFronteira().contains(random_solution))
						swarm.pareto.add(random_solution, swarm.archiver);
				} else
					break;
			}
			
			//Reset the population of the swarm
			swarm.populacao.clear();
			//Start a new population
			swarm.inicializarPopulacao(new_limits);
			swarm.atualizarRepositorio();
			
			
			
		}
		
	
		for (int i = 0; i < swarms.length; i++) {
			//In each swarm, the population chooses the leader according to its Pareto front  
			swarms[i].escolherLider.escolherLideres(swarms[i].populacao, swarms[i].pareto.getFronteira());
			//Initial mutation for swarm i
			swarms[i].escolherParticulasMutacao();
		}

	}

}
