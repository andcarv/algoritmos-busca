package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import kernel.mopso.Particula;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;

public class EscolherNGN extends EscolherLider {
		//NGN= Nearest good neighbor
		//segundo metodo, escolhe o lider que tem a menor soma da distancia ate a particula e da distancia ate a origem
	public EscolherNGN(){
		id = "NGN";
	}

	@Override
	public void escolherLideres(ArrayList<Particula> populacao,	ArrayList<Solucao> lideres) {
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherGlobalBestNGN(particula, lideres); //chama a funcao com uma particula por
												//vez e o conjunto de lideres
		}
	}
	
	public void escolherGlobalBestNGN(Particula particula, ArrayList<Solucao> repositorio){
		
		double melhorNGN = Double.MAX_VALUE;
		Solucao gbest = null;
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {//percorre o repositorio
			Solucao solucao = iter.next();
			//distanciaEuclidiana(double[] vetor1, double[] vetor2)
			//quanto menor o wsum melhor
			//se o wsum anterior for maior q o da solucao atual, substitui
			double[] ideal = new double[solucao.objetivos.length];
			Arrays.fill(ideal, 0); //preenche todas as posicoes do array com 0 (ponto ideal)
			//calcula a distancia entre 
			double NGN = AlgoritmoAprendizado.distanciaEuclidiana(ideal, solucao.objetivos)+AlgoritmoAprendizado.distanciaEuclidiana(particula.solucao.objetivos, solucao.objetivos);  //calcularED(particula.solucao, solucao);
			
			if(NGN<melhorNGN){
				melhorNGN= NGN;
				gbest = solucao;
				//System.out.println("tt: "+NGN+" - ideal: "+AlgoritmoAprendizado.distanciaEuclidiana(ideal, solucao.objetivos)+" - part: "+AlgoritmoAprendizado.distanciaEuclidiana(particula.solucao.objetivos, solucao.objetivos)+" - part: ["+particula.solucao.objetivos[0]+","+particula.solucao.objetivos[1]+"]"+" - lider: ["+solucao.objetivos[0]+","+solucao.objetivos[1]+"]");
			}							
		}		
		//System.out.println("---------------------------------------------------");
		particula.globalBest = ((SolucaoNumerica)gbest).getVariaveis();
		particula.globalBestSolucao = gbest;

		//System.out.println("\n\n\n");
	}
	/*public double calcularED(Solucao solucao, Solucao GBest){
		int objetivo, k;
		//m - número de objetivos do problema
		int m=solucao.objetivos.length;
		double res1=0,resParc=0,ED=0;
		
		for(objetivo=0;objetivo<m;objetivo++){
			res1=0;
			for(k=0;k<m;k++){
				res1+=solucao.objetivos[k];
			}
			resParc=solucao.objetivos[objetivo]/res1;
			
			ED+=resParc*GBest.objetivos[objetivo];
		}
		return ED;
	}*/
	
}
