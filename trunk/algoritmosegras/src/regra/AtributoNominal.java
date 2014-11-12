package regra;

import weka.core.Attribute;

public class AtributoNominal extends Atributo {

	public double valor;
	public double operador;
		
	public static double igual = 0;
	public static double diferente = 1;
	
	public AtributoNominal(){}
	
	public AtributoNominal(double v, double o, Attribute a, int pos){
		valor = v;
		operador = o;
		att = a;
		posicao = pos;
		vazio = false;
	}
	
	public AtributoNominal(boolean v, Attribute a, int pos){
		vazio = v;
		att = a;
		posicao = pos;
	}
	
	/**
	 * Método que atualiza os valores do atributo através do valores passados como parametro
	 * @param valor1 Valor do atributo
	 * @param valor2 Operador do atributo, pode ser igual ou diferente 
	 */
	public void atualizar(double valor1, double valor2) {
		if(valor1!=-1){
			valor = valor1;
			operador = valor2;
			vazio = false;
		} else{
			valor = Double.MIN_VALUE;
			operador = valor2;
			vazio = true;
		}
	}

	
	/**
	 * Método que retorna os valors do atributo em um array de double
	 * @return Array de double, posicao 0 o valor posicao 1 o operador
	 */
	public double[] getValores() {
		double[] retorno = new double[2];
		retorno[0] = valor;
		retorno[1] = operador;
		return retorno;
	}
	
	/**
	 * Método que verifica se o valor do exemplo passado pelo parametro é coberto pelo atributo
	 * @param valorExemplo Valor a ser comparado com o atributo
	 * @return Retorna true caso o valor seja igual ao valor de atributo, de acordo com o operador 
	 */
	public boolean compararValor(double valorExemplo){
		if(valorExemplo == Double.NaN){
			return true;
		} else {
			if(operador == igual){
				if(valor==valorExemplo)
					return true;
				else
					return false;		
			} else {
				if(valor!=valorExemplo)
					return true;
				else
					return false;
			}
		}
	}
	
	/**
	 * Método que calcula um novo valor aleatorio para o atributo
	 * @return Novo valor aleatorio para o atributo
	 */
	public double obterNovoValorAleatorio(){
		//Possiveis valores para os atributos mais o valor vazio
		int numValues = att.numValues()+1;
		double porcentagens = (1.0/numValues);
		double temp = Math.random();
		int random = ((int)(temp/porcentagens)) -1;
		return random;
	}
	
	public boolean isNominal(){
		return true;
	}
	public boolean isNumerico(){
		return false;
	}
	public boolean isVazio(){
		return vazio;
	}
	
	public boolean isCombinacao() {
		return false;
	}
	
	public String toString(){
		if(!vazio){
			String nome = att.value((int)valor);
			String retorno = "[" + att.name() + " = " + nome + "]";
			return retorno;
		} else {
			return "?";
		}
	}
	
	/**
	 * Retorna true se o atributo passado como parametro é generico em relação ao atual (sejam iguais).
	 * 0 caso sejam diferentes retorna false.
	 */
	public boolean isGeneric(Atributo a){
		if(a.isNumerico())
			return false;
		else{
			if(this.equals(a))
				return true;
			else
				return false;
		}
	}
	
	public Atributo clone(){
		AtributoNominal nominal = new AtributoNominal();
		nominal.valor = valor;
		nominal.att = att;
		nominal.operador = operador;
		nominal.vazio = vazio;
		nominal.posicao = posicao;

		
		return nominal;
	}
	
}
