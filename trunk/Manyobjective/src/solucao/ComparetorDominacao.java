package solucao;

import java.util.Comparator;

public class ComparetorDominacao implements Comparator<Solucao>{
	
	public int compare(Solucao s1, Solucao s2){
		if(s1.numDominacao<s2.numDominacao)
			return -1;
		else
			if(s1.numDominacao>s2.numDominacao)
				return 1;
			else
				return 0;
	}
	

}
