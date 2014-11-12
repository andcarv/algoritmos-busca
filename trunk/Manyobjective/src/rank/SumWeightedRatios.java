package rank;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;

public class SumWeightedRatios extends Rank {
	
	public SumWeightedRatios(int m){
		super(m);
		System.out.println("Rank: SR");
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		double mimObj[] = new double[m];
		double maxObj[] = new double[m];
		for (int i = 0; i < maxObj.length; i++) {
			mimObj[i] = Double.POSITIVE_INFINITY;
			maxObj[i] = Double.NEGATIVE_INFINITY;
		}
		for(int k = 0; k<m; k++){
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				solucao.rank = 0;
				mimObj[k] = Math.min(solucao.objetivos[k], mimObj[k]);
				maxObj[k] = Math.max(solucao.objetivos[k], maxObj[k]);
			}
		}
		
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			for(int k = 0; k<m; k++){
				double nratio = (solucao.objetivos[k]- mimObj[k])/(maxObj[k] - mimObj[k]);
				solucao.rank+=nratio;
			}
		}

	}

}
