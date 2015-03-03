package util;

import solucao.SolucaoNumerica;

public class DistribuirPontos {
	
	
	/*
	 * Função que distribui m intervalos em numSol pedaços iguais
	 */
	public static void main(String[] args) {
		int numSol = 300;
		double[][] vectors = new double[numSol][3]; 	
		int m = 2;
		for(int i = 0; i<m; i++){
			for(int j = 0; j<m; j++){
			   if(i == j)	
				   vectors[i][j] = 1;
			   else
				   vectors[i][j] = 0;
			}
		}
		double log_intervalos = (Math.log10(numSol)/2);
		int inter = (int) Math.ceil(Math.pow(10, log_intervalos));
		System.out.println("Intervalos = " + inter);
		int contador[] = new int[m];
		double pedaco = 1.0/inter;
		int k = m;
		int indice = 0;
		while(k < numSol){

			for (int i = 1; i < m+1; i++) {
				double newVal = contador[i-1]*pedaco;
				vectors[k][i-1] = newVal;
				//System.out.print(newVal + "\t");
			}
            //System.out.println();

    		while(indice!=m-1 && contador[indice+1] < inter-1)
				indice++;

			while(indice>0 && contador[indice] == inter-1){
				contador[indice] = 0;
				indice--;
			} 
			contador[indice]++;

			k++;
		}
		
		for(int i = 0; i<numSol; i++){
			for(int j = 0; j<m; j++){
			   System.out.print(vectors[i][j] + "\t");
			}
			System.out.println();
		}

	}

}
