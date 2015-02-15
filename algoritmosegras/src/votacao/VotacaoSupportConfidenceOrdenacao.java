package votacao;

import java.util.ArrayList;
import java.util.Collections;

import kernel.Classe;
import regra.ComparatorRegraSupportConfidence;
import regra.Regra;
import weka.core.Instance;
/**
 * CSA : Support-Confidence
 * Hybrid CSA/Laplace: Selects the best K rules in a Laplace manner, and reorders
 * both the best K CAR list and the original CAR list in a CSA fashion;
 */
public class VotacaoSupportConfidenceOrdenacao extends Votacao {

	public VotacaoSupportConfidenceOrdenacao(){
		System.out.println("Votação: Support Confidence Ordenado");
	}
	@Override
	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo=0,negativo=0;
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras, exemplo, classePositiva);
		
		ComparatorRegraSupportConfidence comp = new ComparatorRegraSupportConfidence();
		
		Collections.sort(regrasVotacaoPositiva, comp);
		Collections.sort(regrasVotacaoNegativa, comp);
				

		positivo = votarLaplaceMedia(regrasVotacaoPositiva, k);
		negativo = votarLaplaceMedia(regrasVotacaoNegativa, k);

		return positivo-negativo;		
	}

public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes) {
		
		ArrayList<Integer> classesMaisVotadas = new ArrayList<Integer>();
		int classeMaisVotada = 0;
		int[] classePontuacao = new int[classes.size()];		
		double k = exemplo.classAttribute().numValues();		
		ComparatorRegraSupportConfidence comp = new ComparatorRegraSupportConfidence();
		
		for(int i = 0; i < classes.size(); i++){
			ArrayList<Regra> regrasDaClasseQueVotamNoExemplo = new ArrayList<Regra>();
			obterRegrasVotamMultiClasse(regrasDaClasseQueVotamNoExemplo, regras, exemplo, classes.get(i).getNome());
			Collections.sort(regrasDaClasseQueVotamNoExemplo, comp);
			classePontuacao[i] += votarLaplaceMedia(regrasDaClasseQueVotamNoExemplo, k);
		}
		
		//verifica qual foi a mais votada
		for(int i = 0; i < (classes.size() - 1); i++){
			if(classePontuacao[i + 1] > classePontuacao[i]){
				classeMaisVotada = i + 1; 
			}			
		}
		
		classesMaisVotadas.add(classeMaisVotada);
		
		//verifica se houve empate entre as classes mais votada e preeche o array classesMaisVotadas em caso positivo
		for(int i = 0; i < classes.size(); i++){
			if( (classeMaisVotada != i) && (classePontuacao[i] == classePontuacao[classeMaisVotada]) ){
				classesMaisVotadas.add(i);
			}			
		}
		
		return classesMaisVotadas;
	}

}
