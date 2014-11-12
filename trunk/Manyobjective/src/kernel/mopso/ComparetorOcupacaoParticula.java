package kernel.mopso;

import java.util.Comparator;


public class ComparetorOcupacaoParticula implements Comparator<Particula>{
	

	
	public int compare(Particula p1, Particula p2){
		if(p1.solucao.ocupacao<p2.solucao.ocupacao)
			return -1;
		else
			if(p1.solucao.ocupacao>p2.solucao.ocupacao)
				return 1;
			else
				return 0;
	}

}
