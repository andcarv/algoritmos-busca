package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;
import kernel.mopso.Particula;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class EscolherMetodoSigma extends EscolherLider {

	
	public EscolherMetodoSigma(){
		id = "sigma";
	}
	@Override
	public void escolherLideres(ArrayList<Particula> populacao, 	ArrayList<Solucao> lideres) {
		for (Iterator<Solucao> iter = lideres.iterator(); iter.hasNext();) {
			Solucao solucaotRepositorio =  iter.next();
			solucaotRepositorio.calcularSigmaVector();
		}
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			particula.solucao.calcularSigmaVector();
			escolherGlobalBestSigma(particula, lideres);
		}
		
		

	}
	
	

	/**
	 * M�todo que escolhe qual particula do repositorio sera escolhida como global best
	 * Escolhe a part�cula probabilisticamente atraves de uma roleta com os valores da
	 * dist�ncia Euclidiana dos sigmaVector
	 */
	public void escolherGlobalBestSigma(Particula particula, ArrayList<Solucao> repositorio){
		double melhorValor = Double.MAX_VALUE;
		Solucao gbest = null; 
		//Calcula o valor da distancia euclidia dos sigmaVector de cada particula do repositorio
		//e escolhe a menor
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			double temp = AlgoritmoAprendizado.distanciaEuclidiana(particula.solucao.sigmaVector, rep.sigmaVector);
			if(temp<melhorValor){
				melhorValor = temp;
				gbest = rep;
			}
		}

		particula.globalBest =  ((SolucaoNumerica)gbest).getVariaveis();
		particula.globalBestSolucao = gbest;
		
	}

}
