package archive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.Problema;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class MGAArchiver extends PreciseArchiver {

	
	private Problema problema;
	
	private FronteiraPareto pareto;
	
	public MGAArchiver(Problema prob,String id) {
		problema = prob;
		ID = id;
		
		
		
		String []maxmim = new String[prob.m];
		
		for (int j = 0; j < maxmim.length; j++) {
			maxmim[j] = "-";
		}
		
		
		pareto = new FronteiraPareto(maxmim);
	}
	
	
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		if(ID.equals("mga"))
			archivingMGA(front, new_solution);
		if(ID.equals("mga2"))
			archivingMGA2(front, new_solution);

	}
	
	public void archivingMGA(ArrayList<Solucao> front, Solucao new_solution){
		Collections.shuffle(front);
		front.add(new_solution);
		int b = compute_b_mga(front);	
		int index_removed = -1;
		while(index_removed==-1){
			for(int i = front.size()-1; i>=0;i--){
				Solucao solution_i = front.get(i);
				double box_i[] = box_mga(solution_i, b);
				for(int j = front.size()-1; j>=0;j--){
					if(i!=j){
						Solucao solution_j = front.get(j);
						double box_j[] = box_mga(solution_j, b);
						int comparation = pareto.compareObjectiveVector(box_i, box_j); 
						if(comparation == FronteiraPareto.DOMINATED_BY || comparation == FronteiraPareto.EQUALS){
							index_removed = i;
							break;
						}

					}
				}
				if(index_removed!=-1){
					//System.out.println(b);
					break;
				}
			}
			b--;
		}
		front.remove(index_removed);		
	}
	
	public void archivingMGA2(ArrayList<Solucao> front, Solucao new_solution){
		Collections.shuffle(front,AlgoritmoAprendizado.random);
		front.add(new_solution);
		int b = compute_b_mga(front);	
		int index_removed = -1;
		while(index_removed==-1){
			for(int i = front.size()-1; i>=0;i--){
				Solucao solution_i = front.get(i);
				double box_i[] = box_mga(solution_i, b);
				for(int j = front.size()-1; j>=0;j--){
					if(i!=j){
						Solucao solution_j = front.get(j);
						double box_j[] = box_mga(solution_j, b);
						int comparation = pareto.compareObjectiveVector(box_i, box_j); 
						if(comparation == FronteiraPareto.DOMINATED_BY){
							index_removed = i;
							break;
						}

					}
				}
				if(index_removed!=-1){
					//System.out.println(b);
					break;
				}
			}
			b--;
		}
		front.remove(index_removed);		
	}
	
	public int compute_b_mga(ArrayList<Solucao> front){
		double max_value = 0;
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao solution = (Solucao) iterator.next();
			for(int i = 0; i<problema.m;i++)
				if(solution.objetivos[i]> max_value)
					max_value = solution.objetivos[i];
		}
		return (int) Math.floor(AlgoritmoAprendizado.log2(max_value)) + 1;
	}
	
	public double[] box_mga(Solucao solution, int b){
		double box[] = new double[problema.m];
		for(int i = 0; i<problema.m; i++){
			double z = solution.objetivos[i];
			//box[i] = (int)Math.floor(z*b_val+ 0.5);
			box[i] = (int) Math.floor(z / Math.pow(2.0, b) + 0.5);
		}
			
		return box;
	}

}
