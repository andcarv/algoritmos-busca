package regra;

import java.util.ArrayList;
import java.util.Iterator;

import weka.core.Attribute;

/**
 * Classe que representa um combinação de atributos nominais da base de dados
 * Cada combinacao é representada através de um número binário:
 * Ex: 0101 - Combinação do atributo 2 com o atributo 0 
 * @author andre
 *
 */
public class AtributoCombinado extends Atributo {
	
	
	//Array que contém os valores da base de dados dos atributos combinados
	private ArrayList<Double> combinacao = null;
	//String que representa a a combinacao dos atributos
	private String combinacaoBinaria = null;
	
	
	public AtributoCombinado(){}
	public AtributoCombinado(boolean v, Attribute a, int pos){
		vazio = v;
		att = a;
		posicao = pos;
		combinacao = new ArrayList<Double>();
		combinacaoBinaria = "0";
	}
	
	
	/**
	 * Método de atualizacao do atributo. Recebe como parametro um inteiro que representa
	 * a combinação binária. 
	 */
	public void atualizar(double valor1, double valor2) {
		
		if(valor1==-1){
			combinacao.clear();
			combinacaoBinaria = "0";
			vazio = true;
		} else{
		vazio = false;
		combinacao.clear();
		//Transforma inteiro em binário
		combinacaoBinaria = Integer.toBinaryString((int)valor1);
		String complemento = "";
		for(int i = 0; i<(valor2-combinacaoBinaria.length());i++)
			complemento+="0";
		combinacaoBinaria = complemento+combinacaoBinaria;
		char[] flags = combinacaoBinaria.toCharArray();
		//Identifica quais são os atributos combinados
		for (int i = 0; i < flags.length; i++) {
			char c = flags[i];
			if(c=='1'){
				//Obtém o valor do atributo igual ao do weka. 
				int valor = (flags.length-1)-i;
				if(combinacao.contains(new Double(valor)))
					System.out.println();
				combinacao.add(new Double(valor));
			}
		}
		
		
		}
	}

	@Override
	public Atributo clone() {
		AtributoCombinado combinado = new AtributoCombinado();
		combinado.att = att;
		combinado.vazio = vazio;
		combinado.posicao = posicao;
		combinado.combinacao = new ArrayList<Double>();
		combinado.combinacao.addAll(combinacao);
		combinado.combinacaoBinaria = combinacaoBinaria;
		return combinado;
	}

	/**
	 * Compara se o valor passado como parametro e coberto pela combinacao
	 */
	public boolean compararValor(double valorExemplo) {
		if(valorExemplo == Double.NaN){
			return true;
		} else {
			Double v = new Double(valorExemplo);
			return combinacao.contains(v);
		}
	
	}

	//Retorna o valor da combinacao. Transforma a combinacao binaria em inteiro.
	public double[] getValores() {
		double[] retorno = new double[2];
		retorno[0] = Integer.parseInt(combinacaoBinaria,2);
		retorno[1] = combinacaoBinaria.length();
		return retorno;
	}

	public boolean isGeneric(Atributo a) {
		if(a.isNumerico())
			return false;
		else{
			if(this.equals(a))
				return true;
			else
				return false;
		}
	}
	
	/**
	 * Verifica se o objeto passado é um AtributoCombinado e checa se a combinação
	 * de ambos atributos é igual.
	 */
	public boolean equals(Object o){
		try{
			AtributoCombinado att2 = (AtributoCombinado) o;
			if(this.combinacaoBinaria.equals(att2.combinacaoBinaria))
				return true;
			else
				return false;
		} catch(ClassCastException ex){return false;}
	}

	public boolean isNominal() {
		return true;
	}

	public boolean isNumerico() {
		return false;
	}
	
	public boolean isCombinacao() {
		return true;
	}

	public boolean isVazio() {
		return vazio;
	}
	
	/**
	 * Método que calcula um novo valor aleatorio para o atributo
	 * @return Novo valor aleatorio para o atributo
	 */
	public double obterNovoValorAleatorio(){
		//Possiveis valores para os atributos mais o valor vazio
		int numValues = (int)(Math.pow(2, att.numValues())-1);
		double porcentagens = (1.0/numValues);
		double temp = Math.random();
		int random = ((int)(temp/porcentagens)) -1;
		return random;
	}
	
	public String toString(){
		if(!vazio){
			StringBuffer retorno = new StringBuffer();
			retorno.append("[" + att.name() + " = ");
			for (Iterator<Double> iterator = combinacao.iterator(); iterator.hasNext();) {
				Double valor = (Double) iterator.next();
				
				String nome = att.value(valor.intValue());
				retorno.append(nome + " v ");
			}
			
			retorno.delete(retorno.length()-3,retorno.length()-1);
			retorno.append("]");
			
			return retorno.toString();
		} else {
			return "?";
		}
	}
	
	public static void main(String[] args) {
		int j = Integer.parseInt("10",2);
		System.out.println(j);
		
		
	}

}
