package objetivo;

import solucao.Solucao;
import solucao.SolucaoBinaria;

public class TamanhoSolucao extends FuncaoObjetivo {
	
	public double calcularObjetivo(Solucao solucao) {
		int tamanho = 0;
		if(solucao.isBinaria()){
			SolucaoBinaria bin = (SolucaoBinaria) solucao;
			for (int i = 0; i < bin.getVariaveis().length; i++) {
				String valor = bin.getVariavel(i);
				if(valor.equals("1"))
					tamanho++;
			}
		}
		
		return tamanho;
	}

}

	