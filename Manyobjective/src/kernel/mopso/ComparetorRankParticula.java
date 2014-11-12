package kernel.mopso;

import java.util.Comparator;

import solucao.SolucaoNumerica;


public class ComparetorRankParticula implements Comparator<Particula>{
	
	public int compare(Particula p1, Particula p2){
		SolucaoNumerica s1 = p1.solucao;
		SolucaoNumerica s2 = p2.solucao;
		if(s1.rank<s2.rank)
			return -1;
		else
			if(s1.rank>s2.rank)
				return 1;
			else
				return 0;
	}
	

}
