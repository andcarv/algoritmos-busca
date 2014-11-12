package solucao;

import indicadores.PontoFronteira;

import java.util.Comparator;

public class ComparetorHipervolume implements Comparator<PontoFronteira>{
	
	public int compare(PontoFronteira s1, PontoFronteira s2){
		if(s1.objetivosAcimaMedia<s2.objetivosAcimaMedia)
			return -1;
		else
			if(s1.objetivosAcimaMedia>s2.objetivosAcimaMedia)
				return 1;
			else{
				if(s1.soma>s2.soma)
					return -1;
				else
					if(s1.soma<s2.soma)
						return 1;
					else
						return 0;
			}
	}
	

}
