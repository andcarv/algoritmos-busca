package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.Classe;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a votação por confiança.
 * @author André de Carvalho
 *
 */
public class VotacaoConfidence extends Votacao {

	public VotacaoConfidence(){
		System.out.println("Votação: Confidence");
	}
	
	/**
	 * Método que implementa a votação por confiança. Todas as regras votam e o ponto adicionado é a confiança 
	 * da regra.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0;
		double negativo = 0;

		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				regra.votou = true;
				if(regra.cabeca == regra.classe.indexOfValue(classePositiva))
					positivo+= regra.getConfidence();
				else 
					negativo+= regra.getConfidence();	
			}	
		}
		
		return positivo-negativo;
	}

	@Override
	public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes) {
		// TODO Auto-generated method stub
		return null;
	}

}
