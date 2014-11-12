package solucao;


/**
 * Classe que representa uma solu��o formada por um vetor de numeros reais
 * @author andre
 *
 */
public class SolucaoNumerica extends Solucao {
	
	//Array contendo as variaves da solucao
	private double[] variaveis;
	
	//Variaveis utilizadas para o c�lculo dos objetivos dos problemas DTLZ
	//public double[] xm;
	public int k;
	
	
	//Vari�vel que marca se a solu��o foi aceita na inser��o da popula��o secund�ria do MISA
	public boolean aceita;
	
	
	//Limites que definem o espa�o de busca dos atributos da part�cula
	public double[] limitesPosicaoInferior;
	public double[] limitesPosicaoSuperior;
	
	
	
	
	/**
	 * Construtor da classe
	 * @param n Numero de variaveis
	 * @param m Numero de objetivos da busca
	 */
	public SolucaoNumerica(int n, int m){
		super(n,m);
		variaveis = new double[n];
		//Calcula o valor k, que eh o numero de variaveis nao utilizadas nas funcoes objetivos
		k = n - m +1;
		//Array que contem o resto das variaveis nao utilizadas nas funcoes objetivos
		
		limitesPosicaoInferior = new double[variaveis.length];
		limitesPosicaoSuperior = new double[variaveis.length];
		
		setLimites();
		
	}
	
	public SolucaoNumerica(int n, int m, double[][] limit_search_space){
		super(n,m);
		variaveis = new double[n];
		//Calcula o valor k, que eh o numero de variaveis nao utilizadas nas funcoes objetivos
		k = n - m +1;
		//Array que contem o resto das variaveis nao utilizadas nas funcoes objetivos
		
		limitesPosicaoInferior = new double[variaveis.length];
		limitesPosicaoSuperior = new double[variaveis.length];
		
		setLimites(limit_search_space);
		
	}
	
	/**
	 * M�todo que seta of limites superiores e inferiores para cada posicao do vetor velocidade
	 */
	public void setLimites(){
		for (int i = 0; i < limitesPosicaoInferior.length; i++) {
			
			limitesPosicaoInferior[i] = 0;
			limitesPosicaoSuperior[i] = 1;
		}
	}
	
	public void setLimites(double[][] limit_search_space){
		for (int i = 0; i < n; i++) {
			limitesPosicaoInferior[i] =  limit_search_space[0][i];
			limitesPosicaoSuperior[i] = limit_search_space[1][i];
		}
	}
	
	
	
	//Metodo que gera uma solucao aleatorio
	public void iniciarSolucaoAleatoria(){
		//Random rand = new Random();
		//rand.setSeed(System.currentTimeMillis());
		for(int i = 0; i<variaveis.length; i++){
			//double xi = rand.nextDouble();
			double xi = Math.random();
			variaveis[i] = xi;
		}
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		//buff.append("\nVariaveis: ");
		for (int i = 0; i < variaveis.length; i++) {
			//buff.append(new Double(variaveis[i]).toString().replace('.', ',') + "\t");
			buff.append(variaveis[i] + "\t");
		}
		/*buff.append("\n");
		if(objetivos.length>0){
			for (int i = 0; i < objetivos.length; i++) {
				//buff.append(new Double(variaveis[i]).toString().replace('.', ',') + "\n");
				buff.append(objetivos[i] + "\t");
			}	
		}
		
		//if(rank!=-1)
			buff.append("\nr: " + rank + "\n");
			
			if(combRank!=null)
				for (int i = 0; i < combRank.length; i++) {
					buff.append(combRank[i] + "\t");
				}
			
		
		//buff.append("\n" + crowdDistance + "\t");*/
		
		return buff.toString();
	}
	
	public Object clone(){
		SolucaoNumerica novaSolucao = new SolucaoNumerica(n, m);
		for(int i = 0; i<variaveis.length; i++){
			novaSolucao.variaveis[i] = variaveis[i];
		}
		
		
		for(int i = 0; i<m; i++){
			novaSolucao.objetivos[i] = objetivos[i];
		}
		
		novaSolucao.aceita = aceita;
		novaSolucao.rank = rank;
		novaSolucao.balanceamentoRank = balanceamentoRank;
		novaSolucao.crowdDistance = crowdDistance;
		novaSolucao.numDominacao = numDominacao;
		novaSolucao.numDominadas = numDominadas;
		novaSolucao.diff = diff;
		novaSolucao.ocupacao = ocupacao;
		novaSolucao.S = S;
		novaSolucao.menorDistancia = menorDistancia;
		novaSolucao.guia = guia;
		novaSolucao.knn = knn;
				
		novaSolucao.limitesPosicaoInferior = new double[limitesPosicaoInferior.length];
		novaSolucao.limitesPosicaoSuperior = new double[limitesPosicaoSuperior.length];
		
		for (int i = 0; i < limitesPosicaoInferior.length; i++) {
			novaSolucao.limitesPosicaoInferior[i] = limitesPosicaoInferior[i];
			novaSolucao.limitesPosicaoSuperior[i] = limitesPosicaoSuperior[i];
			
		}
		
		if(sigmaVector !=null ){
			novaSolucao.sigmaVector = new double[sigmaVector.length];

			for (int i = 0; i < sigmaVector.length; i++) {
				novaSolucao.sigmaVector[i] = sigmaVector[i];

			}
		}


		if(objetivosMedio!=null){
			novaSolucao.objetivosMedio = new double[objetivosMedio.length];
			for (int i = 0; i < objetivosMedio.length; i++) {
				novaSolucao.objetivosMedio[i] = objetivosMedio[i];

			}
		}
		
		novaSolucao.used_objectives = new boolean[used_objectives.length];
		for (int i = 0; i < used_objectives.length; i++) {
			novaSolucao.used_objectives[i] = used_objectives[i];
		}
			
		return novaSolucao;
	}
	
	public boolean equals(Object o){
		SolucaoNumerica sol = (SolucaoNumerica) o;
		if(n!=sol.n || m!=sol.m || k!=sol.k)
			return false;
		for (int i = 0; i < variaveis.length; i++) {
			if(sol.variaveis[i]!=variaveis[i])
				return false;
		}
		return true;
		
	}
	
	public boolean truncar() {
		
		boolean over_limits = false;
		//Checa se algum valor da posicao excedeu o limites(numero negativo)

		for (int i = 0; i < variaveis.length; i++) {
			double var_i = variaveis[i];
			double limit_inf_i = limitesPosicaoInferior[i];
			double limit_sup_i = limitesPosicaoSuperior[i];
			if(var_i<limit_inf_i){
				double part1 = limit_sup_i - limit_inf_i;
				double part2 = Math.abs(limit_inf_i - var_i);
				double part3 = part2 % part1;
				//double part3 = Math.min(limitesPosicaoSuperior[i], part2);
				double part4 =  limit_sup_i - part3;
				variaveis[i] = part4;
				over_limits = true;
			}
			if(var_i>limit_sup_i){
				double part1 = limit_sup_i - limit_inf_i;
				double part2 = Math.abs(var_i - limit_sup_i);
				double part3 = part2 % part1;
				double part4 =  part3 + limit_inf_i;
				variaveis[i] = part4;
				over_limits = true;
			}
			
		}
		return over_limits;
	}
	
	public void setVariavel(int i, double valor){
		variaveis[i] = valor;
	}
	
	public double getVariavel(int i){
		return variaveis[i];
	}
	
	public double[] getVariaveis(){
		return variaveis;
	}
	
	public void setVariaveis(double[] var){
		variaveis = var;
	}
	
	public  boolean  isNumerica(){
		return true;
	}
	
	public  boolean  isBinaria(){
		return false;
	}
	
	
	

}
