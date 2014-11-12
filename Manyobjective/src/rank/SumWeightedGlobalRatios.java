package rank;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;

public class SumWeightedGlobalRatios extends Rank {
	
	public double globalMax[], globalMin[];
	
	public SumWeightedGlobalRatios(int m){
		super(m);
		
		globalMin = new double[m];
		globalMax = new double[m];
		for (int i = 0; i < globalMin.length; i++) {
			globalMin[i] = Double.POSITIVE_INFINITY;
			globalMax[i] = Double.NEGATIVE_INFINITY;
		}
		System.out.println("Rank: SGR");
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {

		for(int k = 0; k<m; k++){
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				solucao.rank = 0;
				globalMin[k] = Math.min(solucao.objetivos[k], globalMin[k]);
				globalMax[k] = Math.max(solucao.objetivos[k], globalMax[k]);
			}
		}
		
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			for(int k = 0; k<m; k++){
				double nratio = (solucao.objetivos[k]- globalMin[k])/(globalMax[k] - globalMin[k]);
				solucao.rank+=nratio;
			}
		}

	}

}
