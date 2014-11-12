package rank;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;

public class AverageRank extends Rank {
	
	public AverageRank(int m){
		super(m);
		System.out.println("Rank: AR");
		ID = "AR";
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		int[][][] A = new int[solucoes.size()][solucoes.size()][m];
		
		double maiorRank = Double.NEGATIVE_INFINITY;
		double menorRank = Double.POSITIVE_INFINITY;

		calcularWinningScore(solucoes, A);

		

		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.rank = 0;
			for(int k = 0; k<m; k++){
				double rank = 0;
				for(int j = 0; j<solucoes.size(); j++){
					if(i!=j){
						if(A[i][j][k]==1)
							rank++;
					}
				}

				double rankObj = (solucoes.size()) - rank;
				solucaoi.rank+= rankObj;
				

			}
			
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
