package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.Classe;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a votação por confiança com a seleção das regras através do valor de Laplace
 */
public class VotacaoConfidenceLaplace extends Votacao {

	public VotacaoConfidenceLaplace(){
		System.out.println("Votação: Confidence Laplace");
	}
	
	/**
	 * Método que restringe a votação à somente k regras por classe, onde k representa o
	 * número de classes do problema.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0;
		double negativo = 0;
		
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		//Seleção das k melhores regras para cada classe
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				if(regra.cabeca == regra.classe.indexOfValue(classePositiva))
					obterKMelhoresRegrasLaplace(regrasVotacaoPositiva, (int)k, regra);
				else 
					obterKMelhoresRegrasLaplace(regrasVotacaoNegativa, (int)k, regra);
			}	
		}
		
		//Votação das k melhores regras para cada classe
		
		for (Iterator<Regra> iter = regrasVotacaoPositiva.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			regra.votou = true;
			positivo+= regra.getConfidence();	
		}
		
		
		for (Iterator<Regra> iter = regrasVotacaoNegativa.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			regra.votou=true;
			negativo+= regra.getConfidence();	
		}
		
		if(regrasVotacaoPositiva.size()!=0)
			positivo = positivo/regrasVotacaoPositiva.size();
		
		
		if(regrasVotacaoNegativa.size()!=0)
			negativo = negativo/regrasVotacaoNegativa.size();
		
		
		return positivo-negativo;
	}
	
	/**
	 * Método que verifica se a Regra r é melhor que as regras presentes no array selecao.
	 * Caso sim a pior regra é retirada e r é inserida na selação
	 * @param selecao Array contendo as k regras selecionadas
	 * @param k Numero de regras presentes em selecao
	 * @param r Regra que será verificada para entrar na selecao
	 * @return Regras selecionadas por Laplace
	 */
	public ArrayList<Regra> obterKMelhoresRegrasLaplace(ArrayList<Regra> selecao, int k, Regra r){
		
		if(selecao.size()<k)
			selecao.add(r);
		else{
			boolean inserir = false;
			for (Iterator<Regra> iter = selecao.iterator(); iter.hasNext() & !inserir;) {
				Regra regra = (Regra) iter.next();
				if(regra.getLaplace()<r.getLaplace())
					inserir = true;
			}
			if(inserir){
				double menorLaplace = Double.MAX_VALUE;
				int menorIndice = Integer.MAX_VALUE;
				int indice = 0;
				for (Iterator<Regra> iter = selecao.iterator(); iter.hasNext();) {
					Regra regra = (Regra) iter.next();
					if(regra.getLaplace()<menorLaplace){
						menorLaplace = regra.getLaplace();
						menorIndice = indice;
					}
					indice++;
				}
				selecao.remove(menorIndice);
				selecao.add(r);
			}
		}
		return selecao;
	}

	@Override
	public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo,	ArrayList<Classe> classes) {
		// TODO Auto-generated method stub
		return null;
	}

}
