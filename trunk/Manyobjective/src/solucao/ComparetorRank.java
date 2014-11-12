package solucao;

import java.util.Comparator;

public class ComparetorRank implements Comparator<Solucao>{
	
	public int compare(Solucao s1, Solucao s2){
		if(s1.rank<s2.rank)
			return -1;
		else
			if(s1.rank>s2.rank)
				return 1;
			else
				return 0;
	}
	

}
