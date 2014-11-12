package kernel.mopso.lider;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.mopso.Particula;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class EscolherNWSum extends EscolherLider {
		
	public EscolherNWSum(){
		id = "NWSum";
	}

	@Override
	public void escolherLideres(ArrayList<Particula> populacao,	ArrayList<Solucao> lideres) {
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			escolherGlobalBestWSum(particula, lideres);
		}
	}
	
	public void escolherGlobalBestWSum(Particula particula, ArrayList<Solucao> repositorio){
		
		double melhorWsum = 0;
		Solucao gbest = null;
		for (Iterator<Solucao> iter = repositorio.iterator(); iter.hasNext();) {
			Solucao solucao = iter.next();
			//quanto menor o wsum melhor
			//se o wsum anterior for maior q o da solucao atual, substitui
			double wsum = calcularWSum(particula.solucao, solucao);
			
			if(wsum>=melhorWsum){
				melhorWsum= wsum;
				gbest = solucao;
			}							
		}		
		
		particula.globalBest = ((SolucaoNumerica)gbest).getVariaveis();
		particula.globalBestSolucao = gbest;

		//System.out.println("\n\n\n");
	}
	public double calcularWSum(Solucao solucao, Solucao GBest){
		int objetivo, k;
		//m - número de objetivos do problema
		int m=solucao.objetivos.length;
		double res1=0,resParc=0,WSum=0;
		
		for(objetivo=0;objetivo<m;objetivo++){
			res1=0;
			for(k=0;k<m;k++){
				res1+=solucao.objetivos[k];
			}
			resParc=solucao.objetivos[objetivo]/res1;
			
			WSum+=resParc*GBest.objetivos[objetivo];
		}
		return WSum;
	}
	
}
