package seeds;

import java.util.ArrayList;

import kernel.mopso.MOPSO;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class InitializeRandomReset extends InitializeSwarms {

	
	public InitializeRandomReset(MOPSO[] s, Problema p){
		super(s,p);
		ID = "rndr";
	}
	
	/**
	 * Initialize the swarms with random solutions
	 * Don't use the previous solutions in the new population, only in the archive
	 * @param solutions
	 */
	public void initializeSwarms(ArrayList<Solucao> solutions, double box_range) {
		//Initializes the solutions of each swarm
		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];
			
			double[][] new_limits = new double[2][problema.n];
			
			int index_guide = (int)(Math.random() * solutions.size());
			//Get the centroid guide for the swarm i
			double[] centroid = ((SolucaoNumerica)solutions.get(index_guide)).getVariaveis();
			for(int j = 0; j<problema.n; j++){
				//Defines the lower and upper limits of the new search space. The values can't overcome the maximum values
				//of the search space of the problem
				new_limits[0][j] = Math.max(centroid[j] - box_range, problema.MIN_VALUE);
				new_limits[1][j] = Math.min(centroid[j] + box_range, problema.MAX_VALUE);
				
			}
			
			for(int j = 0; j< swarm.archiveSize; j++){
				int random_index = (int)(Math.random() * solutions.size());
				Solucao random_solution = solutions.get(random_index);
				if(!swarm.pareto.getFronteira().contains(random_solution))
					swarm.pareto.add(random_solution, swarm.archiver);
			}
			
			
			swarm.populacao.clear();
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
