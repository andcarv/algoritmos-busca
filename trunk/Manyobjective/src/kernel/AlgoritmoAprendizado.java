package kernel;



import hyper.HyperplaneReferencePoints;
import indicadores.Convergence;
import indicadores.GD;
import indicadores.IGD;
import indicadores.LargestDistance;
import indicadores.PontoFronteira;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import archive.AdaptiveGridArchiver;
import archive.CrowdingDistanceArchiver;
import archive.DistanceReferencePointsArchiver;
import archive.DistributedArchiver;
import archive.DominatingArchive;
import archive.EpsAPP;
import archive.EpsAPS;
import archive.HyperPlaneReferenceArchive;
import archive.HyperPlaneReferenceArchiveRandom;
import archive.IdealArchiver;
import archive.MGAArchiver;
import archive.PreciseArchiver;
import archive.RandomArchiver;
import archive.RankArchiver;
import archive.SPEA2Archiver;
import archive.UnboundArchive;
import kernel.mopso.Particula;
import pareto.FronteiraPareto;
import principal.Principal;
import problema.Problema;
import rank.AverageRank;
import rank.BalancedDominationRank;
import rank.BalancedRank;
import rank.BalancedRankObj;
import rank.CombinacaoRank;
import rank.MaximumRank;
import rank.Rank;
import rank.RankDominancia;
import rank.SumWeightedGlobalRatios;
import rank.SumWeightedRatios;
import solucao.ComparetorDistancia;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoBinaria;
import solucao.SolucaoNumerica;



public abstract class AlgoritmoAprendizado {
	
	//Number of decision variables
	public int n;
	public Problema problema = null;
	
	//Number of iterations
	public int geracoes;
	//Population size
	public int populationSize;
	
	public int numeroavalicoes;
	
	public FronteiraPareto pareto = null;
	
	public final double PROB_MUT_COD;
	
	public double limite_ocupacao;
	
	//Numero de divisoes do grid adaptativo
	public int partesGrid;
	
	private final double MAX_MUT = 0.5;
	//Flag que indica se algum metodo de rankeamento many-objetivo sera utilizado
	public boolean rank = false;
	public String tipoRank = "";
	
	public Rank metodoRank = null;
	
	public int archiveSize;

	public double eps;
	
	public static Random random = new Random();
	
	public PreciseArchiver archiver = null;
	

	public GD gd;
	public IGD igd;
	public LargestDistance ld;
	public Convergence con;
	
	public PrintStream psGD;
	public PrintStream psIGD;
	public PrintStream psLD;
	public PrintStream psCon;
	
	public boolean eval_analysis = false;
	
	public static int  eval_id = 0;
	
	public boolean[] used_objectives;
	
	/**
	 * 
	 * @param n - numero de variaveis
	 * @param p - problema
	 * @param g - numero geracoes
	 * @param avaliacoes - numero de avaliacoes
	 * @param t - tamanho da populacao
	 * @param tRank - tipo do metodo de ranking
	 * @param eps - valor do epsilon
	 * @param tr - tamanho do repositorio
	 * @param tPoda - tipo do metodo de poda
	 */
	public AlgoritmoAprendizado(int n, Problema p, int g, int avaliacoes, int t, String[] maxmim,String tRank, double eps, int as, String archiveType, boolean eval_analysis){
		this.n = n;
		problema = p;
		geracoes = g;
		populationSize = t;
		PROB_MUT_COD = 1.0/(double)n;
		
		numeroavalicoes = avaliacoes;
		tipoRank = tRank;
		iniciarMetodoRank();
		
		archiveSize = as;
		
		this.eps = eps;
		
		random.setSeed(System.currentTimeMillis());
		
		setArchiver(archiveType);
		
		this.eval_analysis = eval_analysis;
		
		if(eval_analysis)
			initializeEvalAnalysis(maxmim, eval_id++ + "");
		
		used_objectives = new boolean[problema.m];
		for (int i = 0; i < used_objectives.length; i++) {
			used_objectives[i] = true;
		}
	}
	
	public abstract ArrayList<Solucao> executar();
	
	public abstract ArrayList<Solucao> executarAvaliacoes();
	
	
	public static double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public static double distanciaTchebycheff(double[] z, double[] zEstrela, double[] lambda){
		double distancia = 0;
		
		for (int i = 0; i < z.length; i++) {
			distancia = Math.max(distancia, (1.0/lambda[i]) * Math.abs(zEstrela[i] - z[i]));
		}
		
		return distancia;
	}
	
	public static void calcularCrowdingDistance(ArrayList<Solucao> solucoes, int m){
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao =  iterator.next();
			solucao.crowdDistance = 0;
		}
		
		for(int k = 0; k<m; k++){
			ComparetorObjetivo comp = new ComparetorObjetivo(k);
			Collections.sort(solucoes, comp);
			Solucao sol1 = solucoes.get(0);
			Solucao solN = solucoes.get(solucoes.size()-1);
			sol1.crowdDistance = Double.MAX_VALUE;
			solN.crowdDistance = Double.MAX_VALUE;
			for(int i = 1; i<solucoes.size()-1; i++){
				Solucao sol = solucoes.get(i);
				Solucao solProx = solucoes.get(i+1);
				Solucao solAnt = solucoes.get(i-1);
				sol.crowdDistance += solProx.objetivos[k] - solAnt.objetivos[k];
			}
		}
		
	}
	
	public static void calculateKNeareastNeighbour(ArrayList<Solucao> solucoes, int k){
		for (int i = 0; i<solucoes.size();i++) {
			Solucao solucao_i =  solucoes.get(i);
			solucao_i.menorDistancia = Double.MAX_VALUE;
			solucao_i.knn = 0;
			for (int j = 0; j<solucoes.size();j++) {
				if(i!=j){
					Solucao solucao_j =  solucoes.get(j);
					solucao_j.menorDistancia = distanciaEuclidiana(solucao_i.objetivos, solucao_j.objetivos);
				}
			}
			ComparetorDistancia comp = new ComparetorDistancia();
			Collections.sort(solucoes, comp);
			for(int p = 0; p<k; p++){
				solucao_i.knn+= solucoes.get(p).menorDistancia;
			}
		}
	}
	
	/**
	 * Metodo que remove as solucoes que estao num mesmo cuboide com distancia limite_ocupacao
	 * @param solucoes Solucoes que serao refinadas
	 */
	@SuppressWarnings("unchecked")
	public void removerGranular(ArrayList<Solucao> solucoes){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes, problema.m);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimitesCuboide(solucoes);	
				
		ArrayList<Solucao> cloneSolucoes = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<cloneSolucoes.size(); k++){
			Solucao solucao = cloneSolucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				double[] limites = new double[problema.m*2];
				//Define os limites de ocupacao da solucao
				for(int i = 0;i<problema.m;i++){
					//Posicao final (2 * numero do objetivo) = valor do objetivo menos o  limite. (Limite inferior)  
					limites[2*i] = Math.max(solucao.objetivos[i] - limite_ocupacao, 0);
					//Posicao incial (2 * numero do objetivo + 1) = valor do objetivo mais limite. (Limite superior)
					limites[2*i + 1] = solucao.objetivos[i] + limite_ocupacao;
				}
				
				boolean removida = false;
				//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
				for(int i = 0; i<cloneSolucoes.size() && !removida; i++){
					Solucao solucao_i = cloneSolucoes.get(i);
					boolean dentro = true;
					//Se não é a solução corrente
					if(i != k){
						for(int j = 0;j<problema.m && dentro;j++){
							double obj = solucao_i.objetivos[j];
							//Objetivo abaixo do limite inferior
							if(obj < limites[2*j])
								dentro = false;
							//Objetivo acima do limite superior
							if(obj > limites[2*j + 1])
								dentro = false;
						}
						if(dentro){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)
								solucoes.remove(solucao_i);
							else{
								solucoes.remove(solucao);
								removida = true;
							}
						}
					}			
				}
			}
		}
	}
	
	/**
	 * Conta para cada solucao quantas solucoes estao dentro do limite definido por solucao.limiteOcupacao
	 * @param solucoes
	 */
	public void contarSolucoesLimitesCuboide(ArrayList<Solucao> solucoes){

		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);


			double[] limites = new double[problema.m*2];

			solucao.ocupacao = 0;

			for(int i = 0;i<problema.m;i++){
				//Posicao final (2 * numero do objetivo) = valor do objetivo menos o  limite. (Limite inferior)  
				limites[2*i] = Math.max(solucao.objetivos[i] - limite_ocupacao, 0);
				//Posicao incial (2 * numero do objetivo + 1) = valor do objetivo mais limite. (Limite superior)
				limites[2*i + 1] = solucao.objetivos[i] + limite_ocupacao;
			}

			for(int i = 0; i<solucoes.size(); i++){
				Solucao solucao_i = solucoes.get(i);
				boolean dentro = true;
				//Se não é a solução corrente
				if(i != k){
					for(int j = 0;j<problema.m && dentro;j++){
						double obj = solucao_i.objetivos[j];
						//Objetivo abaixo do limite inferior
						if(obj < limites[2*j])
							dentro = false;
						//Objetivo acima do limite superior
						if(obj > limites[2*j + 1])
							dentro = false;
					}
					if(dentro)
						solucao.ocupacao++;
				}			
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public void removerGranularLimites(ArrayList<Solucao> solucoes){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes, problema.m);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimites(solucoes);	
				
		ArrayList<Solucao> cloneSolucoes = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<cloneSolucoes.size()-1; k++){
			Solucao solucao = cloneSolucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				boolean removida = false;
				for(int j = 0;j<problema.m;j++){
					double limite_inf = solucao.objetivos[j] - limite_ocupacao; 
					double limite_sup = solucao.objetivos[j] + limite_ocupacao;
					//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
					for(int i = k+1; i<cloneSolucoes.size() && !removida; i++){
						Solucao solucao_i = cloneSolucoes.get(i);
						//Se não é a solução corrente						
						double obj = solucao_i.objetivos[j];
						if(obj >= limite_inf && obj <= limite_sup){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)							
								solucoes.remove(solucao_i);
							else{
								solucoes.remove(solucao);
								removida = true;
							}
							/*try{
								imprimirFronteira(solucoes,0,"");
							}catch(IOException ex){ex.printStackTrace();}*/
						}
					}			
				}
			}
		}
	}
	
	/**
	 * Para cada solucao, conta quantas solucoes estao presentes nos limites definidos por limite_ocupacao
	 * @param solucoes
	 */
	public void contarSolucoesLimites(ArrayList<Solucao> solucoes){
		
		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);
			solucao.ocupacao = 0;
		}

		for(int k = 0; k<solucoes.size()-1; k++){
			Solucao solucao = solucoes.get(k);
			for(int j = 0;j<problema.m;j++){
				double limite_inf = Math.max(solucao.objetivos[j] - limite_ocupacao, 0); 
				double limite_sup = solucao.objetivos[j] + limite_ocupacao;
				for(int i = k+1; i<solucoes.size(); i++){
					Solucao solucao_i = solucoes.get(i);
					//Se não é a solução corrente
					double obj = solucao_i.objetivos[j];
					//Objetivo abaixo do limite inferior
					if(obj >= limite_inf && obj <= limite_sup){
						solucao.ocupacao++;
						solucao_i.ocupacao++;
					}

				}			
			}
		}

	}
	
	/**
	 * Metodo que remove as solucoes que estao num mesmo cuboide com distancia limite_ocupacao
	 * @param solucoes Solucoes que serao refinadas
	 */
	@SuppressWarnings("unchecked")
	public void removerGranularRaio(ArrayList<Solucao> solucoes){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes, problema.m);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimitesRaio(solucoes, limite_ocupacao);	
				
		ArrayList<Solucao> cloneSolucoes = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<cloneSolucoes.size(); k++){
			Solucao solucao = cloneSolucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				
				boolean removida = false;
				//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
				for(int i = 0; i<cloneSolucoes.size() && !removida; i++){
					Solucao solucao_i = cloneSolucoes.get(i);
					
					if(i != k){
						
						double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
						if(dist<limite_ocupacao){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)
							//if(solucao.ocupacao < solucao_i.ocupacao)
								solucoes.remove(solucao_i);
							else{
								solucoes.remove(solucao);
								removida = true;
							}
						}
						
					}			
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Metodo que remove as solucoes que estejam num mesmo raio de valor limite_ocupacao
	 */
	public ArrayList<Solucao> removerGranularRaio2(ArrayList<Solucao> solucoes, double limite_ocupacao){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes, problema.m);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimitesRaio(solucoes, limite_ocupacao);	
				
		ArrayList<Solucao> retorno = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				
				boolean removida = false;
				//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
				for(int i = 0; i<solucoes.size() && !removida; i++){
					Solucao solucao_i = solucoes.get(i);
					
					if(i != k){
						
						double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
						if(dist<limite_ocupacao){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)
							//if(solucao.ocupacao < solucao_i.ocupacao)
								retorno.remove(solucao_i);
							else{
								retorno.remove(solucao);
								removida = true;
							}
						}
						
					}			
				}
			}
		}
		
		return retorno;
	}
	
	
	public ArrayList<Solucao> removerCDAS(ArrayList<Solucao> solucoes, double S){
		FronteiraPareto pareto2 = new FronteiraPareto(S, pareto.maxmim, pareto.rank, 0, problema, archiveSize);
		
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			if(!pareto2.getFronteira().contains(solucao))
				pareto2.add((Solucao)solucao.clone(), archiver);
		}
		
		return pareto2.getFronteira();
	}
	
		
	public void contarSolucoesLimitesRaio(ArrayList<Solucao> solucoes, double limite_ocupacao){

		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);
			solucao.ocupacao = 0;

			for(int i = 0; i<solucoes.size(); i++){
				Solucao solucao_i = solucoes.get(i);
				//Se não é a solução corrente
				if(i != k){
					
					double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
					if(dist<limite_ocupacao)
						solucao.ocupacao++;
				}			
			}
		}

	}
	
	/**
	 * Muta��o probabil�stica
	 * @param prob_mutacao Probabilidade de efetuar a muta��o em uma posi��o
	 * @param vetor1 Vetor que ir� sofre a muta��o
	 */
	public void mutacaoPolinomial(double prob_mutacao, double[] vetor1){
		for (int i = 0; i < vetor1.length; i++) {
			double pos = vetor1[i];
			double prob = Math.random();
			double delta;
			if(prob<prob_mutacao){
				double u = Math.random();
				if(u<0.5){
					delta = Math.pow(2*u, 1.0/(vetor1.length+1)) - 1;
				} else{
					delta = 1- Math.pow(2*(1-u), 1.0/(vetor1.length+1));
				}
				
			} else
				delta = 0;
			vetor1[i] = Math.max(0,pos + delta*MAX_MUT); 
		}
	}
	
	public void mutacao(double prob_mutacao, Solucao solucao){
		if(solucao.isNumerica())
			mutacaoPolinomialNumerica(prob_mutacao, (SolucaoNumerica)solucao);
		else
			((SolucaoBinaria) solucao).mutacaoSimples(prob_mutacao);
	}
	
	/**
	 * Mutacao probabilistica
	 * @param prob_mutacao Probabilidade de efetuar a muta��o em uma posi��o
	 * @param 
	 */
	public void mutacaoPolinomialNumerica(double prob_mutacao, SolucaoNumerica solucao){
		for (int i = 0; i < solucao.n; i++) {
			double pos = solucao.getVariavel(i);
			double prob = Math.random();
			double delta;
			if(prob<prob_mutacao){
				double u = Math.random();
				if(u<0.5){
					delta = Math.pow(2*u, 1.0/(solucao.n+1)) - 1;
				} else{
					delta = 1- Math.pow(2*(1-u), 1.0/(solucao.n+1));
				}
				
			} else
				delta = 0;
			solucao.setVariavel(i, pos + delta*MAX_MUT); 
		}
	}
	
	/**
	 * M�todo que executa a muta��o simples, pos = pos + random(0,1)*pos
	 * @param prob_mutacao Probabilidade de efetuar a muta��o em uma posi��o
	 * @param vetor1 Vetor que ir� sofre a muta��o
	 */
	public void mutacao(double prob_mutacao, double[] vetor1){
		for (int i = 0; i < vetor1.length; i++) {
			double pos = vetor1[i];
			double prob = Math.random();
			if(prob<prob_mutacao){
				pos += (Math.random() % pos);
				vetor1[i] = pos;
			}
		}	
	}
	
	public void rankParticula(ArrayList<Particula> particulas){
		ArrayList<Solucao> solucoes = new ArrayList<Solucao>();
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			solucoes.add(particula.solucao);
		}
		metodoRank.rankear(solucoes, -1);
		solucoes.clear();
		solucoes = null;
	}
	
	public void iniciarMetodoRank(){
		rank = true;
		
		String[] rs = tipoRank.split("_");
		
		ArrayList<Rank> ranks = new ArrayList<Rank>();
		StringBuffer nomeTemp = new StringBuffer();
		
		for (int i = 0; i < rs.length; i++) {
			String rankTemp = rs[i];
			nomeTemp.append(rankTemp);
			if(rankTemp.equals("ar"))
				metodoRank = new AverageRank(problema.m);
			else{
				if(rankTemp.equals("mr"))
					metodoRank = new MaximumRank(problema.m);
				else{
					if(rankTemp.equals("br"))
						metodoRank = new BalancedRank(problema.m);
					else{
						if(rankTemp.equals("bdr"))
							metodoRank = new BalancedDominationRank(problema.m);
						else{
							if(rankTemp.equals("bro"))
								metodoRank = new BalancedRankObj(problema.m);
							else{
								if(rankTemp.equals("sr"))
									metodoRank = new SumWeightedRatios(problema.m);
								else{
									if(rankTemp.equals("sgr"))
										metodoRank = new SumWeightedGlobalRatios(problema.m);
									else{
										if(rankTemp.equals("nsga"))
											metodoRank = new RankDominancia(problema.m);
										else{
											if(rankTemp.equals("gb"))
												;//metodoRank = new GB(problema.m, "-");
											else
												rank = false;
										}
									}
								}
							}
						}

					}
				}
			}
			
			ranks.add(metodoRank);
		}
		
		if(rs.length>1){
			metodoRank = new CombinacaoRank(problema.m, ranks);
			tipoRank = "comb_"+ nomeTemp;
		}
	}
	
	/*public static void rankear(ArrayList<Solucao> solucoes, Rank metodoRank){
		metodoRank.rankear(solucoes, -1);
	}*/
	


	
	/**
	 * Metodo que busca as solucoes nao dominadas da populacao atual
	 * @return Solucoes nao dominadas da populacao
	 */
	public void encontrarSolucoesNaoDominadas(ArrayList<Solucao> solucoes, FronteiraPareto pareto){
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao solucao =  iter.next();
			pareto.add(solucao, archiver);
		}
	}
	
	
	/**
	 * Metodo que encontra as solucoes nos extremos dos objetivos e a solucao mais proxima a ideal
	 * @param solucoes
	 * @param prox_ideal flag que decide se vai ser escolhida a solucao ideal ou a solucao mais proxima a ideal
	 * @param m Numero de objetivos
	 * @return Array com as solucoes nos extremos e a ideal
	 */
	public static ArrayList<ArrayList<Solucao>> obterSolucoesExtremasIdeais(ArrayList<Solucao> solucoes, boolean prox_ideal, Problema problema){
		
		//double[][] retorno = new double[problema.m+1][problema.m];
		
		ArrayList<ArrayList<Solucao>> extremos2 = new ArrayList<ArrayList<Solucao>>();
		
		for(int i = 0; i<problema.m+1; i++){
			ArrayList<Solucao> extremo_i = new ArrayList<Solucao>();
			extremos2.add(extremo_i);
		}
		
		//Solucao[] extremos = new Solucao[problema.m+1];
	
		
		Solucao ideal = new SolucaoNumerica(problema.n, problema.m);
		
		for (int i = 0; i < ideal.objetivos.length; i++) {
			ideal.objetivos[i] = Double.POSITIVE_INFINITY;	
		}
		
		//Obtem a solucoes no extremo e calcula a solucao ideal
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			
			for(int j = 0; j<problema.m;j++){
				if(rep.objetivos[j]<=ideal.objetivos[j]){
					if(rep.objetivos[j]<ideal.objetivos[j])
						extremos2.get(j).clear();
					ideal.objetivos[j] = rep.objetivos[j];
					//extremos[j] = rep;
					extremos2.get(j).add(rep);
				}
			}
		}	
	
		//extremos[problema.m] = ideal;
		extremos2.get(problema.m).add(ideal);
		
		//Busca a solucao mais proxima ideal
		
		if(prox_ideal){			
			double menorDist = Double.MAX_VALUE;
			for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
				Solucao rep = iter.next();
				double distancia = distanciaEuclidiana(rep.objetivos, ideal.objetivos);
				if(distancia<menorDist){
					//extremos[problema.m] = rep;
					menorDist = distancia;
					extremos2.get(problema.m).clear();
					extremos2.get(problema.m).add(rep);
				}



			}
		}
		
		return extremos2;		
	}
	
	/**
	 * Método que obtem para cada solucao a solucao do extremo em que ela esta mais perto
	 * @param extremos Solucoes no extremo e a ideal
	 * @param define qual metodo de distancia sera utilizado, euclidiana ou Tchebycheff, e se usa o vetor objetivos ou vetor sigma
	 * @param solucoes
	 */
	public static void definirDistanciasSolucoesProximasIdeais(ArrayList<ArrayList<Solucao>> extremos2, ArrayList<Solucao> solucoes, String metodoDistancia, Problema problema){
		
		
		double[] lambda = new double[problema.m];
		for (int i = 0; i < lambda.length; i++) {
			lambda[i] = 1;
		}
		
		
		if(metodoDistancia.equals("sigma")){
			for(int i = 0; i<problema.m + 1; i++){
				ArrayList<Solucao> extremos_i = extremos2.get(i);
				for (Iterator<Solucao> iterator = extremos_i.iterator(); iterator
						.hasNext();) {
					Solucao solucao = (Solucao) iterator.next();
					solucao.calcularSigmaVector();
				}
			}
			
			
		}
		
		//Variavel utiliza para identificar como eh definida a proporcao da distribuicao das solucoes
		int[] contador = new int[problema.m+1];
		
		//Percorre todas as solucoes a atribui a cada uma a solucao extrema mais proxima
		//Pode ser utilizada a distancia euclidiana, de tchebycheff ou a distancia dos vetores sigmas
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			if(metodoDistancia.equals("sigma"))
				solucao.calcularSigmaVector();
			solucao.menorDistancia = Double.MAX_VALUE;
			for(int i =0; i<problema.m+1; i++){
				ArrayList<Solucao> extremos_i = extremos2.get(i);
				for (Iterator<Solucao> iterator2 = extremos_i.iterator(); iterator2
						.hasNext();) {
					Solucao solucao_extr_i = (Solucao) iterator2.next();

					double distancia = 0 ;
					if(metodoDistancia.equals("eucli"))
						distancia = distanciaEuclidiana(solucao_extr_i.objetivos, solucao.objetivos);
					if(metodoDistancia.equals("tcheb"))
						distancia = distanciaTchebycheff(solucao_extr_i.objetivos, solucao.objetivos, lambda);
					if(metodoDistancia.equals("sigma"))
						distancia = distanciaEuclidiana(solucao_extr_i.sigmaVector, solucao.sigmaVector);
					if(distancia< solucao.menorDistancia){
						solucao.menorDistancia = distancia;
						solucao.guia = i;
					}
					
				}
				
				
				
				
				
			}
			contador[solucao.guia]++;
		}
		
		/*for (int i = 0; i < contador.length; i++) {
			System.out.print(contador[i] +  " ");
		}
		System.out.println();*/
	}
	
	/**
	 * Método que obtem para cada solucao a distancia em relacao ao ideal
	 * @param solucoes
	 */
	public void calcularDistanciaIdeal(ArrayList<Solucao> solucoes ){
		Solucao ideal = obterSolucoesExtremasIdeais(solucoes, false, problema).get(problema.m).get(0);
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = distanciaEuclidiana(ideal.objetivos, solucao.objetivos);
		}		
	}
	
	
	public void imprimirFronteira(ArrayList<Solucao> solucoes, int j, String id) throws IOException{
		PrintStream ps = new PrintStream("fronteiras/fronteira_" + id + "_" +j+".txt");
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(new Double (solucao.objetivos[i]).toString().replace('.', ',') + "\t");
			}
			ps.println();
		}
		ps.close();
	}
	
	public void imprimirFronteira2(ArrayList<SolucaoNumerica> solucoes, int j, String id) throws IOException{
		PrintStream ps = new PrintStream("fronteiras/fronteira_" + id + "_" +j+".txt");
		for (Iterator<SolucaoNumerica> iterator = solucoes.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(new Double (solucao.objetivos[i]).toString().replace('.', ',') + "\t");
			}
			ps.println();
		}
		ps.close();
	}
	
	public static double log2(double num){
		return Math.log10(num)/Math.log10(2);
	}
	
	
	public static boolean vector_equality(double[] vector1, double[] vector2){
		if(vector1.length!=vector2.length)
			return false;
		else{
			for (int i = 0; i < vector2.length; i++) {
				if(vector1[i] != vector2[i])
					return false;
			}
			return true;
		}
		
	}
	
	/**
	 * Metodo que efetua a poda das solucoes do repositorio de acordo com o metodo definido pelo parametro tipoPoda:
	 * crowd = poda pelo CrowdedOperator = distancia de crowding
	 * AR = poda que calcula o ranking AR e poda pelo valor de AR
	 * BR = poda que calcula o ranking AR e poda pelo valor de BR
	 * pr_id = poda que seleciona as solucoes mais proximas dos extremos e da solucao ideal
	 * ex_id = poda que seleciona as solucoes mais proximas dos extremos e da solucao mais proxima da ideal
	 * eucli = poda que utiliza a menor distancia euclidiana de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * sigma = poda que utiliza a menor distancia euclidiana do vetor sigma de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * tcheb = poda que utiliza a menor distancia de tchebycheff de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * rand = aleatorio
	 * p-ideal = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao ideal
	 *  p-pr_id = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao mais proxiama ideal
	 *  p-ag = Poda que adiciona a soluções num grid adaptativo
	 *  pdom = a solucao nao dominada nao entra no arquivo, somente entram solucoes que dominem alguma outra
	 *  pub = unbound, todas as solucoes entram
	 */
	public void setArchiver(String archiveType){
		if(archiveType == null || archiveType.equals("") || archiveType.equals("null"))
			archiver = null;
		else{
			//Poda somente de  acordo com a distancia de Crowding ou AR+CD ou BR+CD 
			if(archiveType.equals("crowd"))
				archiver = new CrowdingDistanceArchiver(problema.m);
			if(archiveType.equals("ar")){
				AverageRank ar = new AverageRank(problema.m);
				archiver = new RankArchiver(ar);
			}
			if(archiveType.equals("br")){
				BalancedRank ar = new BalancedRank(problema.m);
				archiver = new RankArchiver(ar);
			}
					
			if(archiveType.equals("ideal")){
				archiver = new IdealArchiver(problema);
			}	
			if(archiveType.equals("dist")){
				archiver = new DistributedArchiver(problema, archiveSize);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(archiveType.equals("eucli") || archiveType.equals("sigma") || archiveType.equals("tcheb")){
				archiver = new DistanceReferencePointsArchiver(problema, archiveType);
			}
			
			//Random selection
			if(archiveType.equals("rand"))
				archiver = new RandomArchiver();
			//The solutions are selected through the Adpative Grid scheme
			if(archiveType.equals("ag"))
				archiver = new AdaptiveGridArchiver(problema, archiveSize);
		
			//Unbound, every non-dominated solutions enters
			if(archiveType.equals("ub"))
				archiver = new UnboundArchive();
			//Only enter in the archive solutions that dominate any other
			if(archiveType.equals("dom"))
				archiver = new DominatingArchive();
			if(archiveType.equals("spea2"))
				archiver = new SPEA2Archiver();
			if(archiveType.equals("mga") || archiveType.equals("mga2"))
				archiver = new MGAArchiver(problema, archiveType);
			if(archiveType.equals("eapp"))
				archiver = new EpsAPP();
			if(archiveType.equals("eaps"))
				archiver = new EpsAPS();
			
			if(archiveType.equals("hyp_a"))
				archiver = new HyperPlaneReferenceArchive(problema.m, HyperplaneReferencePoints.ALL);
			if(archiveType.equals("hyp_ed"))
				archiver = new HyperPlaneReferenceArchive(problema.m, HyperplaneReferencePoints.EDGE);
			if(archiveType.equals("hyp_m"))
				archiver = new HyperPlaneReferenceArchive(problema.m, HyperplaneReferencePoints.MIDDLE);
			if(archiveType.equals("hyp_ex"))
				archiver = new HyperPlaneReferenceArchive(problema.m, HyperplaneReferencePoints.EXTREME);
			
			if(archiveType.equals("hyp_r"))
				archiver = new HyperPlaneReferenceArchiveRandom(problema.m);
		}
		
	}
	
	/**
	 * Clusteing method k-Means. Returns only the centroids of each cluster
	 * @param vectors The vectors that will be clustered.
	 * @return Index of the centroids of each cluster
	 */
	public int[] KMeans(double[][] vectors, int k){
		
		int num_vectors = vectors.length;
		int dimension = vectors[0].length;
		double[][] centroids = new double[k][dimension];
		int[] centroids_index = new int[num_vectors];
		//Indicates the group of each vector
		int[] groups = new int[num_vectors];
		
		for(int g = 0; g< num_vectors; g++)
			groups[g] = -1;
		
		//Define initial seeds
		for (int i = 0; i < centroids_index.length; i++) {
			centroids_index[i] = -1;
		}
		
		int num_centroids = 0;
		
		while(num_centroids!=k){
			int index = (int) Math.random() * num_vectors;
			if(centroids_index[index] == -1 ){
				centroids_index[index] = 1;
				centroids[num_centroids] = new double[dimension];
				for(int i = 0; i<dimension; i++)
					centroids[num_centroids][i] = vectors[index][i];
				num_centroids++;
			}
		}
		
		//Controls if there is a change between the groups
		boolean changes = true;
		
		while(changes){
			changes = false;
			for(int i = 0; i<vectors.length; i++){
				double[] vector = vectors[i];
				double smallerDist = Double.MAX_VALUE;
				//Finds the centroid which the vector is closer
				for(int j = 0; j<k; j++){
					double[] centroid_j = centroids[j];
					//Calculates the distance between the vector and the centroid of the group i
					double dist = distanciaEuclidiana(vector, centroid_j);
					if(dist < smallerDist){
						//If the new centroid is different than the previous one, changes the vector from group
						if(groups[i]!=j){
							groups[i] = j; 
							changes = true;
						}
						smallerDist = dist;
					}
				}
			}

			//Re-calculates the centroids
			for(int i = 0; i<k; i++){
				double[] new_centroid_i = new double[dimension];
				int num_vectors_centroid_i = 0;
				for(int j = 0; j<vectors.length; j++){
					double[] vector = vectors[j];
					if(groups[j] == i){
						num_vectors_centroid_i++;
						for(int d = 0; d<dimension; d++){
							new_centroid_i[d] += vector[d]; 
						}
					}
				}

				for(int d = 0; d<dimension; d++){
					new_centroid_i[d] /= num_vectors_centroid_i; 
				}

				centroids[i] = new_centroid_i;
			}
		}
		
		//Define for each group, which solution is close to the final centroid
		int[] center_vectors = new int[k];
		for(int i = 0; i<k; i++){
			double centroid_i[] = centroids[i];
			double smallerDist = Double.MAX_VALUE;
			for(int j = 0; j< vectors.length; j++){
				if(groups[j] == i){
					double[] vector = vectors[j];
					double dist = distanciaEuclidiana(centroid_i, vector);
					if(dist<smallerDist){
						smallerDist = dist;
						center_vectors[i] = j;
					}
				}
			}
		}
		
		return center_vectors;
	}
	
	
	
	
	public void initializeEvalAnalysis(String[] maxmim, String ID){
		
		String caminhoDir = "evaluations/";
		String temp = "temp";
		ArrayList<PontoFronteira> pftrue= Principal.carregarFronteiraPareto(System.getProperty("user.dir"), problema.problema, problema.m);
		
		gd = new GD(problema.m, caminhoDir, temp, pftrue);
		gd.preencherObjetivosMaxMin(maxmim);
		
		igd = new IGD(problema.m, caminhoDir, temp, pftrue);
		igd.preencherObjetivosMaxMin(maxmim);
		
		ld = new LargestDistance(problema.m, caminhoDir, temp, pftrue);
		ld.preencherObjetivosMaxMin(maxmim);
		
		con = new Convergence(problema.m, caminhoDir, temp, pftrue);
		con.preencherObjetivosMaxMin(maxmim);
		try{
			psGD = new PrintStream(caminhoDir + ID + "_gd.txt");
			psIGD = new PrintStream(caminhoDir + ID + "_igd.txt");
			psLD = new PrintStream(caminhoDir + ID + "_ld.txt");
			psCon = new PrintStream(caminhoDir + ID + "_convergence.txt");
		} catch(IOException ex){ex.printStackTrace();}

		
	}
	
	public void evaluationAnalysis(ArrayList<Solucao> solutions){
		
		System.out.print(".");
		ArrayList<PontoFronteira> solutions_pf = new ArrayList<PontoFronteira>();
		
		for (Iterator<Solucao> iterator = solutions.iterator(); iterator.hasNext();) {
			Solucao solution = (Solucao) iterator.next();
			double objectives_temp[] = new double[solution.objetivos.length];
			for(int i = 0; i < solution.objetivos.length; i++)
				objectives_temp[i] = solution.objetivos[i];
			
			PontoFronteira pf = new PontoFronteira(objectives_temp);
			solutions_pf.add(pf);
		}
		
		gd.fronteira = igd.fronteira = ld.fronteira = con.fronteira = solutions_pf;
		
		double gd_value = gd.calcular();
		double igd_value = igd.calcular();
		double ld_value = ld.calcular();
		double con_value = con.calcular();
		
		psGD.println(problema.avaliacoes + "\t" + gd_value);
		psIGD.println(problema.avaliacoes + "\t" + igd_value);
		psLD.println(problema.avaliacoes + "\t" + ld_value);
		psCon.println(problema.avaliacoes + "\t" + con_value);
		
	}
	

	
	public static double[][] getReferencePointsHyperPlane(int m, int p, double[][] extremes){
		int dimension=m; //m
		int divisions=p;  //p
		
		double[] limInf, limSup;
		
		limInf = new double[dimension];
		limSup = new double[dimension];
		
		for(int j = 0; j<dimension;j++){
			limInf[j] = 0;
			limSup[j] = 1;
		}
		
		 
		
		int number_points = (int) combination((dimension+divisions-1), divisions);
		
		double base_points [][] = new double[number_points][dimension];
		
		double intervals[] = new double[dimension];
		
		for(int j = 0; j<dimension; j++)
			intervals[j] = (limSup[j]-limInf[j])/(double) divisions;
		
		

		
		double solucaoAtual[] = new double[dimension];
		double solucaoAnterior[] = new double[dimension];

		double error=1-0.999999; //aceitar uma pequena margem de erro por falhas de arredondamento que impediam a geração de 100 ou mais pontos
		
		for(int j=0; j<dimension;j++){
			solucaoAtual[j] = limInf[j];
			solucaoAnterior[j] = limInf[j];
		}
		

		int col;
		float sumCol;
		
		int num_current_points = 0;
		
		
		while(num_current_points<number_points){ //i - linha atual
			for(int j=0;j<dimension;j++){ //j - coluna atual
				if(j==dimension-1) //se essa coluna e a ultima, incrementa
					solucaoAtual[j]=solucaoAnterior[j]+intervals[j];
				else //se nao, repete
					solucaoAtual[j]=solucaoAnterior[j];
				if(j>0){ //se estourar e não for a primeira coluna
					col=j;
					while(col>0){
						//if(solucaoAtual[col]>=1+margemErro){ //se estourou
						if(solucaoAtual[col]>limSup[j]+error){ //se estourou
							//solucaoAtual[col]=0;
							solucaoAtual[col]=limInf[j];
							solucaoAtual[col-1]=solucaoAnterior[col-1]+intervals[j];
						}
						col--;
					}
				}
			}
			sumCol=0;
			
			for(int j=0;j<dimension;j++){
				sumCol+=solucaoAtual[j];
			}
			if(sumCol>1-error && sumCol<1+error){
				double[] current_reference_point = new double[dimension];
				
				for(int j =0;j<dimension;j++){
					current_reference_point[j] = solucaoAtual[j];
				}
				base_points[num_current_points++] = current_reference_point; 
				
			}

			solucaoAnterior=solucaoAtual.clone();
			
			for(int j=0; j<dimension;j++){
				solucaoAtual[j] = limInf[j];
				
			}
		}
		
		for(int j = 0; j<dimension; j++){
			limInf[j] = Double.MAX_VALUE;
			limSup[j] = 0;
			for(int i = 0; i<extremes.length;i++){
				double[] extremes_i = extremes[i];
				if(extremes_i[j]<limInf[j])
					limInf[j] = extremes_i[j];
				if(extremes_i[j]>limSup[j])			
					limSup[j] = extremes_i[j];
			}
		}
		
		double reference_points[][] = new double[number_points][dimension];
		
		for(int i = 0; i<base_points.length; i++){
			double[] base_point = base_points[i];
			reference_points[i] = new double[dimension];
			for(int j =0; j< dimension; j++){
				double new_value = (limSup[j]-limInf[j])*base_point[j] + limInf[j];
				reference_points[i][j] = new_value;
				
			}
		}
		return reference_points;
	}
	
	public static double combination(int n, int p){
		int fact_n = factorial(n);
		int fact_p = factorial(p);
		int fact_n_p = factorial(n-p);
		return fact_n/(fact_p*fact_n_p);
	}
	
	public static int factorial(int n){
		if(n == 0)
			return 1;
		return n * factorial(n-1);
	}
	
	public static void main(String[] args) {
		int m = 3;
		int p = 3;
		
		double[][] extremes = {{1,0,0}, {1, 0,0}, {1,0,0}};
		
		double[][] reference= AlgoritmoAprendizado.getReferencePointsHyperPlane(m,p,extremes);
		for(int i = 0; i< reference.length; i++){
			double reference_point[] = reference[i]; 
			for(int j = 0; j<reference_point.length; j++)
				System.out.print(reference_point[j] + "\t");
			System.out.println();
		}

		
	}
	
		
}
