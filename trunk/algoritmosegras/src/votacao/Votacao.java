package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.Classe;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe abstrata que representa um m�todo de vota��o das regras. Cada m�todo de vota��o deve exentender esta classe
 * e implementar o m�todo de vota��o.
 * @author Andr� de Carvalho
 *
 */
public abstract class Votacao {
	
	/**
	 * M�todo abstrato de vota��o
	 * @param regras Regras que ir�o votar
	 * @param exemplo Exemplo a ser votado
	 * @param classePositiva String usada para a identifica��o da classe positiva ou negativa
	 * @return
	 */
	public abstract double votacao(ArrayList<Regra> regras, Instance exemplo, String classePositiva);
	
	/**
	 * M�todo abstrato de vota��o multiclasses
	 * @author Matheus Rosendo
	 * @param regras Regras que ir�o votar
	 * @param exemplo Exemplo a ser votado
	 * @param numClasses quantidade de classes do exemplo
	 * @return ArrayList<Integer> Indices das classes mais votadas
	 */
	public abstract ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, ArrayList<Classe> classes);
	
	/**
	 * M�todo que seleciona todas as regras, positivas e negativas, que votam no exemplo passado como param�tro.
	 * @param regrasVotacaoPositiva Lista em que ser�o guardados as regras positivas
	 * @param regrasVotacaoNegativa Lista em que ser�o guardados as regras negativas
	 * @param regras Todas as regras
	 * @param exemplo Exemplo a ser votado
	 * @param classePositiva String que identifica qual � a classe positiva
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
	 * @param regrasVotacaoPositiva Lista em que ser�o guardados as regras positivas
	 * @param regrasVotacaoNegativa Lista em que ser�o guardados as regras negativas
	 * @param regras Todas as regras
	 * @param exemplo Exemplo a ser votado
	 * @param classePositiva String que identifica qual � a classe positiva
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
	 * M�todo em que as k regras, positivas e negativas, votam. O valor da vota��o � a m�dia das regras que votam.
	 * @param regrasVotacaoPositiva Regras positivas que cobrem o exemplo
	 * @param regrasVotacaoNegativa Refras negativas que cobrem o exemplo
	 * @param k numero de regras que ir�o votar
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
 * M�todo em que as k regras, positivas e negativas, votam.
 * @param regrasVotacaoPositiva Regras positivas que cobrem o exemplo
 * @param regrasVotacaoNegativa Refras negativas que cobrem o exemplo
 * @param k numero de regras que ir�o votar
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
