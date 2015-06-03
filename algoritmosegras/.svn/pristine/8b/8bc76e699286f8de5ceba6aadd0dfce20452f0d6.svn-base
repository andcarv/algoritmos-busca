package regra;

import weka.core.Attribute;

public abstract class Atributo {
	
	public Attribute att;
	public int posicao;
	public boolean vazio;
	
	/**
	 * Método que atualiza os valore do atributo
	 * @param valor1 Primeiro valor do atributo, varia de acordo com o tipo do atributo
	 * @param valor2 Segundo valor do atributo, varia de acordo com o tipo do atributo
	 */
	public abstract void atualizar(double valor1, double valor2);
	
	/**
	 * Método que retorna os valores do atributo
	 * @return Array de double que contém todos os valoresa do atributo
	 */
	public abstract double[] getValores();
	
	/**
	 * Método que compara o valor do atributo com o valor passado como parametro
	 * @param valorExemplo Valor a ser comparado com a regra
	 * @return Boolean que verifica se o valor passado como parametro e coberto ou não pelo atributo
	 */
	public abstract boolean compararValor(double valorExemplo);
	
	public abstract boolean isNominal();
	public abstract boolean isNumerico();
	public abstract boolean isVazio();
	public abstract boolean isCombinacao();
	public abstract boolean isGeneric(Atributo a);
	
	public abstract Atributo clone();
	
	public boolean equals(Object o){
		Atributo a = (Atributo) o;
		if(this.isNominal()){
			if(a.isNumerico())
				return false;
			if(this.isVazio() && a.isVazio())
				return true;
			else{
				if(this.isVazio() || a.isVazio())
					return false;
				double[] valores1 = this.getValores(); 
				double[] valores2 = a.getValores();
				if((valores1[0]!= valores2[0]) ||(valores1[1]!= valores2[1]))
					return false;
			}
		} else {
			if(a.isNominal())
				return false;
			if(this.isVazio() && a.isVazio())
				return true;
			else{
				if(this.isVazio() || a.isVazio())
					return false;
				double[] valores1 = this.getValores(); 
				double[] valores2 = a.getValores();
				if((valores1[0]!= valores2[0]) ||(valores1[1]!= valores2[1]))
					return false;
			}
		}
		return true;
	}
	

}
