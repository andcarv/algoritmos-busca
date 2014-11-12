package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;
import kernel.mopso.Particula;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class EscolherOposto extends EscolherLider {
	
	public EscolherOposto(){
		id = "oposto";
	}

	@Override
	public void escolherLideres(ArrayList<Particula> populacao,	ArrayList<Solucao> lideres) {
		for (Iterator<Solucao> iter = lideres.iterator(); iter.hasNext();) {
			Solucao solucaotRepositorio =  iter.next();
			solucaotRepositorio.setVetorObjetivosMedio();
		}
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherGlobalOposto(particula, lideres);	
		}

	}
	
	
	//Metodo que o lider escolhido leva a particula para o centro da fronteira de Pareto
	public void escolherGlobalOposto(Particula particula, ArrayList<Solucao> repositorio){	
		//Calcula um vetor de objetivos medio - Valor do objetivo menos a media dos objetivos
		particula.solucao.setVetorObjetivosMedio();
		double[] vetorZero = new double[particula.solucao.objetivos.length];
		double melhorValor = Double.MAX_VALUE;
			
		Solucao gbest = null; 
		//Calcula o valor da distancia euclidia dos sigmaVector de cada particula do repositorio
		//e escolhe a menor
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			double[] vetorSoma = new double[particula.solucao.objetivosMedio.length];
			//Calcula a soma dos vetores medios da solucao entre todos os vetores do repositorio
			for (int i = 0; i < vetorSoma.length; i++) {
				vetorSoma[i] = particula.solucao.objetivosMedio[i] + rep.objetivosMedio[i];
			}
			//Escolhe o vetor que leva a particula mais proxima ao centro - Diferenca entre os valores objetivos igual a zero
			double temp = AlgoritmoAprendizado.distanciaEuclidiana(vetorSoma, vetorZero);
			if(temp<melhorValor){
				melhorValor = temp;
				gbest = rep;
			}
			
		}
		
		particula.globalBestSolucao = gbest;
		particula.globalBest =  ((SolucaoNumerica)gbest).getVariaveis();
		
	}

}
