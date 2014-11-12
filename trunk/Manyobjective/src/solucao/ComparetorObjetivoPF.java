package solucao;

import indicadores.PontoFronteira;

import java.util.Comparator;

public class ComparetorObjetivoPF implements Comparator<PontoFronteira>{
	
	public int objetivoVez;
	
	public ComparetorObjetivoPF(int i){
		objetivoVez = i;
	}
	
	public int compare(PontoFronteira s1, PontoFronteira s2){
		double objetivo1 = s1.objetivos[objetivoVez];
		double objetivo2 = s2.objetivos[objetivoVez];
		if(objetivo1<objetivo2)
			return -1;
		else
			if(objetivo1>objetivo2)
				return 1;
			else
				return 0;
	}

}
