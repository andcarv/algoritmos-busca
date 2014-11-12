package rank;

import java.util.ArrayList;

import pareto.FronteiraPareto;

import solucao.Solucao;

public abstract class Rank {
	
	public int m;
	
	public String ID;
	
	protected FronteiraPareto pareto = null;
	
	public Rank(int m){
		this.m = m;
	}
	
	
	public abstract void rankear(ArrayList<Solucao> solucoes, int i);
	
	
	public void setPareto(FronteiraPareto p){
		pareto = p;
	}
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public void calcularWinningScore(ArrayList<Solucao> solucoes, int[][][] A) {
		for(int i = 0; i<solucoes.size()-1; i++){
			Solucao solucaoi = solucoes.get(i);
			for(int j = i+1; j<solucoes.size(); j++){
				Solucao solucaoj =  solucoes.get(j);
				for(int k = 0; k<m; k++){
					if(solucaoi.objetivos[k]<solucaoj.objetivos[k]){
						A[i][j][k] = 1;
						A[j][i][k] = -1;
					} else {
						if(solucaoi.objetivos[k]>solucaoj.objetivos[k]){
							A[i][j][k] = -1;
							A[j][i][k] = 1;
						} else {
							A[i][j][k] = 0;
							A[j][i][k] = 0;
						}
					}
				}
			}
		}
	}

}
