package regra;

import weka.core.Attribute;

/**
 * Classe que representa um atributo númerico de uma regra
 * @author andre
 *
 */
public class AtributoNumerico extends Atributo {

	
	public double limiteInferior;
	public double limiteSuperior;
	
	public double limiteMaximo;
	public double limiteMinimo;
	
	public static final double probabilidaVazioNumerico = 0.1;
	
	
	/**
	 * Construtor da classe AtributoNumerico. Recebe como parametro os limites
	 * do atributo
	 * @param li Limite inferior
	 * @param ls Limite Superior
	 */
	public AtributoNumerico(double li, double ls, double lmin, double lmax, Attribute a, int pos){
		limiteInferior = li;
		limiteSuperior = ls;
		limiteMinimo = lmin;
		limiteMaximo = lmax;
		
		att = a;
		posicao = pos;
	}
	
	public AtributoNumerico(boolean v,Attribute a, int pos, double lmin, double lmax){
		vazio = v;
		att = a;
		posicao = pos;
		limiteMinimo = lmin;
		limiteMaximo = lmax;
	}
	
	public AtributoNumerico(){}
	
	/**
	 * Método que recebe parametros o limite inferior, valor1, e o limte superior, valor2
	 */
	public void atualizar(double valor1, double valor2) {
		if(valor1 == Double.MIN_VALUE && valor2 == Double.MIN_VALUE){
			vazio = true;
		} else {	
			vazio = false;
			if(valor1>=limiteMinimo)
				limiteInferior = valor1;
			else
				limiteInferior = limiteMinimo;

			if(valor2<=limiteMaximo)
				limiteSuperior = valor2;
			else
				limiteSuperior = limiteMaximo;
		}
	}

	/**
	 *Método que verifica se o valor passado como parametro esta dentro do intervalo
	 *definido para o atributo 
	 */
	public boolean compararValor(double valorExemplo) {
		if(valorExemplo == Double.NaN){
			return true;
		} else {
			if(valorExemplo >=limiteInferior && valorExemplo<=limiteSuperior){
				return true;
			}
		}
		return false;
	}

	/**
	 * Método que retorna os valores do intervalo do atributo
	 */
	public double[] getValores() {
		double[] retorno = new double[2];
		retorno[0] = limiteInferior;
		retorno[1] = limiteSuperior;
		return retorno;
	}
	
	public boolean isNominal(){
		return false;
	}
	public boolean isNumerico(){
		return true;
	}
	public boolean isVazio(){
		return vazio;
	}
	
	public boolean isCombinacao() {
		return false;
	}
	
	public double[] obterNovoValorAleatorio(){
		double[] retorno = new double[2];
		//Probabilidade de se escolher um atributo vazio
		double temp = Math.random();
		//Valor definido atraves de uma constante. Se numero aleatorio for menor
		//que a proabilidade de vazio, entao preencher o atributo com o valor vazio
		if(temp < AtributoNumerico.probabilidaVazioNumerico){
			double vazio = Double.MIN_VALUE;
			//Passa o valor mínimo para a funcao atualizar, que configura o atributo como vazio
			retorno[0] = vazio;
			retorno[1] = vazio;
		}else{
			
			double limiteMinimo = this.limiteMinimo;
			double limiteMaximo = this.limiteMaximo;
			
			double tamanhoIntevalo = limiteMaximo - limiteMinimo;
			
			//Calcula a ordem de grandeza do intervalo para o calculo dos limites
			double potencia = Math.ceil(Math.log10(tamanhoIntevalo));
			double ordemGrandeza = Math.pow(10,potencia);
			//Calcula os limites de forma aleatoria, dentro do tamanho do intevalo
			double intervalo = (Math.random()*ordemGrandeza)%tamanhoIntevalo; 
			double lim1 = intervalo+ limiteMinimo;
			intervalo = (Math.random()*ordemGrandeza)%tamanhoIntevalo;
			double lim2 = intervalo+ limiteMinimo;
			
			if(lim1<lim2){
				retorno[0] =lim1;
				retorno[1] =lim2;
			} else {
				retorno[0] =lim2;
				retorno[1] =lim1;
			}
			
		}
		return retorno;
	}
	
	/**
	 * Retorna true se o atributo passado como parametro eh generico em relação ao atributo atual.
	 */
	public boolean isGeneric(Atributo a){
		if(a.isNominal())
			return true;
		else{
			//Caso os dois sejam vazios, eles são iguais
			if(this.isVazio() && a.isVazio())
				return true;
			else{
				//Casa um dos dois sejam vazios, eles são diferentes
				if(this.isVazio() || a.isVazio())
					return false;
				double[] valoresThis = this.getValores();
				double[] valoresA = a.getValores();
				//Se o intervalo do atributo passado como parametro contém o intervalo do atributo
				//ele é generico em relação ao atributo atual.
				if(valoresA[0]<= valoresThis[0] && valoresA[1]>=valoresThis[1])
					return true;
				else{
					return false;
				}
			}
		}
	}
	
	public String toString(){
		String retorno;
		
		if(!vazio)
			retorno = "[" + att.name() + " = ("+ limiteInferior+" , "+ limiteSuperior+ ")]";			
		else
			retorno = "?";
		return retorno;
	}
	
	public Atributo clone(){
		AtributoNumerico numerico = new AtributoNumerico();
		numerico.limiteInferior = limiteInferior;
		numerico.limiteSuperior = limiteSuperior;
		numerico.limiteMinimo = limiteMinimo;
		numerico.limiteMaximo = limiteMaximo;
		numerico.att = att;
		numerico.posicao = posicao;
		numerico.vazio = vazio;
		return numerico;
	}
}
