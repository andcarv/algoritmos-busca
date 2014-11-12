package objetivo;

import solucao.Solucao;
import solucao.SolucaoBinaria;

public class CriterioFuncional extends Criterios {
	
	String criterio = null;

	public CriterioFuncional(String arquivoArcos, String c){
		super(arquivoArcos);
		criterio = c;
	}
	
	@Override
	/**
	 * Calcula o valor do objetivo da solucao a partir da matriz contendo a cobertura de cada criterio
	 */
	public double calcularObjetivo(Solucao solucao) {
		SolucaoBinaria sol = (SolucaoBinaria) solucao;
		String[] stringBinaria = sol.getVariaveis();
		//Percorre os casos de teste da solucao e marcar todos os criterios que a solucao cobriu
		int[] vetorCombinado = new int[dados[0].length];
		for (int i = 0; i < stringBinaria.length; i++) {
			if(stringBinaria[i].equals("1")){
				int[] vetorTesteI = dados[i];
				OR(vetorCombinado, vetorTesteI, vetorCombinado);
			}
		}
		
		//Calcula quantos criterios foram cobertos pela solucao em relação ao total de critérios.
		double cont = 0;
		for (int i = 0; i < vetorCombinado.length; i++) {
			if(vetorCombinado[i] == 1)
				cont++;
		}
		
		double retorno = cont/(double)vetorCombinado.length;
		return retorno;
	}
	
	public static void main(String[] args) {
		SolucaoBinaria bin = new SolucaoBinaria(5, 4);
		
		bin.setVariavel(0, "1");
		bin.setVariavel(1, "1");
		bin.setVariavel(2, "1");
		bin.setVariavel(4, "0");
		bin.setVariavel(3, "0");
		
		String arquivoArcos = "E:\\Andre\\ES\\arcos_schedule2.txt";
		String c = "arcos";
		CriterioFuncional crit = new CriterioFuncional(arquivoArcos, c);
		crit.calcularObjetivo(bin);
	}
	
	

}
