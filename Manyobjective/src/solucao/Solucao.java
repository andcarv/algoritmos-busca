package solucao;


/**
 * Classe que representa uma solu��o formada por um vetor de numeros reais
 * @author andre
 *
 */
public abstract class Solucao {
	
	
	public int n, m;
	
	public double[] objetivos;
	
	//Conta por qts solucoes a solucao e dominada
	public double numDominacao;
	
	//Conta quantas solucoes ela domina
	public double numDominadas;
		
	public double crowdDistance;
	
	public double rank = -1;
	//Used on BalancedRank
	public double balanceamentoRank = -1;
	
	public double[] combRank = null;
	
	//highest distance between each objective value
	public double diff;
	
	public int ocupacao;

	//Valor da distancia sigma da solucao
	public double[] sigmaVector = null;
	
	//Used on leader's choose Oposto
	public double[] objetivosMedio;
	
	public double S;
	
	//public int indice;
	
	//Used on methods that calculate some distance between the solutions 
	public double menorDistancia;
	
	//Used on KNN archiver (SPEA2)
	public double knn;
	
	//Used on some archiving methods
	public int guia;
	
	public boolean[] used_objectives;
	
	
	/**
	 * Construtor da classe
	 * @param n Numero de variaveis
	 * @param m Numero de objetivos da busca
	 */
	public Solucao(int n, int m){
		this.n = n;
		this.m = m;
		
		objetivos = new double[m];
		
		used_objectives = new boolean[m];
		
		for (int i = 0; i < used_objectives.length; i++) {
			used_objectives[i] = true;
		}
	
	}
	
	public abstract boolean  isNumerica();
	
	public abstract boolean  isBinaria();
	
	
	//Metodo que gera uma solucao aleatorio
	public abstract void iniciarSolucaoAleatoria();
	
	/**
	 * M�todo que c�lcula o vetor sigma de acordo com a f�rmula proposta por Mostaghim
	 * @return Vetor sigma
	 */
	public double[] calcularSigmaVector(){
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
	 * C�lcula a combina��o de m, n a n.
	 * @param m 
	 * @param n
	 * @return Combina��o (m n) 
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
	 * C�lcula o fatorial de n
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
	 * M�todo que c�lculo o valor sigma para dois objetivos
	 * @param f1 Objetivo 1
	 * @param f2 Objetivo 2
	 * @return Valor da fun��o sigma
	 */
	public double calcularSigma(double f1, double f2){
		double valor = (f1*f1) - (f2*f2);
	
		double denominador = (f1*f1)+ (f2*f2);
		if(denominador!=0)
			return  valor/denominador;
		else
			return 0;
	}
	
	public void setVetorObjetivosMedio(){
		double valorMedio = 0;
		for(int i = 0 ;i<objetivos.length; i++){
			valorMedio+=objetivos[i];
		}
		valorMedio = valorMedio/objetivos.length;
		
		objetivosMedio = new double[objetivos.length];
		for(int i= 0 ; i <objetivosMedio.length; i++){
			objetivosMedio[i] = objetivos[i] - valorMedio;
		}			
	}
	
	public void setDiferenca(){
		double maiorValor = 0;
		double menorValor = Double.MAX_VALUE;
		
		//Procura o maior e o menor valor dos objetivos
		for (int i = 0; i < objetivos.length; i++) {
			double d = objetivos[i];
			if(d > maiorValor)
				maiorValor = d;
			if(d< menorValor)
				menorValor = d;
		}
		
		diff = maiorValor - menorValor;
		
	}
	
	public void arredondar(){
		double copia[] = new double[objetivos.length];
		for (int i = 0; i < objetivos.length; i++) {
			copia[i] = objetivos[i];
			if(objetivos[i]<0.01)
				objetivos[i] = 0;
		}
		int cont = 0;
		for (int i = 0; i < objetivos.length; i++) {
			if(objetivos[i]==0)
				cont++;
		}
		
		if(cont == objetivos.length)
			System.out.println();
		
		
	}
	
	public void setUsedObjectives(boolean[] uo){
		for (int i = 0; i < uo.length; i++) {
			used_objectives[i] = uo[i];
		}
	}
		
	
	
	

}
