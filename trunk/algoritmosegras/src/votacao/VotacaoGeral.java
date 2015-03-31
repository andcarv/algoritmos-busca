package votacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kernel.Classe;
import regra.ComparatorConfianca;
import regra.ComparatorEspecificidade;
import regra.ComparatorRegraSupportConfidence;
import regra.ComparatorSensitividade;
import regra.Regra;
import weka.core.Instance;

public class VotacaoGeral extends Votacao {
	private Comparator<Regra> comparatorOrdenacao;
	private int votacaoM;
	private static final int ORDENACAO_CSA = 0;
	private static final int ORDENACAO_SENSITIVIDADE = 1;
	private static final int ORDENACAO_ESPECIFICIDADE = 2;

	private static final int VOTACAO_LAPLACE_MEDIA = 10;
	private static final int VOTACAO_SENSITIVIDADE_MEDIA = 11;
	private static final int VOTACAO_ESPECIFICIDADE_MEDIA = 12;

	public VotacaoGeral(int ordenacao, int votacaoMedia) {
		switch (ordenacao) {
		case ORDENACAO_CSA:
			comparatorOrdenacao = new ComparatorRegraSupportConfidence();
			break;

		case ORDENACAO_SENSITIVIDADE:
			comparatorOrdenacao = new ComparatorSensitividade();
			break;

		case ORDENACAO_ESPECIFICIDADE:
			comparatorOrdenacao = new ComparatorEspecificidade();
			break;

		default:
			comparatorOrdenacao = new ComparatorConfianca();
		}
		votacaoM = votacaoMedia;
	}

	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0, negativo = 0;
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras,
				exemplo, classePositiva);

		Collections.sort(regrasVotacaoPositiva, comparatorOrdenacao);
		Collections.sort(regrasVotacaoNegativa, comparatorOrdenacao);

		positivo = getResultadotVotacao(regrasVotacaoPositiva, k);
		negativo = getResultadotVotacao(regrasVotacaoNegativa, k);

		return positivo - negativo;
	}

	@Override
public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes) {
		
		ArrayList<Integer> classesMaisVotadas = new ArrayList<Integer>();
		int classeMaisVotada = 0;
		int[] classePontuacao = new int[classes.size()];		
		double k = exemplo.classAttribute().numValues();		
		
		
		for(int i = 0; i < classes.size(); i++){
			ArrayList<Regra> regrasDaClasseQueVotamNoExemplo = new ArrayList<Regra>();
			obterRegrasVotamMultiClasse(regrasDaClasseQueVotamNoExemplo, regras, exemplo, classes.get(i).getNome());
			Collections.sort(regrasDaClasseQueVotamNoExemplo, comparatorOrdenacao);
			classePontuacao[i] += getResultadotVotacao(regrasDaClasseQueVotamNoExemplo, k);
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
	private double getResultadotVotacao(ArrayList<Regra> regra, double k) {
		double valor=0;
		switch (votacaoM) {
		case VOTACAO_LAPLACE_MEDIA:
			valor = votarLaplaceMedia(regra, k);
			break;
		case VOTACAO_SENSITIVIDADE_MEDIA:
			valor = votarSensitividadeMedia(regra, k);
			break;
		case VOTACAO_ESPECIFICIDADE_MEDIA:
			valor = votarEspecifidadeMedia(regra, k);
			break;
		}
		return valor;
	}

}
