package solucao;

import indicadores.PontoFronteira;

import java.util.Comparator;

public class ComparetorRankPF implements Comparator<PontoFronteira>{
	
	public int compare(PontoFronteira s1, PontoFronteira s2){
		if(s1.rank<s2.rank)
			return -1;
		else
			if(s1.rank>s2.rank)
				return 1;
			else
				return 0;
	}

}
