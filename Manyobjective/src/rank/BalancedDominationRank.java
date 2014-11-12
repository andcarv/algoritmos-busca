package rank;

import java.util.ArrayList;

import solucao.Solucao;

public class BalancedDominationRank extends Rank {
	
	private RankDominancia dom = null;
	
	public BalancedDominationRank(int m){
		super(m);
		System.out.println("Rank: BDR");
		dom = new RankDominancia(m);
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		int[][][] A = new int[solucoes.size()][solucoes.size()][m];

		calcularWinningScore(solucoes, A);
		
		
		dom.setPareto(pareto);
		dom.rankear(solucoes, -1);

		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.balanceamentoRank = (solucaoi.rank+1)/(dom.rankMaximo+1);
			solucaoi.rank = 0;
		}

		//Calcula do average rank ponderado
		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			double maiorRank = Double.NEGATIVE_INFINITY;
			double menorRank = Double.POSITIVE_INFINITY;
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
				if (rankObj >maiorRank)
					maiorRank = rankObj;
				if(rankObj<menorRank)
					menorRank = rankObj;
			}

			//Calculo da pondera��o
			double diff = solucaoi.balanceamentoRank*((maiorRank - menorRank)/solucoes.size());
			solucaoi.rank = solucaoi.rank * diff;	
			if(c!=-1)
				solucaoi.combRank[c] = solucaoi.rank;
		}

	}

}
