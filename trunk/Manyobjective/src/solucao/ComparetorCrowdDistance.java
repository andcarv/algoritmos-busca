package solucao;

import java.util.Comparator;

public class ComparetorCrowdDistance implements Comparator<Solucao>{
	
	public int compare(Solucao s1, Solucao s2){
		if(s1.crowdDistance>s2.crowdDistance)
			return -1;
		else
			if(s1.crowdDistance<s2.crowdDistance)
				return 1;
			else
				return 0;
	}
	

}
