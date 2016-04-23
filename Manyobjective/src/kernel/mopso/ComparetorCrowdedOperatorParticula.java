package kernel.mopso;

import java.util.Comparator;

import solucao.Solucao;

/**
 * Comparetor que equivale ao operador <n proposto no artigo do nsga2
 * @author Andre
 *
 */
public class ComparetorCrowdedOperatorParticula implements Comparator<Particula>{
	
	public int compare(Particula p1, Particula p2){
		Solucao s1 = p1.solucao;
		Solucao s2 = p2.solucao;
		if(s1.rank<s2.rank)
			return -1;
		else
			if(s1.rank>s2.rank)
				return 1;
			else{
				if(s1.crowdDistance>s2.crowdDistance)
					return -1;
				else
					if(s1.crowdDistance<s2.crowdDistance)
						return 1;
					else return 0;
						
			}
				
	}
	

}
