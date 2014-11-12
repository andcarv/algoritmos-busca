package kernel.mopso;

import java.util.Comparator;


public class ComparetorDominacaoParticula implements Comparator<Particula>{
	

	
	public int compare(Particula p1, Particula p2){
		if(p1.solucao.numDominacao<p2.solucao.numDominacao)
			return -1;
		else
			if(p1.solucao.numDominacao>p2.solucao.numDominacao)
				return 1;
			else
				return 0;
	}

}
