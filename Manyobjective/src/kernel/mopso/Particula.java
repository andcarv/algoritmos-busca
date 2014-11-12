package kernel.mopso;

import pareto.FronteiraPareto;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;


/**
 * Classe que representa uma part�cula do MOPSO-N
 * @author Andr� B. de Carvalho
 *
 */
public class Particula {
	
	//Solu��o a qual a part�cula representa
	public SolucaoNumerica solucao = null;
	//Velocidade atual da part�cula
	public double[] velocidade = null;
	//Posi��o atual da part�cula
	public double[] posicao = null;
	
	//Melhor local da part�cula
	public double[] localBest = null;
	//Valores dos objestivos para o melhor local
	public double[] localBestObjetivos = null;
	//Melhor global da part�cula
	public double[] globalBest = null;
	
	
	public Solucao globalBestSolucao = null;
	
	//Coeficientes da equacao do calculo da velocidade
	public double phi1;
	public double phi2;
	
	public double c1;
	public double c2;
	
	//Valores que definem um limite m�ximo para a velocidade da part�cula
	public double[] limitesMaxVelSup;
	public double[] limitesMaxVelInf;
	//Limites que definem o espa�o de busca dos atributos da part�cula
	public double[] limitesPosicaoInferior;
	public double[] limitesPosicaoSuperior;
	
	
	                        
	                        
	
	
	public double omega;
	//Limites da atualiza��o dos coeficientes
	private final double MAX_PHI = 1;
	private final double MAX_OMEGA = 0.8;
	private final double REDUCAO_VELOCIDAE = 0.001;

	public boolean mutacao;
	
	Problema problema = null;
	
	public Particula(){
	}
	
	
	/**
	 * M�todo que inicializa a particula de forma aleatoria
	 * 
	 */
	public void iniciarParticulaAleatoriamente(Problema prob, SolucaoNumerica s){
		solucao = s;
		posicao = localBest = s.getVariaveis();
		velocidade = new double[posicao.length];
		inicializarVelocidadeAleatoria();
			
		//Obtendo os valores de phi aleatoriamente, com valor maximo MAX_PHI
		phi1 = (Math.random()*10) % MAX_PHI;
		phi2 = (Math.random()*10) % MAX_PHI;
		
		//Obtendo o valor de omega aleatoriamente, com valor maximo MAX_OMEGA
		omega = (Math.random()) % MAX_OMEGA;
		
		problema = prob;
		mutacao = false;
		
		limitesMaxVelInf = new double[velocidade.length];
		limitesMaxVelSup = new double[velocidade.length];
		
		limitesPosicaoInferior = new double[posicao.length];
		limitesPosicaoSuperior = new double[posicao.length];
		
		setLimites();
		
		c1 = c2 = 2.05;
		
	}
	
	/**
	 * M�todo que inicia a velocidade da part�cula  de forma aleat�ria
	 *
	 */
	public void inicializarVelocidadeAleatoria(){
		for(int i = 0; i<velocidade.length; i++){
			//double xi = rand.nextDouble();
			double v = Math.random();
			velocidade[i] = v;
		}
	}
	
	/**
	 * M�todo que seta of limites superiores e inferiores para cada posicao do vetor velocidade
	 */
	public void setLimites(){
		for (int i = 0; i < limitesMaxVelSup.length; i++) {
			limitesMaxVelSup[i] = 5;
			limitesMaxVelInf[i] = -5;
			
			limitesPosicaoInferior[i] = 0;
			limitesPosicaoSuperior[i] = 1;
			
			
			
		}
		
		
	}
	
	
	/**
	 * Funcao que retorna o valor da phi1. Phi1 pode variar de acordo com o n�mero da itera��o
	 * @param iteracao N�mero da itera��o da execu��o do algoritmo 
	 * @return Valor de phi1
	 */
	public double getPhi1(int iteracao){
		//Valor de phi1 aleatorio. Pode-se utilizar uma fun��o que varia o valor de phi de acordo com aitera��o
		return phi1 = (Math.random()*10) % MAX_PHI;
	}
		
	/**
	 * Funcao que retorna o valor da phi1. Phi2 pode variar de acordo com o n�mero da itera��o
	 * @param iteracao N�mero da itera��o da execu��o do algoritmo 
	 * @return Valor de phi2
	 */
	public double getPhi2(int iteracao){
		//Valor de phi2 aleatorio. Pode-se utilizar uma fun��o que varia o valor de phi de acordo com aitera��o
		return phi2 = (Math.random()*10) % MAX_PHI;
	}
	
	/**
	 * Funcao que retorna o valor aleatorio de phi1 [0,1].  
	 * @return Valor de phi1
	 */
	public double getPhi1(){
		return phi1 = Math.random() % MAX_PHI;
	}
		
	/**
	 * Funcao que retorna o valor aleatorio de phi2 [0,1].  
	 * @return Valor de phi2
	 */	public double getPhi2(){
		return phi2 = Math.random() % MAX_PHI;
	}
	 
	 public double getC1(){
		 //return c1 = 2.05;
		 return c1 = 1.5 + Math.random();
	 }
	 
	 public double getC2(){
		 //return c2 = 2.05;
		 return c2 = 1.5 + Math.random();
	 }
	 
	 /*public double getFi(){
		 if(c1+c2>4)
			 return c1+c2;
		 else return 1;
	 }*/
	 
	 public double getFi(){
		
		 return Math.max(c1+c2, 4.1);
		
	 }
	
	/**
	 * Atualiza o valor de omega aleatoriamente
	 * @return
	 */
	public double getOmega(){
		return omega = (Math.random()) % MAX_OMEGA;
	}
	
	
	/**
	 * Calculo da dist�nca Euclidiana entre dois vetores de mesmo tamanho
	 * @return Valor da dist�ncia Euclidiana entre os vetores
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
	 * M�todo que c�lcula a nova velocidade da particula
	 * @param iter N�mero da itera��o da execu��o do algoritmo
	 */
	public void calcularNovaVelocidade(){
		//omega*velocidade
		double[] parte1 = multiplicacao(getOmega(),velocidade);
		//phi1*(localBest - posicao);
		double[] parte2 = multiplicacao(c1*getPhi1(),soma(localBest, posicao, -1));
		//phi2*(globalBest - posicao);
		double[] parte3 = multiplicacao(c2*getPhi2(),soma(globalBest, posicao, -1));
		double[] parte4 = soma(parte2,parte3,1);
		double[] parte5 = soma(parte1,parte4,1);
		
		velocidade = parte5;
		
		for (int i = 0; i < velocidade.length; i++) {
			System.out.print(velocidade[i] + "\t");
		}
		
		System.out.println();
		
		
	}
	
	/**
	 * M�todo de c�lculo da velocidade com limita��o do valor da velocidade
	 * Utilizado no algoritmo SMOPSO
	 */
	public void calcularNovaVelocidadeConstriction(){
		
		//omega*velocidade
		double[] parte1 = multiplicacao(getOmega(),velocidade);
		//phi1*(localBest - posicao);
		double[] parte2 = multiplicacao(getC1()*getPhi1(),soma(localBest, posicao, -1));
		//phi2*(globalBest - posicao);
		double[] parte3 = multiplicacao(getC2()*getPhi2(),soma(globalBest, posicao, -1));
		double[] parte4 = soma(parte2,parte3,1);
		double[] parte5 = soma(parte1,parte4,1);
		
		double fi = getFi();
		double raiz = fi*fi - 4*fi;
		
		double X = Math.abs(2.0/(2-fi-Math.sqrt(raiz)));
				
		
		velocidade = multiplicacao(X, parte5);
		
		
		for (int i = 0; i < velocidade.length; i++) {
			double deltai = (limitesMaxVelSup[i]-limitesMaxVelInf[i])/2;
			if(velocidade[i]>deltai)
				velocidade[i] = deltai;
			else{
				if(velocidade[i]<=(deltai*-1))
					velocidade[i] = deltai*-1;
			}
			
		}	
		
		/*for (int i = 0; i < velocidade.length; i++) {
			System.out.print(velocidade[i] + "\t");
		}
		
		System.out.println();*/
	}
	
	/**
	 * M�todo que calcula a nova posi��o da part�cula. Soma a posi��o � velocidade e limita o valor da velocidade
	 *
	 */
	public void calcularNovaPosicao(){
		posicao = soma(posicao, velocidade, 1);
		
		/*for (int i = 0; i < posicao.length; i++) {
			solucao.variaveis[i] = posicao[i];
		}*/
	}

	
	/**
	 * M�todo que trunca os valores da posi��o da part�cula caso eles extrapolem os limites
	 */
	public void truncar() {
	
		for (int i = 0; i < posicao.length; i++) {
			solucao.setVariavel(i, posicao[i]);
		}
		
		boolean over_limits = solucao.truncar();
		
		for (int i = 0; i < posicao.length; i++) {
			 posicao[i] = solucao.getVariavel(i);
		}
		
		//Caso algum limite seja extrapolado, a velocidade eh reduzida em 0.001
		if(over_limits){
			for (int i = 0; i < velocidade.length; i++) {
				velocidade[i] = velocidade[i] * REDUCAO_VELOCIDAE;
				
			}
		}
	}

	/**
	 * M�todo que soma ou subtrai dois vetores
	 * @param vetor1 Primeiro vetor da soma
	 * @param vetor2 Segundo vetor da soma
	 * @param fator Fator que ir� defini se ser� uma soma ou subtra��o dos vetores
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
	 * M�todo que executa a multiplica��o de um vetor por um escalar
	 * @param k Escalar da multiplica��o
	 * @param vetor1 Vetor da multiplica��o
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
	

	
	
	
	
	/**
	 * M�todo que verifica se os novos valores dos objetivos da part�cula dominam o melhor local.
	 * Caso sim os novos objetivos s�o setados.
	 *
	 */
	public void escolherLocalBest(FronteiraPareto pareto){
		double[] objetivos = solucao.objetivos;
		//Compara os objetivos atuais com os valores guardados para o melhor local
		int retorno = pareto.compareObjectiveVector(objetivos,localBestObjetivos);
		//Se o melhor local eh dominado, entao eh feita a atualizacao do lider
		if(retorno == FronteiraPareto.DOMINATES){
			localBestObjetivos = objetivos;
			localBest = posicao;
		} else{
			if(retorno == FronteiraPareto.EQUALS || retorno == FronteiraPareto.NON_DOMINATED){
				//Se nao ha relacao de dominancia, o lider locao eh escolhido de forma aleatoria, entre os dois valoes
				double rand = Math.random();
				if(rand<0.5){
					localBestObjetivos = objetivos;
					localBest = posicao;
				}
			}
			
		}
	}
	
	/**
	 * M�todo que verifica se duas part�culas s�o iguais.
	 * Utiliza o equal da Solucao.
	 */
	public boolean equals(Object o){
		Particula p = (Particula) o;
		return solucao.equals(p.solucao);
		
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
		
		str.append("Objetivos: <");
		for(int i = 0; i< solucao.objetivos.length; i++){
			str.append(solucao.objetivos[i] + ", ");
		}
		
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Objetivos Best: <");
		for(int i = 0; i< globalBestSolucao.objetivos.length; i++){
			str.append(globalBestSolucao.objetivos[i] + ", ");
		}
		
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Rank: " + solucao.rank);
		
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
		
		novaParticula.solucao = (SolucaoNumerica)solucao.clone();
		problema.calcularObjetivos(solucao);
		
		return novaParticula;
	}
	
	public void atualizarSolucao(){
		for (int i = 0; i < posicao.length; i++) {
			solucao.setVariavel(i, posicao[i]);
		}
	}
	

}

