package indicadores;


import java.util.Comparator;

public class ComparetorObjetivoPontoFronteira implements Comparator<PontoFronteira>{
	
	public int objetivoVez;
	
	public ComparetorObjetivoPontoFronteira(int i){
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
