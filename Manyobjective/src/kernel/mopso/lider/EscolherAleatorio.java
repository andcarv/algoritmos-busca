package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;
import solucao.SolucaoNumerica;

import kernel.mopso.Particula;

public class EscolherAleatorio extends EscolherLider {
	
	public EscolherAleatorio(){
		id = "aleatorio";
	}
	
	
	public void escolherLideres(ArrayList<Particula> populacao, ArrayList<Solucao> lideres){
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherAleatoria(particula, lideres);
		}
	}
	
	
	public void escolherAleatoria(Particula particula, ArrayList<Solucao> repositorio){
		Solucao solucao = repositorio.get((int)Math.random()*repositorio.size());
		
		particula.globalBest = ((SolucaoNumerica)solucao).getVariaveis();
		particula.globalBestSolucao = solucao;
		
	}

}
