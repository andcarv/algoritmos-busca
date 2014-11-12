package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;
import kernel.mopso.Particula;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class EscolherIdeal extends EscolherLider {
	
	private int m;
	
	public EscolherIdeal(int m){
		this.m = m;
		id = "ideal";
	}

	@Override
	public void escolherLideres(ArrayList<Particula> populacao,	ArrayList<Solucao> lideres) {
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherGlobalBestIdeal(particula, lideres);	
		}
	}
	
	
	public void escolherGlobalBestIdeal(Particula particula, ArrayList<Solucao> repositorio){
		double[] ideal = new double[m];
		for (int i = 0; i < ideal.length; i++) {
			ideal[i] = Double.POSITIVE_INFINITY;	
		}
		
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			for(int j = 0; j<m;j++){
				if(rep.objetivos[j]<ideal[j])
					ideal[j] = rep.objetivos[j];
			}
		}
		
		
		double melhorValor = Double.MAX_VALUE;
		Solucao gbest = null; 
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			double temp = AlgoritmoAprendizado.distanciaEuclidiana(ideal, rep.objetivos);
			if(temp<melhorValor){
				melhorValor = temp;
				gbest = rep;
			}
		}

		particula.globalBest =  ((SolucaoNumerica)gbest).getVariaveis();
		particula.globalBestSolucao = gbest;
	}
	
	
	//Pior que o ideal - método acima
	public void escolherGlobalBestIdeal2(Particula particula, ArrayList<Solucao> repositorio){
		double[] ideal = new double[m];
		Solucao[] melhores = new Solucao[m+1];
		
		for (int i = 0; i < ideal.length; i++) {
			ideal[i] = Double.POSITIVE_INFINITY;
		}
		
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			for(int j = 0; j<m;j++){
				if(rep.objetivos[j]<ideal[j]){
					ideal[j] = rep.objetivos[j];
					melhores[j] = rep;
				}
			}
		}
		
		
		double melhorValor = Double.MAX_VALUE;
		Solucao gbest = null; 
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			double temp = AlgoritmoAprendizado.distanciaEuclidiana(ideal, rep.objetivos);
			if(temp<melhorValor){
				melhorValor = temp;
				gbest = rep;
			}
		}
		
		melhores[melhores.length-1] = gbest;

				
		int ordem = (int)Math.ceil(Math.log10(melhores.length));
		int indice1 = (int)(Math.random()*(Math.pow(10, ordem))%melhores.length);
		int indice2 = (int)(Math.random()*(Math.pow(10, ordem))%melhores.length);
		Solucao particula1 = melhores[indice1];
		Solucao particula2 = melhores[indice2];
		if(particula1.crowdDistance>particula2.crowdDistance){
			particula.globalBest =  ((SolucaoNumerica)particula1).getVariaveis();
			particula.globalBestSolucao = particula1;
		}
		else{
			particula.globalBest = ((SolucaoNumerica)particula2).getVariaveis();
			particula.globalBestSolucao = particula2;
		}
	}

}
