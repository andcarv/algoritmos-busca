package regra;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import objetivo.FuncaoObjetivo;

import weka.associations.ItemSet;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Classe que representa uma regra que possui atributos discretos e numericos
 * @author André B. de Carvalho
 *
 */
public class Regra{
	
	//Array de atributos que representa o corpo da regra
	public Atributo[] corpo;
	//Classe da regra
	public int cabeca;
	//Contador dos atributos não vazios da regra
	public int atributosNaoVazios = 0;
	//Objeto do Weka que possui informações sobre a classe da regra
	public Attribute classe;
	//public List<Attribute> attributes = new LinkedList<Attribute>();
	//Matriz de contigencia contendo as medidas da regra
	public MatrizContingencia matrizContigencia = null;
	//Array que contém as medidas dos objetivos da regra
	public double[] valoresObjetivos;
	//Conjunto de funções objetivos 
	public ArrayList<FuncaoObjetivo> objetivos = null;
	
	//Variáveis contendo os valores das medidas para a regra
	private double acc = -1;
	private double err = -1;
	private double negrel= -1;
	private double sens= -1;
	private double spec= -1;
	private double cov=-1;
	private double sup= -1;
	public double confianca =-1;
	private double erro = -1;
	private double acerto = -1;
	private double laplace = -1;
	private double nov = -1;
	//Boleano que indica se a regra foi utilizada no processo de votação de um exemplo
	public boolean votou = false;
	
	
	public Regra(){
		matrizContigencia = new MatrizContingencia();
		objetivos = new ArrayList<FuncaoObjetivo>();		
		atributosNaoVazios = 0;
	}
	
	/**
	 * Construtor que inicia a regra com as funções objetivo.
	 * @param o ArrayList contendo as funções objetivo
	 */
	public Regra(ArrayList<FuncaoObjetivo> o){
		matrizContigencia = new MatrizContingencia();
		objetivos = new ArrayList<FuncaoObjetivo>();
		for (Iterator<FuncaoObjetivo> iter = o.iterator(); iter.hasNext();) {
			FuncaoObjetivo obj = (FuncaoObjetivo) iter.next();
			objetivos.add(obj);
			
		}
		atributosNaoVazios = 0;
	}
	
	/**
	 * Método que inicializa uma regra com os valores obtidos atraves do Apriori.
	 * @param b Corpo da regre
	 * @param h Cabecao da regra
	 * @param conf Confianca da regra
	 * @param e Lista com os atributos da regra
	 * @param c Atributo da classe da regra
	 */
	public Regra(ItemSet b, ItemSet h, double conf, Enumeration<Attribute> e, Attribute c) throws Exception{
		
		cabeca = h.itemAt(0);
		confianca = conf;
		int corpoTemp[] = b.items();
		atributosNaoVazios = 0;
		corpo = new Atributo[corpoTemp.length];
		int i = 0;
		while(e.hasMoreElements()){
			Attribute att = (Attribute) e.nextElement(); 
			//attributes.add(att);
			if(att.isNominal()){
				if(corpoTemp[i]==-1){
					AtributoNominal vazio = new AtributoNominal(true, att, i);
					corpo[i++] = vazio;
				} else {
					AtributoNominal nominal = new AtributoNominal(corpoTemp[i], AtributoNominal.igual, att, i);
					corpo[i++] = nominal;
				}
				
			} else{
				throw new Exception("Atributo não nominal!");
			}
 							
		}
		
		classe = c;
		matrizContigencia = new MatrizContingencia();
		getNumAtributosNaoVazios();
				
	}

	public Object clone(){
		Regra novaRegra = new Regra(objetivos);
		
		novaRegra.cabeca = cabeca;
		novaRegra.classe = classe;
		novaRegra.confianca = confianca;
		/*novaRegra.attributes = new LinkedList<Attribute>();
		for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			Attribute atributo = (Attribute) iter.next();
			novaRegra.attributes.add(atributo);
		}*/
		novaRegra.corpo = new Atributo[corpo.length]; 
		for(int i = 0; i<corpo.length; i++){
			Atributo a = corpo[i].clone();
			novaRegra.corpo[i] = a;
		}
		
		novaRegra.matrizContigencia = (MatrizContingencia) matrizContigencia.clone();
		
		
		return novaRegra;
	}
	
	
	/**
	 * Método que cálcula a precisão da regra a partir da matriz de contingência
	 * @return Precisão da regra
	 */
	public double getACC(){
		double temp = matrizContigencia.getB();
		if(temp!=0)
			acc = matrizContigencia.getH_B()/temp;
		else
			acc = 0;
	 
		return acc;
	}
	
	/**
	 * Cálculo da precisão de Laplace
	 * @return Precisão de Laplace
	 */
	public double getLaplace(){
		double temp = matrizContigencia.getB();
		int numClasses = classe.numValues();
		if(temp!=0)
			laplace = (matrizContigencia.getH_B()+1)/(temp+numClasses);
		else
			laplace = 0;
	 
		return laplace;
	}
	
	/**
	 * Cálculoda novidade da regra
	 * @return Novidade
	 */
	public double getNovelty(){
		
		double fhb = matrizContigencia.getH_B()/matrizContigencia.getNumElementos();
		double fh = matrizContigencia.getH()/matrizContigencia.getNumElementos();
		double fb = matrizContigencia.getB()/matrizContigencia.getNumElementos();
		nov = fhb - (fh*fb);
		return nov;
	}
	
	/**
	 * Cálculo do erro. Complemento da precisão
	 * @return Erro
	 */
	public double getERR(){
		
		double temp = matrizContigencia.getB();
		if(temp!=0)
			err = matrizContigencia.getNotH_B()/temp;
		else
			err = 0;
		 
		return err;
	}
	
	/**
	 * Cálculo da Negative Relation
	 * @return Negative Relation
	 */
	public double getNegRel(){
	
		double temp = matrizContigencia.getNotB();
		if(temp!=0)
			negrel = matrizContigencia.getNotH_NotB()/temp;
		else
			negrel = 0;
	 
		return negrel;
	}
	
	/**
	 * Cálculo da sensitividade
	 * @return Sensitividade
	 */
	public double getSens(){
		
		double temp = matrizContigencia.getH();
		if(temp!=0)
			sens = matrizContigencia.getH_B()/temp;
		else sens = 0;
		 
		return sens;
	}
	
	/**
	 * Cálculoda especificidade
	 * @return Especificidade
	 */
	public double getSpec(){
	
		double temp = matrizContigencia.getNotH();
		if(temp !=0)
			spec = matrizContigencia.getNotH_NotB()/temp;
		else spec =0;
	 
		return spec;
	}
	/**
	 * Cálculo da cobertura
	 * @return Cobertura
	 */
	public double getCov(){
		double temp = matrizContigencia.getNumElementos();
		if(temp!=0)
			cov = matrizContigencia.getB()/ temp;
		else
			cov = 0;
		return cov;
	}
	/**
	 * Cálculo do suporte
	 * @return Suporte
	 */
	public double getSup(){
	
		double temp = matrizContigencia.getNumElementos();
		if(temp!=0)
			sup = matrizContigencia.getH_B()/temp;
		else
			sup = 0;
		return sup;
	}
	
	/**
	 * Cálculo da confiança da regra
	 * @return Confiança
	 */
	public double getConfidence(){
		
			double temp = matrizContigencia.getH_B()+matrizContigencia.getNotH_B();
			if(temp == 0)
				confianca = 0;
			else
				confianca = matrizContigencia.getH_B()/(temp); 
		return confianca;
	}
	
	public double getErro(){
 
		double temp = matrizContigencia.getNumElementos();
		if(temp != 0 )
			erro = (matrizContigencia.getNotH_B()+matrizContigencia.getH_NotB())/temp;
		else
			erro = 0;
		return erro;
	}
	
	public double getAcerto(){
		
		double temp = matrizContigencia.getNumElementos();
		if(temp!=0)
			acerto = (matrizContigencia.getH_B()+matrizContigencia.getNotH_NotB())/temp;
		else
			acerto = 0;
		
		return acerto;
	}
	
	/**
	 * Método que obtem os valores dos objetivos
	 * @return Array contendo todos os valores dos objetivos
	 * 
	 */
	public double[] getValoresObjetivos(){
		if(valoresObjetivos == null){
			calcularHeuristicas();
		}
		return valoresObjetivos;
	}
	
	/**
	 * Método que re-calcula os valores dos objetivos
	 * @return
	 */
	public double[] calcularValoresObjetivos(){
		calcularHeuristicas();
		return valoresObjetivos;
	}
	
	/**
	 * Método que calculo todos os objetivos da regra
	 *
	 */
	public void calcularHeuristicas(){
		valoresObjetivos = new double[objetivos.size()];
		int i = 0;
		for (Iterator<FuncaoObjetivo> iter = objetivos.iterator(); iter.hasNext();) {
			FuncaoObjetivo funcao = (FuncaoObjetivo) iter.next();
			valoresObjetivos[i++] = funcao.calcularHeuristica(this);
		}
		
	}

	public String toString1(){
		
		
		StringBuffer str = new StringBuffer();
		String nome;
		str.append("< ");
		for(int i = 0; i<corpo.length; i++){
			str.append(corpo[i] + ", ");
		}
		
		nome = classe.value(cabeca);
		
		
		str.append("[" + classe.name() + " = " + nome + "] " + "(" + confianca +")");
		str.append(">");
		
		if(valoresObjetivos != null){
			str.append("\t[");
			for(int i = 0; i<valoresObjetivos.length; i++){
				str.append(valoresObjetivos[i] + ", ");
			}
			str.deleteCharAt(str.length()-1);
			str.deleteCharAt(str.length()-1);
			str.append(']');
		}
		
		
		str.append("\t(" + getACC()+")");
		str.append("\t(" + getAcerto()+")");
		str.append("\t(" + getConfidence()+")");
		str.append("\t(" + getCov()+")");
		str.append("\t(" + getERR()+")");
		str.append("\t(" + getErro()+")");
		str.append("\t(" + getNegRel()+")");
		str.append("\t\t(" + getSens() +")");
		str.append("\t(" + getSpec() +")");
		
		//str.append("\n" +matrizContigencia.toString() + "\n");
		
		
		return str.toString();
		
	}
	
public String toString(){
		
		
		StringBuffer str = new StringBuffer();
		String nome;
		for(int i = 0; i<corpo.length; i++){
				str.append(corpo[i] + "\t");
		}
		
		nome = classe.value(cabeca);
		
		
		str.append("[" + classe.name() + " = " + nome + "]");
		
		if(valoresObjetivos != null){
			str.append("\t");
			for(int i = 0; i<valoresObjetivos.length; i++){
				str.append(new Double(valoresObjetivos[i]).toString().replace('.',',') + "\t");
			}
		}
		
		str.append("\t" + new Double(getACC()).toString().replace('.',','));
		str.append("\t" + new Double(getERR()).toString().replace('.',','));
		str.append("\t" + new Double(getNegRel()).toString().replace('.',','));
		
		//str.append("\t" + new Double(getAcerto()).toString().replace('.',','));
		
		str.append("\t" + new Double(getConfidence()).toString().replace('.',','));
		str.append("\t" + new Double(getSup()).toString().replace('.',','));
		str.append("\t" + new Double(getCov()).toString().replace('.',','));
		str.append("\t" + new Double(getNovelty()).toString().replace('.',','));
		
		//str.append("\t" + new Double(getErro()).toString().replace('.',','));
		
		//str.append("\t" + new Double(getSens()).toString().replace('.',','));
		//str.append("\t" + new Double(getSpec()).toString().replace('.',','));
		
		
		
		
		return str.toString();
		
	}
	
	
	/**
	 * Medoto que verifica se a instancia passada como parametro e igual a regra.
	 * Retorna resultados que serao usados para a construcao da matriz de contigencia
	 * @param i Instancia de teste a ser comparada com a regra
	 * @return Retorno da comparacao: 0 hb, 1 h'b, 2 hb', 3 h'b'
	 * 
	 * 
	 */
	public void compararRegraContigencia(Instance i){
		double b[] = i.toDoubleArray();
		double h = b[b.length-1];
		boolean compCorpo = compararCorpo(b);
		if(compCorpo){
			if(cabeca == (int)h){
				//Corpo e cabeca iguais - hb
				matrizContigencia.incH_B();
				
			}
			else{
				//Corpo igual mas cabeca diferente - h'b
				matrizContigencia.incNotH_B();
			}
		}else {
			
			if(cabeca == (int)h){				
				//Corpo diferente e cabeca igual - hb'
				matrizContigencia.incH_NotB();
			}
			else{
				//Corpo e cabeca diferentes - h'b'
				matrizContigencia.incNotH_NotB();
			}
			
		}
	}
	
	/**
	 * Método que indica se a regra a vazia
	 * @return Vazio ou não vazio
	 */
	public boolean isEmpty(){
		for(int i = 0; i<corpo.length; i++){
			if(!corpo[i].isVazio())
				return false;
		}
		
		return true;
	}
	
	/**
	 * Método que verifica se duas regras são iguais
	 */
	public boolean equals(Object o){
		Regra r = (Regra) o;
		for (int i = 0; i < corpo.length; i++) {
			Atributo atributo1 = corpo[i];
			Atributo atributo2 = r.corpo[i];
			if(!atributo1.equals(atributo2))
				return false;
		}
		if(cabeca == r.cabeca)
			return true;
		else
			return false;
	}
	
	
	/**
	 * @deprecated Método com erro
	 * Método que verifica se uma regra eh generica em relacao a outra * 
	 * @param r Regra a ser comparada
	 * @return -1 a regra r é genérica em relação a atual, 0 as regras sao diferentes, 1 a regra atual
	 * é genérica em relação a regra passada como parâmetro.
	 */
	public int isGeneric2(Regra r){
		boolean matrizes = matrizContigencia.equals(r.matrizContigencia);
		if(matrizes){
			int[] generic = new int[corpo.length];
			for (int i = 0; i < corpo.length; i++) {
				Atributo atributo1 = corpo[i];
				Atributo atributo2 = r.corpo[i];
				int g = 1;
				atributo1.isGeneric(atributo2);
				if(g == 0){		
					if(atributo1.isVazio())
						//Caso os atributos sejam diferentes, mas o primeiro seja vazio, ele é generico 
						//em relacao ao segundo
						generic[i] = 1;
					else{
						if(atributo2.isVazio())
							//Caso os atributos sejam diferentes, mas o segundo seja vazio, ele é generico 
							//em relacao ao segundo
							generic[i] = -1;
						else
							generic[i] = g;
					}
				} else
					generic[i] = g;
			}
			int sinal = 2;
			for (int i = 0; i < generic.length; i++) {
				if(generic[i]==0)
					return 0;
				if(generic[i]==-1){
					if(sinal==1)
						return 0;
					else
						sinal = -1;
				} else{
					if(generic[i]==1)
						if(sinal==-1)
							return 0;
						else
							sinal = 1;
				}
				
			}
			return sinal;}
		else return 0;
	}
	
	/**
	 * Método que verifica se a regra passada como parameto é generica em relação à regra atual.
	 * Uma regra eh generica se suas matriz de contingencias sao iguais, se os atributos discretos sao iguais
	 * e se seus atributos numericos sao cobertos
	 * @param r Regra que será verificada se eh generica ou nao.
	 * @return True se r é generica
	 */
	public boolean isGeneric(Regra r){
		boolean matrizes = matrizContigencia.equals(r.matrizContigencia);
		if(matrizes){
			for (int i = 0; i < corpo.length; i++) {
				Atributo atributo1 = corpo[i];
				Atributo atributo2 = r.corpo[i];
				boolean g = atributo1.isGeneric(atributo2);
				if(!g)
					return false;
			}
			return true;
		} else{
			return false;
		}
			
	}

	
	/**
	 * Método que retorna o número de atributos da regra
	 */
	public int getNumAtributosNaoVazios(){
		atributosNaoVazios = 0;
		for (int i = 0; i < corpo.length; i++) {
			Atributo a = corpo[i];
			if(!a.isVazio())
				atributosNaoVazios++;
		}
		
		return atributosNaoVazios;
	}
	
	/**
	 * Método que retorna os valores dos atributos no formato de array. 
	 * Utilizado no metodo de nuvem de partículas
	 * @return Todos os valores dos atributos no formato array
	 */
	public double[] obterCorpoArray(){
		ArrayList<Double> valoresAtributos = new ArrayList<Double>();
		for (int i = 0; i<corpo.length; i++) {
			Atributo atributo = corpo[i];
			double[] valores = atributo.getValores();
			if(atributo.isNominal()){
				//Caso o atributo seja nominal só é adicionado o valor do atributo.
				if(atributo.isVazio())
					valoresAtributos.add(new Double(0));
				else{
					//Adicao de 1 no valor do atributo para representar o valor vazio como sendo 0;
					double temp = valores[0] +1;
					valoresAtributos.add(new Double(temp));
				}
			} else {
				//Caso seja numerico, os dois valores do limite sao adicionados.
				AtributoNumerico a = (AtributoNumerico) atributo;
				if(atributo.isVazio()){
					valoresAtributos.add(new Double(a.limiteMinimo));
					valoresAtributos.add(new Double(a.limiteMinimo));
				}
				else{
					valoresAtributos.add(new Double(valores[0]));
					valoresAtributos.add(new Double(valores[1]));
				}
			}
		}
		
		//Gerar regras somente de uma classe
		//valoresAtributos.add(new Double(cabeca+1));
		
		double[] retorno = new double[valoresAtributos.size()];
		
		int i = 0;
		for (Iterator<Double> iter = valoresAtributos.iterator(); iter.hasNext();) {
			Double valor = (Double) iter.next();
			retorno[i++] = valor.doubleValue();
		}
		return retorno;
		
	}
	
	/**
	 * Método que preencher uma regra atraves de um array de valores obtido pela nuvem de particulas
	 * @param novosValores Array com os novos valores para a a regra
	 */
	public void preencherRegraArray(double[] novosValores){
		int j = 0;
		for(int i = 0; i<corpo.length; i++){
			Atributo atributo = corpo[i];
			if(atributo.isNominal()){
				//Diminui o valor de 1 para voltar aos valores normais no padrao do weka (vazio == -1).
				double valor = novosValores[j++] -1;
				atributo.atualizar(valor, AtributoNominal.igual);
			} else {
				AtributoNumerico a = (AtributoNumerico) atributo;
				double valor1 = novosValores[j++];
				double valor2 = novosValores[j++];
				if((valor1 == valor2) && (valor1 == a.limiteMinimo || valor1 == a.limiteMaximo))
					atributo.atualizar(Double.MIN_VALUE, Double.MIN_VALUE);
				else	
					atributo.atualizar(valor1, valor2);
			}
		}
		getNumAtributosNaoVazios();
	}
	
	
	/**
	 * Prencher o atributo nominal na posicao pos
	 * @param pos Posicao do atributo
	 * @param att Tipo do atributo representado atraves do tipo Attribute
	 */
	public void preencherAtributoNominalAleatorio(int pos, Atributo atributo){
		
		if(!atributo.isCombinacao()){
			AtributoNominal atNom = (AtributoNominal) atributo;
			double random = atNom.obterNovoValorAleatorio();
			double operador = AtributoNominal.igual;
			corpo[pos].atualizar(random, operador);
		} else{
			AtributoCombinado atComb = (AtributoCombinado) atributo;
			double random = atComb.obterNovoValorAleatorio();
			corpo[pos].atualizar(random, atComb.att.numValues());
		}
	}
	
	/**
	 * Prencher o atributo nominal na posicao pos, de forma proporcional aos valores da base
	 * @param pos Posicao do atributo na rega
	 * @param atributo Atributo a ser preenchido
	 * @param proporcao Array com as proporcoes dos valores na base de dados
	 */
	public void preencherAtributoNominalAleatorioProporcional(int pos, Atributo atributo, int[] proporcao){
		
		double probVazio = 1.0/atributo.att.numValues();
		int soma = 0;
		for (int i = 0; i < proporcao.length; i++) {
			soma += proporcao[i];
		}
		
		//Teto da roleta. Soma da distribuição dos possíveis valores, mais o probabilidade do vazio
		double tetoRoleta = soma/(1-probVazio);
		
		double random = Math.random();
		int indice = 0;
		//Acumulador da roleta
		double acc = 0;
		for (indice = 0; indice < proporcao.length; indice++) {
			double temp = proporcao[indice]/tetoRoleta;
			acc +=temp;
			if(random<acc)
				break;
		}
		
		//Caso o número seja maior que as propoções, a faixa da roleta e a faixa do valor vazio
		if(indice==proporcao.length)
			indice=-1;
		

		double operador = AtributoNominal.igual;
		
		corpo[pos].atualizar(indice, operador);
	}
	
	/**
	 * Preenche um atributo numerico com um novo valor calculado de forma aleatoria.
	 * Define os limites inicias como o meio do intervalo.
	 * @param pos
	 * @param att
	 * @param val
	 */
	public void preencherAtributoNumericoAleatorio(int pos, Atributo a){
		AtributoNumerico atributo = (AtributoNumerico) a;
		//Probabilidade de se escolher um atributo vazio
		double temp = Math.random();
		//Valor definido atraves de uma constante. Se numero aleatorio for menor
		//que a proabilidade de vazio, entao preencher o atributo com o valor vazio
		
		//double probabilidade = Math.random();
		//if(temp < probabilidade){
		if(temp < AtributoNumerico.probabilidaVazioNumerico){
			double vazio = Double.MIN_VALUE;
			//Passa o valor mínimo para a funcao atualizar, que configura o atributo como vazio
			corpo[pos].atualizar(vazio, vazio);
		}else{
			 
			double limiteMinimo = atributo.limiteMinimo;
			double limiteMaximo = atributo.limiteMaximo;
	
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
				corpo[pos].atualizar(lim1, lim2);
			} else {
				corpo[pos].atualizar(lim2, lim1);
			}
			
		}
			
	}
	
	/**
	 * Método que verifica se a regra cobre o exemplo passado como parâmetro
	 * @param c Array com os valores do exemplo
	 * @return Se a regra cobre ou não o exemplo
	 */
	public boolean compararCorpo(double[] c){
		for(int i = 0; i<corpo.length ; i++){
			Atributo atributo = corpo[i];
			if(!atributo.isVazio()){
				if(!atributo.compararValor(c[i]))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Método que define se a regra cobre corretamento o exemplo 
	 * @param exemplo Exemplo que será verificado se a regra o cobre ou não.
	 * @return
	 */
	public boolean cobreCorretamente(Instance exemplo){
		if(compararCorpo(exemplo.toDoubleArray()))
			if(exemplo.classValue() == cabeca)
				return true;
			else
				return false;
		else
			return false;
	}
	
		
	/**
	 * Método que verifica se a regra passa como parâmetro é igual nos atributo discreto e se todos
	 * atributos numericos contem um intervalo que se cruzem
	 * @param r2 Regra a ser compararada com a atual
	 * @return true se as regras se cruzam, false se são diferentes
	 */
	public boolean verificarIntervalos(Regra r2){
		if(this.cabeca != r2.cabeca)
			return false;
		for(int i = 0; i<corpo.length; i++){
			Atributo at1 = this.corpo[i];	
			Atributo at2 = r2.corpo[i];
			//Se o atributo das duas regras forem vazio o atributo tem o valor igual
			//Se uma é vazio e a outra não as regras são diferentes
			//Se as duas não são vazias, deve ser comparado o valor dos atributos.
			if(at1.isVazio()){
				if(!at2.isVazio())
					return false;
			} else{
				if(at2.isVazio())
					return false;
				else{
					if(at1.isNominal()){
						double[] v1 = at1.getValores();
						double[] v2 = at2.getValores();
						if((v1[0]!= v2[0]) ||(v1[1]!= v2[1]))
							return false;
					} else{
						double[] v1 = at1.getValores();
						double[] v2 = at2.getValores();
						
						if((v1[1]<v2[0]) || v2[1]<v1[0])
							return false;
					}
					
				}
			}
		}
		
		return true;
		
	}

	/**
	 * Método que faz um merge da regra passada como parâmetro com a regra atual.
	 * Só executa o merge nos atributos númericos, os atributos discretos ficam iguais
	 * à regra atual. Deve ser usada após a verificação se as retas se cruzam.
	 * @param r2 Regra a ser aglutinada com a atual
	 * @return true casa haja um merge, false caso nao 
	 */
	public boolean merge(Regra r2){
		boolean c = this.verificarIntervalos(r2);
		//Caso as regras se cruzem, elas podem ser juntas.
		if(c){
			for(int i = 0; i<corpo.length; i++){
				Atributo at1 = this.corpo[i];	
				if(at1.isNumerico()){
					Atributo at2 = r2.corpo[i];
					if(!at1.isVazio()){
						double[] v1 = at1.getValores();
						double[] v2 = at2.getValores();
						
						//Se o limite inferior da regras passada como parametro for menor, 
						//o limite da regra atual é alterado
						if(v2[0]<v1[0])
							v1[0] = v2[0];
						
						//Mesmo procedimento, agora para o limite superior
						if(v2[1]>v1[1])
							v1[1] = v2[1];
						
						at1.atualizar(v1[0],v1[1]);

						
					}
				}
			}
		}
		return c;
	}
	
	public static void main(String[] args) {
		String nomebase = "bupa";
		String caminhoBase = "C:\\Andre\\Mestrado\\oficina\\Bases\\celso\\";
		String arquivo = caminhoBase + nomebase + "\\it0\\" + nomebase + "_data.arff";
		try{
			Reader reader = new FileReader(arquivo);
			Instances dados = new Instances(reader);
			dados.setClassIndex(dados.numAttributes()-1);
			//Regra regra = Regra.gerarRegraAleatoria(dados.enumerateAttributes(), dados.classAttribute(), dados.numAttributes());
			//System.out.println(regra);
		} catch (Exception e){e.printStackTrace();}
		
	}
	

}

