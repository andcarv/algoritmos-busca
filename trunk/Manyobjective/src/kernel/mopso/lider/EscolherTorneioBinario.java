package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;
import solucao.SolucaoNumerica;

import kernel.mopso.Particula;

public class EscolherTorneioBinario extends EscolherLider {
	
	public EscolherTorneioBinario(){
		id = "torneio";
	}
	
	public void escolherLideres(ArrayList<Particula> populacao, ArrayList<Solucao> lideres){
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherGlobalBestBinario(particula, lideres);
		}
	}
	
	
	public void escolherGlobalBestBinario(Particula particula, ArrayList<Solucao> repositorio){
		int ordem = (int)Math.ceil(Math.log10(repositorio.size()));
		int indice1 = (int)(Math.random()*(Math.pow(10, ordem))%repositorio.size());
		int indice2 = (int)(Math.random()*(Math.pow(10, ordem))%repositorio.size());
		Solucao solucao1 = repositorio.get(indice1);
		Solucao solucao2 = repositorio.get(indice2);
		if(solucao1.crowdDistance>solucao2.crowdDistance){
			particula.globalBest = ((SolucaoNumerica)solucao1).getVariaveis();
			particula.globalBestSolucao = solucao1;
		}
		else{
			particula.globalBest = ((SolucaoNumerica)solucao2).getVariaveis();
			particula.globalBestSolucao = solucao2;
		}
	}

}
