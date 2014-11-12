package rank;

import java.util.ArrayList;
import java.util.Iterator;
import solucao.Solucao;

public class GB extends Rank {
	
	private double fator;
	
	public GB(int m, String maxmim){
		super(m);
		System.out.println("Rank: GB");
		
		if(maxmim.equals("-"))
			fator = -1;
		else
			fator =1;
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		
		double maiorRank = Double.NEGATIVE_INFINITY;
		double menorRank = Double.POSITIVE_INFINITY;

		
		
		double[] gbest = new double[m];

		for (int i = 0; i < gbest.length; i++) {
			double melhorObj = -1;
			if(fator ==1)
				melhorObj = Double.NEGATIVE_INFINITY;
			else
				melhorObj = Double.POSITIVE_INFINITY;
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				if(solucao.objetivos[i]*fator>melhorObj*fator){
					melhorObj = solucao.objetivos[i];
				}	
			}
			gbest[i] = melhorObj;
		}

		

		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.rank = distanciaEuclidiana(gbest, solucaoi.objetivos);
			
			
			maiorRank = Math.max(maiorRank, solucaoi.rank);
			menorRank = Math.min(menorRank, solucaoi.rank);
			
			
		}
		
		
		if(c!=-1)
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				solucao.rank = solucao.rank/maiorRank;
				solucao.combRank[c] = solucao.rank;
			}
			

	}

}
