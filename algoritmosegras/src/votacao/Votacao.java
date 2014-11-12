package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.Classe;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe abstrata que representa um método de votação das regras. Cada método de votação deve exentender esta classe
 * e implementar o método de votação.
 * @author André de Carvalho
 *
 */
public abstract class Votacao {
	
	/**
	 * Método abstrato de votação
	 * @param regras Regras que irão votar
	 * @param exemplo Exemplo a ser votado
	 * @param classePositiva String usada para a identificação da classe positiva ou negativa
	 * @return
	 */
	public abstract double votacao(ArrayList<Regra> regras, Instance exemplo, String classePositiva);
	
	/**
	 * Método abstrato de votação multiclasses
	 * @author Matheus Rosendo
	 * @param regras Regras que irão votar
	 * @param exemplo Exemplo a ser votado
	 * @param numClasses quantidade de classes do exemplo
	 * @return ArrayList<Integer> Indices das classes mais votadas
	 */
	public abstract ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes);
	
	/**
	 * Método que seleciona todas as regras, positivas e negativas, que votam no exemplo passado como paramêtro.
	 * @param regrasVotacaoPositiva Lista em que serão guardados as regras positivas
	 * @param regrasVotacaoNegativa Lista em que serão guardados as regras negativas
	 * @param regras Todas as regras
	 * @param exemplo Exemplo a ser votado
	 * @param classePositiva String que identifica qual é a classe positiva
	 */
	public void obterRegrasVotam(ArrayList<Regra> regrasVotacaoPositiva, ArrayList<Regra> regrasVotacaoNegativa, ArrayList<Regra> regras, Instance exemplo, String classePositiva){
		
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				if(regra.cabeca == regra.classe.indexOfValue(classePositiva)){
					regrasVotacaoPositiva.add(regra);
				}
				else{ 
					regrasVotacaoNegativa.add(regra);
				}
			}	
		}
		
	}
	
	/**
	 * @author Matheus
	 * @param regrasVotacaoPositiva Lista em que serão guardados as regras positivas
	 * @param regrasVotacaoNegativa Lista em que serão guardados as regras negativas
	 * @param regras Todas as regras
	 * @param exemplo Exemplo a ser votado
	 * @param classePositiva String que identifica qual é a classe positiva
	 */
	public void obterRegrasVotamMultiClasse(ArrayList<Regra> regrasDaClasseQueVotamNoExemplo, ArrayList<Regra> regras, Instance exemplo, String nomeClasse){
		
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				if(regra.cabeca == regra.classe.indexOfValue(nomeClasse)){
					regrasDaClasseQueVotamNoExemplo.add(regra);
				}
			}	
		}
		
	}
	
	/**
	 * Método em que as k regras, positivas e negativas, votam. O valor da votação é a média das regras que votam.
	 * @param regrasVotacaoPositiva Regras positivas que cobrem o exemplo
	 * @param regrasVotacaoNegativa Refras negativas que cobrem o exemplo
	 * @param k numero de regras que irão votar
	 * @return
	 */
	public double votarConfidenceMedia(ArrayList<Regra> regrasVotacao, double k){
		
		double votos = 0;
		
		
		for (int i = 0; i<k;i++) {
			if(i<regrasVotacao.size()){
				int indice = (regrasVotacao.size()-1) - i;
				Regra regraPos = regrasVotacao.get(indice);
				regraPos.votou = true;
				votos += regraPos.getConfidence();
			}
			
		}
		
		if(regrasVotacao.size()<k && regrasVotacao.size()!=0)
			votos = votos/regrasVotacao.size();
		else
			votos = votos/k;
		
		
		return votos;
	}
	
public double votarNormal(ArrayList<Regra> regrasVotacao, double k){
		
		double votos = 0;
		
		
		for (int i = 0; i<k;i++) {
			if(i<regrasVotacao.size()){
				int indice = (regrasVotacao.size()-1) - i;
				Regra regraPos = regrasVotacao.get(indice);
				regraPos.votou = true;
				votos += 1;
			}
			
		}
		
		return votos;
	}
	
	
/**
 * Método em que as k regras, positivas e negativas, votam.
 * @param regrasVotacaoPositiva Regras positivas que cobrem o exemplo
 * @param regrasVotacaoNegativa Refras negativas que cobrem o exemplo
 * @param k numero de regras que irão votar
 * @return
 */
public double votarConfidence(ArrayList<Regra> regrasVotacao, double k){
	
	double votos = 0;
	
	
	for (int i = 0; i<k;i++) {
		if(i<regrasVotacao.size()){
			int indice = (regrasVotacao.size()-1) - i;
			Regra regraPos = regrasVotacao.get(indice);
			regraPos.votou = true;
			votos += regraPos.getConfidence();
		}
		
	}
	
	return votos;
}
	

}
