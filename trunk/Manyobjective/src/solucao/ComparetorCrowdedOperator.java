package solucao;

import java.util.Comparator;

/**
 * Comparetor que equivale ao operador <n proposto no artigo do nsga2
 * @author Andre
 *
 */
public class ComparetorCrowdedOperator implements Comparator<Solucao>{
	
	public int compare(Solucao s1, Solucao s2){
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
