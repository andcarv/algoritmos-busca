package seeds;

import java.util.ArrayList;
import java.util.Collections;

import kernel.mopso.MOPSO;
import problema.Problema;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class InitializeExtremesReset extends InitializeSwarms{
	
	public InitializeExtremesReset(MOPSO[] s, Problema p){
		super(s,p);
		ID = "extr";
	}
	
	public void initializeSwarms(ArrayList<Solucao> solutions, double box_range) {
		ArrayList<Integer> available_dimensions = new ArrayList<Integer>();

		for(int i = 0; i<problema.m; i++){
			available_dimensions.add(i);
		}

		Collections.sort(available_dimensions);

		//Initializes the solutions of each swarm


		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];

			double[][] new_limits = new double[2][problema.n];
			int index_guide = -1;
			if(available_dimensions.size()>0){
				int dimension = available_dimensions.remove(0);

				//double closerValue = Double.MAX_VALUE;
				double closerValue = 0;

				for(int j = 0; j<solutions.size(); j++){
					if(solutions.get(j).objetivos[dimension] > closerValue){
						closerValue = solutions.get(j).objetivos[dimension];
						index_guide = j;
					}
				}


			} else{
				index_guide = (int)(Math.random() * solutions.size());
			}
			//Get the centroid guide for the swarm i
			double[] centroid = ((SolucaoNumerica)solutions.get(index_guide)).getVariaveis();


			for(int j = 0; j<problema.n; j++){
				//Defines the lower and upper limits of the new search space. The values can't overcome the maximum values
				//of the search space of the problem
				new_limits[0][j] = Math.max(centroid[j] - box_range, problema.MIN_VALUE);
				new_limits[1][j] = Math.min(centroid[j] + box_range, problema.MAX_VALUE);

			}

			if(i<problema.m){
				ComparetorObjetivo comp = new ComparetorObjetivo(i);
				Collections.sort(solutions, comp);
				int j = 0;

				int as = swarm.archiveSize;
				int ss = solutions.size();
				while(j<as && j<ss){
					Solucao random_solution = solutions.get((ss-1)-j);
					if(!swarm.pareto.getFronteira().contains(random_solution))
						swarm.pareto.add(random_solution, swarm.archiver);
					j++;
				}
			} else{
				for(int j = 0; j< swarm.archiveSize; j++){
					int random_index = (int)(Math.random() * solutions.size());
					Solucao random_solution = solutions.get(random_index);
					if(!swarm.pareto.getFronteira().contains(random_solution))
						swarm.pareto.add(random_solution, swarm.archiver);
				}
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
