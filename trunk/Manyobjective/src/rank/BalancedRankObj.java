package rank;

import java.util.ArrayList;

import solucao.Solucao;

public class BalancedRankObj extends Rank {
	
	public BalancedRankObj(int m){
		super(m);
		System.out.println("Rank: BR");
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		int[][][] A = new int[solucoes.size()][solucoes.size()][m];
		
		
		
		double maiorRankGeral = Double.NEGATIVE_INFINITY;
		double menorRankGeral = Double.POSITIVE_INFINITY;
		
		double maiorDiff = Double.NEGATIVE_INFINITY;

		calcularWinningScore(solucoes, A);
		
		

		

		//Calcula do average rank ponderado
		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.rank = 0;
			double maiorObj = Double.NEGATIVE_INFINITY;
			double menorObj = Double.POSITIVE_INFINITY;
			for(int k = 0; k<m; k++){
				double rank = 0;
				for(int j = 0; j<solucoes.size(); j++){
					if(i!=j){
						if(A[i][j][k]==1)
							rank++;
					}
				}

				double rankObj = (solucoes.size()) - rank;
				//Calculo do AR
				solucaoi.rank+= rankObj;
				//Defini��o dos maiores e menores rankings
				if (solucaoi.objetivos[k] >maiorObj)
					maiorObj = solucaoi.objetivos[k];
				if (solucaoi.objetivos[k] <menorObj)
					menorObj = solucaoi.objetivos[k];
			}


			solucaoi.diff = maiorObj - menorObj;
			solucaoi.rank = solucaoi.rank;
			
			maiorDiff = Math.max(maiorDiff,solucaoi.diff);
			
				
		}
		
		
		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			double pond = (solucaoi.diff/maiorDiff);
			solucaoi.rank = solucaoi.rank*pond;
			
			maiorRankGeral = Math.max(maiorRankGeral, solucaoi.rank);
			menorRankGeral = Math.min(menorRankGeral, solucaoi.rank);
		}
		
		
		
		if(c!=-1)
		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			 
			solucaoi.rank = solucaoi.rank/maiorRankGeral;
			solucaoi.combRank[c] = solucaoi.rank;
		}

	}

}
