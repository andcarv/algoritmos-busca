package rank;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;

public class CombinacaoRank extends Rank {
	
	private ArrayList<Rank> combinacao = null;
	
	public CombinacaoRank(int m, ArrayList<Rank> c){
		super(m);
		combinacao = c;
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			solucao.combRank = new double[combinacao.size()];
		}
		
		int comb = 0;
		for (Iterator<Rank> iterator = combinacao.iterator(); iterator.hasNext();) {
			Rank r = (Rank) iterator.next();
			r.rankear(solucoes, comb++);
		}
		
		
		
		ArrayList<Solucao> atual = new ArrayList<Solucao>();
		ArrayList<Solucao> proxima = new ArrayList<Solucao>();
		
		
		atual.addAll(solucoes);
		
		int rank = 0;
		
		while(atual.size()>0){
			for (Iterator<Solucao> iter = atual.iterator(); iter.hasNext();) {
				Solucao solucao = iter.next();
				solucao.numDominacao = pareto.obterNumDomincaoRank(solucao, atual);
				if(solucao.numDominacao == 0){
					solucao.rank = rank;
				} else
					proxima.add(solucao);
			}
			atual.clear();
			atual.addAll(proxima);
			proxima.clear();
			rank++;
		}
		

	}

}
