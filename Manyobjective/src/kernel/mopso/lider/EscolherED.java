package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.mopso.Particula;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;

public class EscolherED extends EscolherLider {
		//ED= Euclidean Distance
		//primieiro metodo, escolhe o lider mais proximo da particula
	public EscolherED(){
		id = "ED";
	}

	@Override
	public void escolherLideres(ArrayList<Particula> populacao,	ArrayList<Solucao> lideres) {
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherGlobalBestED(particula, lideres); //chama a funcao com uma particula por
												//vez e o conjunto de lideres
		}
	}
	
	public void escolherGlobalBestED(Particula particula, ArrayList<Solucao> repositorio){
		
		double melhorED = Double.MAX_VALUE;
		Solucao gbest = null;
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {//percorre o repositorio
			Solucao solucao = iter.next();
			//distanciaEuclidiana(double[] vetor1, double[] vetor2)
			//calcula a distancia entre 
			double ED = AlgoritmoAprendizado.distanciaEuclidiana(solucao.objetivos, particula.solucao.objetivos);  //calcularED(particula.solucao, solucao);
			
			if(ED<=melhorED){
				melhorED= ED;
				gbest = solucao;
			}							
		}		
		
		particula.globalBest = ((SolucaoNumerica)gbest).getVariaveis();
		particula.globalBestSolucao = gbest;
	}	
}
