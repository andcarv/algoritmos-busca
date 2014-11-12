package archive;

import java.util.ArrayList;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class SPEA2Archiver extends PreciseArchiver {

	
	public SPEA2Archiver() {
		ID = "spea2";
	}
	
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		front.add(new_solution);

		int k = 1;

		AlgoritmoAprendizado.calculateKNeareastNeighbour(front, k);

		double highKNNValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.knn > highKNNValue){
				highKNNValue = solucao.knn;
				index = i;
			}
		}

		front.remove(index);

	}

}
