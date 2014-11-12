package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.Classe;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a votação simples, com a adição de um ponto para cada voto da regra.
 * @author André de Carvalho
 *
 */
public class VotacaoSimples extends Votacao {

	public VotacaoSimples(){
		System.out.println("Votação: Simples");
	}
	
	/**
	 * Método de votação simples. Todas regras votam e um ponto para cada voto.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo, String classePositiva) {
		int positivo = 0;
		int negativo = 0;

		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				regra.votou = true;
				if(regra.cabeca == regra.classe.indexOfValue(classePositiva))
					positivo++;
				else 
					negativo++;	
			}	
		}
		
		return positivo-negativo;
	}

	/**
	 * Método de votação simples multiclasse. Todas regras votam e um ponto para cada voto.
	 * @author Matheus Rosendo
	 * @return a classe mais votada pelo conjunto de regras 
	 */
	public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes) {
		int[] classePontuacao = new int[classes.size()];
		int classeMaisVotada = 0;
		
		ArrayList<Integer> classesMaisVotadas = new ArrayList<Integer>();//será apenas uma em caso de não haver empate
		
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			// em caso da regra atual cobrir o exemplo adiciona-se um ponto para ela no array classePontuacao
			if(b){
				classePontuacao[regra.cabeca]++; 
			}	
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
