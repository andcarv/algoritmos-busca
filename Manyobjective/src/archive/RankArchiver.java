package archive;

import java.util.ArrayList;
import java.util.Collections;

import rank.Rank;
import solucao.ComparetorCrowdedOperator;
import solucao.Solucao;

public class RankArchiver extends PreciseArchiver {

	
	private Rank rankMethod;
	
	public RankArchiver(Rank rm){
		rankMethod = rm;
		ID = rm.ID;
	}
	
	/**
	 * Removes the solution with worst rank value
	 */
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		front.add(new_solution);
		//Calculates the ranking of each solution in the front
		rankMethod.rankear(front, -1);
		ComparetorCrowdedOperator comp = new ComparetorCrowdedOperator();
		Collections.sort(front, comp);
		front.remove(front.size()-1);
	}

}
