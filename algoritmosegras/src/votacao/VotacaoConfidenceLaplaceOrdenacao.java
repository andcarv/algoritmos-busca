package votacao;

import java.util.ArrayList;
import java.util.Collections;

import kernel.Classe;
import regra.ComparatorRegraLaplace;
import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a vota��o por confian�a com a sele��o das regras atrav�s do valor de Laplace. Implementa��o
 * diferente, as regras s�o ordenadas e depois selecionadas.
 * @author Andr� de Carvalho
 *
 */
public class VotacaoConfidenceLaplaceOrdenacao extends Votacao {

	public VotacaoConfidenceLaplaceOrdenacao(){
		System.out.println("Vota��o: Confidence Laplace com ordena��o");
	}
	
	/**
	 * M�todo que restringe a vota��o � somente k regras por classe, onde k representa o
	 * n�mero de classes do problema. Inicialmente ordena todas as regras, depois executa a vota��o.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0;
		double negativo = 0;
		
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		
		obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras, exemplo, classePositiva);
		
		ComparatorRegraLaplace comp = new ComparatorRegraLaplace();
		
		Collections.sort(regrasVotacaoPositiva, comp);
		Collections.sort(regrasVotacaoNegativa, comp);
		
		//Vota��o das k melhores regras para cada classe
		
		positivo = votarConfidenceMedia(regrasVotacaoPositiva, k);
		negativo = votarConfidenceMedia(regrasVotacaoNegativa, k);

		return positivo-negativo;
	}

	@Override
	public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes) {
		
		ArrayList<Integer> classesMaisVotadas = new ArrayList<Integer>();
		int classeMaisVotada = 0;
		int[] classePontuacao = new int[classes.size()];		
		double k = exemplo.classAttribute().numValues();		
		ComparatorRegraLaplace comp = new ComparatorRegraLaplace();
		
		for(int i = 0; i < classes.size(); i++){
			ArrayList<Regra> regrasDaClasseQueVotamNoExemplo = new ArrayList<Regra>();
			obterRegrasVotamMultiClasse(regrasDaClasseQueVotamNoExemplo, regras, exemplo, classes.get(i).getNome());
			Collections.sort(regrasDaClasseQueVotamNoExemplo, comp);
			classePontuacao[i] += votarConfidenceMedia(regrasDaClasseQueVotamNoExemplo, k);
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
