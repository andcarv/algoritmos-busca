package nuvemparticulas;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.FronteiraPareto;

import objetivo.FuncaoObjetivo;

import regra.Atributo;
import regra.AtributoCombinado;
import regra.AtributoNominal;
import regra.AtributoNumerico;
import regra.Regra;

/**
 * Classe que representa uma partícula do MOPSO-N
 * @author André B. de Carvalho
 *
 */
public class Particula {
	
	//Regra a qual a partícula representa
	public Regra regra = null;
	//Velocidade atual da partícula
	public double[] velocidade = null;
	//Posição atual da partícula
	public double[] posicao = null;
	
	//Melhor local da partícula
	public double[] localBest = null;
	//Valores dos objestivos para o melhor local
	public double[] localBestObjetivos = null;
	//Melhor global da partícula
	public double[] globalBest = null;
	//Valor da distância sigma da partícula
	public double[] sigmaVector = null;
	
	//Coeficientes da equação do cálculo da velocidade
	public double phi1;
	public double phi2;
	
	public final double c1 = 2.05;
	public final double c2 = 2.05;
	public double X = 0.72984;
	
	private double omega;
	//Limites da atualização dos coeficientes
	private final double MAX_PHI = 4;
	private final double MAX_OMEGA = 0.8;
	
	public Particula(){
	}
	
	
	/**
	 * Método que inicializa a particula de forma aleatoria
	 * @param objetivos ArrayList de funções objetivos.
	 * @param atts Enumeration que contem as informações dos atributos
	 */
	public void iniciarParticulaAleatoriamente(ArrayList<FuncaoObjetivo> objetivos, Regra r){
		regra = r;
		posicao = localBest = regra.obterCorpoArray();
		velocidade = new double[posicao.length];
		inicializarVelocidadeAleatoria();
			
		//Obtendo os valores de phi aleatoriamente, com valor maximo MAX_PHI
		phi1 = (Math.random()*10) % MAX_PHI;
		phi2 = (Math.random()*10) % MAX_PHI;
		
		//Obtendo o valor de omega aleatoriamente, com valor maximo MAX_OMEGA
		omega = (Math.random()) % MAX_OMEGA;
		
	}
	
	/**
	 * Método que inicia a velocidade da partícula  de forma aleatória
	 *
	 */
	public void inicializarVelocidadeAleatoria(){
		int j = 0;
		for(int i = 0; i<regra.corpo.length; i++){
			Atributo atributo = regra.corpo[i];
			if(atributo.isNominal()){
				
				double novoValor = 0 ;
				if(!atributo.isCombinacao())
					novoValor = ((AtributoNominal)atributo).obterNovoValorAleatorio();
				else
					novoValor = ((AtributoCombinado)atributo).obterNovoValorAleatorio();
				velocidade[j++] = novoValor+1;
			} else {
				double[] novoValor = ((AtributoNumerico)atributo).obterNovoValorAleatorio();
				velocidade[j++] = novoValor[0];
				velocidade[j++] = novoValor[1];
			}
		}		
	}
	
	
	/**
	 * Funcao que retorna o valor da phi1. Phi1 pode variar de acordo com o número da iteração
	 * @param iteracao Número da iteração da execução do algoritmo 
	 * @return Valor de phi1
	 */
	public double getPhi1(int iteracao){
		//Valor de phi1 aleatorio. Pode-se utilizar uma função que varia o valor de phi de acordo com aiteração
		return phi1 = (Math.random()*10) % MAX_PHI;
	}
		
	/**
	 * Funcao que retorna o valor da phi1. Phi2 pode variar de acordo com o número da iteração
	 * @param iteracao Número da iteração da execução do algoritmo 
	 * @return Valor de phi2
	 */
	public double getPhi2(int iteracao){
		//Valor de phi2 aleatorio. Pode-se utilizar uma função que varia o valor de phi de acordo com aiteração
		return phi2 = (Math.random()*10) % MAX_PHI;
	}
	
	/**
	 * Funcao que retorna o valor aleatorio de phi1 [0,1].  
	 * @return Valor de phi1
	 */
	public double getPhi1(){
		return phi1 = Math.random()*10;
	}
		
	/**
	 * Funcao que retorna o valor aleatorio de phi2 [0,1].  
	 * @return Valor de phi2
	 */	public double getPhi2(){
		return phi2 = Math.random();
	}
	
	/**
	 * Atualiza o valor de omega aleatoriamente
	 * @return
	 */
	public double getOmega(){

		return (Math.random()) % MAX_OMEGA;
	}
	
	/**
	 * @deprecated Ver calcularSigmaVector()
	 * Método que calcula o sigma vector da particula. Método somente implementado para 2 objetivos
	 * @return Sigma vector
	 */
	public double[] calcularSigmaVector2(){
		double[] objetivos = regra.getValoresObjetivos();
		double denominador = 0;
		if(objetivos.length==2){
			sigmaVector = new double[objetivos.length-1];
			double obj1 = objetivos[0];
			double obj2 = objetivos[1];
			double valor = (obj1*obj1) - (obj2*obj2);
			denominador = (obj1*obj1)+ (obj2*obj2);
			sigmaVector[0] = valor;
		}else{
		sigmaVector = new double[objetivos.length];
		for(int i = 0; i< sigmaVector.length; i++){
			double obj1 = objetivos[i];
			denominador+= (obj1*obj1);
			double obj2 = objetivos[(i+1)% sigmaVector.length] ;
			//(obj1^2) - (obj2^2) 
			double valor = (obj1*obj1) - (obj2*obj2);
			sigmaVector[i] = valor;
		}
		}
		sigmaVector = multiplicacao(1/denominador,sigmaVector);
		return sigmaVector;
	}
	
	/**
	 * Método que cálcula o vetor sigma de acordo com a fórmula proposta por Mostaghim
	 * @return Vetor sigma
	 */
	public double[] calcularSigmaVector(){
		double[] objetivos = regra.getValoresObjetivos(); 
		int tamVetor = (int) combinacao(objetivos.length, 2);
		sigmaVector = new double[tamVetor];
		int  cont = 0;
		for(int i = 0; i<objetivos.length-1; i++){
			for(int j = i+1; j<objetivos.length;j++){
				double obj1 = objetivos[i];
				double obj2 = objetivos[j];
				sigmaVector[cont++] =  calcularSigma(obj1, obj2);
			}
		}
		
		return sigmaVector;
		
	}
	
	/**
	 * Método que cálculo o valor sigma para dois objetivos
	 * @param f1 Objetivo 1
	 * @param f2 Objetivo 2
	 * @return Valor da função sigma
	 */
	public double calcularSigma(double f1, double f2){
		double valor = (f1*f1) - (f2*f2);
	
		double denominador = (f1*f1)+ (f2*f2);
		if(denominador!=0)
			return  valor/denominador;
		else
			return 0;
	}
	
	/**
	 * Cálcula a combinação de m, n a n.
	 * @param m 
	 * @param n
	 * @return Combinação (m n) 
	 */
	public double combinacao(int m, int n){
		if(n==m)
			return 1;
		else{
			double fatM = fatorial(m);
			double fatN = fatorial(n);
			double fatNM = fatorial(m-n);
			return (fatM)/(fatN*fatNM);
		}
	}
	
	/**
	 * Cálcula o fatorial de n
	 * @param n
	 * @return n!
	 */
	public double fatorial(int n){
		double fat = 1;
		for(int i = n;i>0;i--){
			fat*=i;
		}
		return fat;
	}
	

	
	
	/**
	 * Método que escolhe qual particula do repositorio sera escolhida como global best
	 * Escolhe a partícula probabilisticamente atraves de uma roleta com os valores da
	 * distância Euclidiana dos sigmaVector
	 * @param repositorio
	 * @return
	 */
	public double[] escolherGlobalBest(ArrayList<Particula> repositorio){
		double melhorValor = Double.MAX_VALUE;
		Particula gbest = null; 
		//Calcula o valor da distancia euclidia dos sigmaVector de cada particula do repositorio
		//e escolhe a menor
		for (Iterator<Particula> iter = repositorio.iterator(); iter.hasNext();) {
			Particula rep = (Particula) iter.next();
			double temp = distanciaEuclidiana(sigmaVector, rep.sigmaVector);
			if(temp<melhorValor){
				melhorValor = temp;
				gbest = rep;
			}
		}

		globalBest = gbest.posicao;
		
		return globalBest;
	}
	
	/**
	 * Cálculo da distânca Euclidiana entre dois vetores de mesmo tamanho
	 * @return Valor da distância Euclidiana entre os vetores
	 */
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for(int i = 0; i<vetor1.length; i++){
			double diferenca = Math.pow(vetor1[i]-vetor2[i],2);
			soma +=diferenca;
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * Método que cálcula a nova velocidade da particula
	 * @param iter Número da iteração da execução do algoritmo
	 */
	public void calcularNovaVelocidade(int iter){
		//omega*velocidad
		double[] parte1 = multiplicacao(omega,velocidade);
		//phi1*(localBest - posicao);
		double[] parte2 = multiplicacao(c1*getPhi1(),soma(localBest, posicao, -1));
		//phi2*(globalBest - posicao);
		double[] parte3 = multiplicacao(c2*getPhi2(),soma(globalBest, posicao, -1));
		double[] parte4 = soma(parte2,parte3,1);
		double[] parte5 = soma(parte1,parte4,1);
		double[] parte6 = mod(parte5);
		velocidade = truncar(parte6);
		
	}
	
	public void calcularNovaVelocidadeConstriction(){
		
		//c1*phi1*(localBest - posicao)
		double[] parte1 = multiplicacao(c1*getPhi1(),soma(localBest, posicao, -1));
		//c2*phi2*(globalBest - posicao)
		double[] parte2 = multiplicacao(c2*getPhi2(),soma(globalBest, posicao, -1));
		//c1*phi1*(localBest - posicao) + c2*phi2*(globalBest - posicao)
		double[] parte3 = soma(parte1,parte2,1);
		//v + c1*phi1*(localBest - posicao) + c2*phi2*(globalBest - posicao)
		double[] parte4 = soma(velocidade,parte3,1);
		//X(v + c1*phi1*(localBest - posicao) + c2*phi2*(globalBest - posicao))
		double[] parte5 = multiplicacao(X, parte4); 
		double[] parte6 = mod(parte5);
		velocidade = truncar(parte6);
		
	}
	
	/**
	 * Método que calcula a nova posição da partícula. Soma a posição à velocidade e a aplica
	 * os operadores do MOD
	 *
	 */
	public void calcularNovaPosicao(){
		posicao = mod(soma(posicao, velocidade, 1));
		int j = 0;
		for (int i = 0; i < regra.corpo.length; i++) {
			Atributo atributo = regra.corpo[i];
			if(atributo.isNominal()){
				//Adição de 1 nos valores dos atributos disponibilizados pelo weka.
				if(posicao[j]<0)
					posicao[j] = 0;
				++j;
			} else{
				AtributoNumerico a = (AtributoNumerico) atributo;
				double linf = posicao[j];
				double lsup = posicao[j+1];
				if(linf>lsup){
					double temp = linf;
					linf = lsup;
					lsup = temp;
				}
				double novoLiminteInf = linf;
				double novoLiminteSup = lsup;
				//Novos valores dos limites fora do intervalo
				if(linf>a.limiteMaximo || lsup<a.limiteMinimo ||(linf<a.limiteMinimo && lsup>a.limiteSuperior)){
					posicao[j] = a.limiteMinimo;
					++j;
					posicao[j] = a.limiteMinimo;
					++j;
				} else{
				  //Pelo menos um novo valor dentro dos limites
					if(linf<a.limiteMinimo){ 
						double temp = a.limiteMaximo - Math.abs(a.limiteMinimo - linf);
						novoLiminteInf = Math.max(temp,a.limiteMinimo);
					}
					
					if(lsup>a.limiteMaximo){
						double temp = a.limiteMinimo + Math.abs(lsup- a.limiteMaximo);
						novoLiminteSup = Math.min(temp,a.limiteMaximo);
					}
					if(novoLiminteInf>novoLiminteSup){
						double temp = novoLiminteInf;
						novoLiminteInf = novoLiminteSup;
						novoLiminteSup = temp;
					}
					posicao[j] = novoLiminteInf;
					++j;
					posicao[j] = novoLiminteSup;
					++j;
				}
				
			}
		}

		regra.preencherRegraArray(posicao);
	}
	
	/**
	 * Método que soma ou subtrai dois vetores
	 * @param vetor1 Primeiro vetor da soma
	 * @param vetor2 Segundo vetor da soma
	 * @param fator Fator que irá defini se será uma soma ou subtração dos vetores
	 * @return Vetor resultante
	 */
	public double[] soma(double[] vetor1, double[] vetor2, double fator){
		double soma = 0;
		double[] retorno = new double[vetor1.length];
		
		for (int i = 0; i < vetor1.length; i++) {
			soma = vetor1[i] +(fator*vetor2[i]);
			retorno[i] = soma;
		}
		return retorno;
	}
	
	/**
	 * Método que executa a multiplicação de um vetor por um escalar
	 * @param k Escalar da multiplicação
	 * @param vetor1 Vetor da multiplicação
	 * @return Vetor resultante
	 */
	public double[] multiplicacao(double k, double[] vetor){
		double mult = 0;
		double[] retorno = new double[vetor.length];
		for (int i = 0; i < vetor.length; i++) {
			mult = vetor[i] * k;
			retorno[i] = mult;
		}
		return retorno;
	}
	
	public double[] mod(double[] vetor){
		double[] retorno = new double[vetor.length];
		int j = 0;
		for (int i = 0; i < regra.corpo.length; i++) {
			Atributo atributo = regra.corpo[i];
			if(atributo.isNominal()){
				//Adição de 1 nos valores dos atributos disponibilizados pelo weka.
				retorno[j] = vetor[j] % (atributo.att.numValues()+1);
				++j;
			} else{
				retorno[j] = vetor[j];
				++j;
				retorno[j] = vetor[j];
				++j;
			}
		}
		
		return retorno;
	}
	
	/**
	 * Método que trunca os valores do vetor
	 * @param vetor Vetor de entrada
	 * @return Vetor de retorno
	 */
	public double[] truncar(double[] vetor){
		double[] retorno = new double[vetor.length];
		int j = 0;
		for (int i = 0; i < regra.corpo.length; i++) {
			Atributo atributo = regra.corpo[i];
			if(atributo.isNominal()){
				retorno[j] = Math.floor(vetor[j]);
				++j;
			} else {
				retorno[j] = vetor[j];
				++j;
				retorno[j] = vetor[j];
				++j;
			}
		}
		return retorno;
	}
	/**
	 * Método que verifica se os novos valores dos objetivos da partícula dominam o melhor local.
	 * Caso sim os novos objetivos são setados.
	 *
	 */
	public void escolherLocalBest(){
		double[] objetivos = regra.valoresObjetivos;
		int retorno = FronteiraPareto.compararMedidas(objetivos,localBestObjetivos);
		if(retorno == 1){
			localBestObjetivos = objetivos;
			localBest = posicao;
		}
	}
	
	/**
	 * Método que verifica se duas partículas são iguais.
	 * Utiliza o equal das regras.
	 */
	public boolean equals(Object o){
		Particula p = (Particula) o;
		boolean generic = this.regra.isGeneric(p.regra);
		
		//Caso as regras sejam iguais, ou uma regra é generica em relação a outra
		if(this.regra.equals(p.regra) || generic)
			return true;
		else
			return false;
		
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append("Posicao: <");
		for(int i = 0; i< posicao.length; i++){
			str.append(posicao[i] + ", ");
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Velocidade: <");
		for(int i = 0; i< velocidade.length; i++){
			str.append(velocidade[i] + ", ");
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Local Best: <");
		for(int i = 0; i< localBest.length; i++){
			str.append(localBest[i] + ", ");
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		if(globalBest!=null){
		str.append("Global Best: <");
		for(int i = 0; i< globalBest.length; i++){
			str.append(globalBest[i] + ", ");
		}
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		str.append(regra);
		return str.toString();
	}
	
	public Object clone(){
		Particula novaParticula = new Particula();
		novaParticula.posicao = new double[posicao.length];
		novaParticula.velocidade = new double[velocidade.length];
		if(localBest!=null)
			novaParticula.localBest = new double[localBest.length];
		if(localBestObjetivos!=null)
			novaParticula.localBestObjetivos = new double[localBestObjetivos.length];
		if(globalBest!=null)
			novaParticula.globalBest = new double[globalBest.length];
		
		
		for (int i = 0; i < novaParticula.posicao.length; i++) {
			novaParticula.posicao[i] = posicao[i];
			novaParticula.velocidade[i] = velocidade[i];
			if(localBest!=null)
				novaParticula.localBest[i] = localBest[i];
			if(globalBest!=null)
				novaParticula.globalBest[i] = globalBest[i];
		}
		
		if(localBestObjetivos!=null){
			for (int i = 0; i < localBestObjetivos.length; i++) {
				novaParticula.localBestObjetivos[i] = localBestObjetivos[i];
			}
		}
		
		novaParticula.regra = (Regra)regra.clone();
		novaParticula.regra.calcularValoresObjetivos();
		
		return novaParticula;
	}
	
	

}

