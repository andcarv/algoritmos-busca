package kernel.mopso;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import problema.Problema;
import pareto.FronteiraPareto;
import rank.AverageRank;
import rank.BalancedRank;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;
import kernel.mopso.lider.EscolherAleatorio;
import kernel.mopso.lider.EscolherIdeal;
import kernel.mopso.lider.EscolherLider;
import kernel.mopso.lider.EscolherMetodoSigma;
import kernel.mopso.lider.EscolherNWSum;
import kernel.mopso.lider.EscolherOposto;
import kernel.mopso.lider.EscolherTorneioBinario;
import kernel.mopso.lider.EscolherWSum;
import kernel.mopso.lider.EscolherNGN;
import kernel.mopso.lider.EscolherED;





/**
 * Classe que implementa o algoritmo da Otimiza��o por nuvem de part�culas multi-objetivo.
 * @author Andr� B. de Carvalho
 *
 */
public abstract class MOPSO extends AlgoritmoAprendizado{

	//Arraylist que cont�m as particulas da execu��o do algoritmo
	public ArrayList<Particula> populacao = null;
	
	//Arraylist que representa o reposit�rio com as solu��es n�o dominadas
	//public ArrayList<Particula> repositorio = null;
	
	//private String[] maxmim = null;
	
	public double S_MAX = 0.5;
	
	public EscolherLider escolherLider = null;
	
	
	
	//Used to identify the swarm on the TreeMultiSwarm
	public String ID;
	
	
	
	/**
	 * 	
	 * @param n - numero de variaveis
	 * @param prob - problema
	 * @param g - numero de geracoes
	 * @param a - numero de avaliacoes
	 * @param t - tamanho da populacao
	 * @param s - valor do s do metodo do sato
	 * @param maxmim - string q indica se cada objetivo eh de maximizacao ou minimizacao
	 * @param tRank - 
	 * @param smax
	 * @param el
	 * @param eps
	 * @param tRepositorio
	 * @param tPoda
	 */
	public MOPSO(int n, Problema prob, int g, int a, int t, String s, String[] maxmim, String tRank, double smax, String el, double eps, int tRepositorio, String tPoda, boolean eval_analysis){
		super(n,prob,g, a,t, maxmim,tRank, eps, tRepositorio, tPoda, eval_analysis);
		populacao = new ArrayList<Particula>();
		//repositorio = new ArrayList<Particula>();
		pareto = new FronteiraPareto(new Double(s).doubleValue(), maxmim,rank, eps, problema, archiveSize);
		S_MAX = smax;
		if(rank)
			metodoRank.setPareto(pareto);
		
		problema = prob;
		
		setMetodoEscolhaLider(el);
		
		if(archiver.ID.equals("ar"))
			metodoRank = new AverageRank(problema.m);
		if(archiver.ID.equals("br"))
			metodoRank = new BalancedRank(problema.m);
		
		
		if(archiver.ID.equals("eaps") || archiver.ID.equals("eapp")){
			if(eps > 1 || eps <= 0){
			System.err.println("Tipo de arquivo escolhido: " + archiver.ID);
			System.err.println("Deve ser escolhido um valor de Epsilon entre 0 e 1");
			System.exit(0);
			}
		}
		
		
		
		
			
		
	}
	
	/**
	 * MOPSO with shared pareto front - used in multiswarm
	 * @param n
	 * @param prob
	 * @param g
	 * @param a
	 * @param t
	 * @param s
	 * @param maxmim
	 * @param tRank
	 * @param smax
	 * @param el
	 * @param eps
	 * @param tRepositorio
	 * @param tPoda
	 * @param pareto
	 */
	public MOPSO(int n, Problema prob, int g, int a, int t, String s, String[] maxmim, String tRank, double smax, String el, double eps, int tRepositorio, String tPoda, FronteiraPareto pareto, boolean eval_analysis){
		super(n,prob,g, a,t, maxmim,tRank, eps, tRepositorio, tPoda, eval_analysis);
		populacao = new ArrayList<Particula>();
		//repositorio = new ArrayList<Particula>();
		this.pareto = pareto;
		S_MAX = smax;
		if(rank)
			metodoRank.setPareto(pareto);
		
		problema = prob;
		
		setMetodoEscolhaLider(el);
		
		if(archiver.ID.equals("ar"))
			metodoRank = new AverageRank(problema.m);
		if(archiver.ID.equals("br"))
			metodoRank = new BalancedRank(problema.m);
		
		
		if(archiver.ID.equals("eaps") || archiver.ID.equals("eapp")){
			if(eps > 1 || eps <= 0){
			System.err.println("Tipo de arquivo escolhido: " + archiver.ID);
			System.err.println("Deve ser escolhido um valor de Epsilon entre 0 e 1");
			System.exit(0);
			}
		}
			
		
	}
	
	/**
	 * M�todo que limpa as listas do algoritmo e o prepara para uma nova execu��o.
	 *
	 */
	public void reiniciarExecucao(){
		populacao = new ArrayList<Particula>();
		pareto = new FronteiraPareto(pareto.S, pareto.maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
		problema.avaliacoes =0; 
	}
	
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public abstract ArrayList<Solucao> executar();
	
	/*public void escolherLideres(){
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
	}*/
	
	
	/**
	 * Initializes the population randomly
	 */
	public void inicializarPopulacao(){
		
		for(int i = 0; i<populationSize; i++){
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
			int cont = 0;
			do{
				SolucaoNumerica s = new SolucaoNumerica(n, problema.m);
				
				for (int j = 0; j < used_objectives.length; j++) {
					s.used_objectives[j] = used_objectives[j];
				}
				
				s.iniciarSolucaoAleatoria();
				s.truncar();
				particula.iniciarParticulaAleatoriamente(problema, s);
				problema.calcularObjetivos(s);
				cont++;
			}while(populacao.contains(particula) && (cont<20));
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);	
		}
		if(rank)
			rankParticula(populacao);
	}
	
	/**
	 * Initializes the population randomly
	 */
	public void inicializarPopulacao(double[][] limit_search_space){
		
		for(int i = populacao.size(); i<populationSize; i++){
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
			int cont = 0;
			do{
				SolucaoNumerica s = new SolucaoNumerica(n, problema.m, limit_search_space);
				s.iniciarSolucaoAleatoria();
				s.truncar();
				particula.iniciarParticulaAleatoriamente(problema, s);
				problema.calcularObjetivos(s);
				cont++;
			}while(populacao.contains(particula) && (cont<20));
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);	
		}
		if(rank)
			rankParticula(populacao);
	}
	
	/**
	 * Starts a new swarm with a set of initial solutions 
	 * @param solutions initial solutions
	 * @param limit_search_space Limits of the search space
	 */
	public void initializePopulation(ArrayList<Solucao> solutions, double[][] limit_search_space){
		
		for (Iterator<Solucao> iterator = solutions.iterator(); iterator.hasNext();) {
			Solucao solution = (Solucao) iterator.next();
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
			
			SolucaoNumerica s = (SolucaoNumerica) solution;
			s.truncar();
			particula.iniciarParticulaAleatoriamente(problema, s);
			problema.calcularObjetivos(s);

			
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);	
		}
		if(rank)
			rankParticula(populacao);
	}
	
	/**
	 * M�todo que preenche o respositorio com as solu��o n�o dominadas
	 *
	 */
	public void atualizarRepositorio(){
		
						
		/*try{
			imprimirParticulas(populacao);
		} catch (IOException ex) {ex.printStackTrace();}*/
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula =  iter.next();
			if(!pareto.getFronteira().contains(particula.solucao)){
				if(!rank){
					particula.solucao.numDominacao = pareto.add((Solucao)particula.solucao.clone(), archiver);	
				}
				else
					pareto.addRank((Solucao)particula.solucao.clone());
			}
		}
		if(pareto.S < 0.1){
			//System.out.print(pareto.getFronteira().size() + " - ");
			pareto.scdas(pareto.S);
			//System.out.println(pareto.getFronteira().size());
		}
		
		
		
		
		/*try{
			imprimirFronteira(pareto.getFronteira(), 0, "temp");
		} catch(IOException ex){ex.printStackTrace();}*/
	}
	
	/**
	 * Metodo que efetua a poda das solucoes do repositorio de acordo com o metodo definido pelo parametro tipoPoda:
	 * crowd = poda pelo CrowdedOperator = distancia de crowding
	 * AR = poda que calcula o ranking AR e poda pelo valor de AR
	 * BR = poda que calcula o ranking AR e poda pelo valor de BR
	 * ex_id2 = poda que seleciona as solucoes mais proximas dos extremos e da solucao ideal
	 * ex_id = poda que seleciona as solucoes mais proximas dos extremos e da solucao mais proxima da ideal
	 * eucli = poda que utiliza a menor distancia euclidiana de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * sigma = poda que utiliza a menor distancia euclidiana do vetor sigma de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * tcheb = poda que utiliza a menor distancia de tchebycheff de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * rand = aleatorio
	 * p-ideal = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao ideal
	 *  p-pr_id = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao mais proxiama ideal
	 *  p-ag = Poda que adiciona a soluções num grid adaptativo
	 */
	/*public void filter(){
		if(rank)
			pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
		else{
			//Poda somente de  acordo com a distancia de Crowding 
			if(filter.equals("pcrowd"))
				pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
			//Calcula o ranking AR e poda de acordo com o AR, caso haja empate usa a distancia de crowding
			if(filter.equals("par")){				
				rankear(pareto.getFronteira());
				pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
			}
			//Calcula o ranking BR e poda de acordo com o BR, caso haja empate usa a distancia de crowding
			if(filter.equals("pbr")){
				rankear(pareto.getFronteira());
				pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
			}
			
			if(filter.equals("pideal")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), false).get(problema.m).get(0);
				pareto.podarLideresIdeal(tamanhoRepositorio, ideal);
			}
			
			if(filter.equals("ppr_id")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), true).get(problema.m).get(0);
				pareto.podarLideresExtremosIdeal(tamanhoRepositorio, problema.m, ideal);
			}

			if(filter.equals("pex_id2")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), false).get(problema.m).get(0);
				pareto.podarLideresExtremosIdeal(tamanhoRepositorio, problema.m, ideal);
			}

			if(filter.equals("pex_id")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), true).get(problema.m).get(0);
				pareto.podarLideresExtremosIdeal(tamanhoRepositorio, problema.m, ideal);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("peucli")){
				ArrayList<ArrayList<Solucao>> extremos = obterSolucoesExtremasIdeais(pareto.getFronteira(), true);
				definirDistanciasSolucoesProximasIdeais(extremos, pareto.getFronteira(), "euclidiana");
				pareto.podarLideresDistancia(tamanhoRepositorio);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("psigma")){
				ArrayList<ArrayList<Solucao>> extremos = obterSolucoesExtremasIdeais(pareto.getFronteira(), true);
				definirDistanciasSolucoesProximasIdeais(extremos, pareto.getFronteira(), "sigma");
				pareto.podarLideresDistancia(tamanhoRepositorio);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("ptcheb")){
				ArrayList<ArrayList<Solucao>> extremos = obterSolucoesExtremasIdeais(pareto.getFronteira(), true);
				definirDistanciasSolucoesProximasIdeais(extremos, pareto.getFronteira(), "tcheb");
				pareto.podarLideresDistancia(tamanhoRepositorio);
			}
			if(filter.equals("prand"))
				pareto.podarLideresAleatorio(tamanhoRepositorio);
			if(filter.equals("pag"))
				pareto.podarAdaptiveGrid(tamanhoRepositorio, problema.m, partesGrid);
		}
			
		
	}*/
	
	/**
	 * Metodo que define o parametro S do metodo CDAS automaticamente para cada solucao.
	 * Solcuoes mais no extremo sao definidas com o valor S_MAX para evitar que elas sejam dominadas
	 * O valor de S vai diminuindo quando chega as solucoes mais ao centro
	 * @param particulas
	 */
	public void definirSExtremos(ArrayList<Particula> particulas){
		double max = pareto.S;
		double min = S_MAX;
		
		double maiorDiff = 0;
		double menorDiff = Double.MAX_VALUE;
		
		//Obtem quais solucoes estao mais nos extremos
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Solucao solucao = ((Particula) iterator.next()).solucao; 
			solucao.setDiferenca();
			if(solucao.diff > maiorDiff){
				//System.out.println("Maior: " + solucao);
				maiorDiff = solucao.diff;
			}
			if(solucao.diff < menorDiff){
				//System.out.println("Menor: " + solucao);
				menorDiff = solucao.diff;
			}
			
		}
		
		double denominador = maiorDiff - menorDiff;
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			
			//Define o S para cada solucao, de acordo com sua proximidade dos extremos
			Solucao solucao = ((Particula) iterator.next()).solucao; 
			double diff = solucao.diff;

			double parte1 = (max-min)*(maiorDiff-diff);
			double parte2 = 0;
			if(denominador != 0)
				parte2 = parte1/denominador;
			double S = -1.0*parte2 + max;
			solucao.S = S;
			//System.out.println(solucao.diff + " - " +  S);
			
		}
	//	System.out.println();
	}
	
	/**
	 * Metodo que defineo valor de S automaticamente, com S maior para solcoes em areas povadas e menor para solcuoes em areas menos povadas 
	 * @param particulas
	 */
	public void definirS(ArrayList<Particula> particulas){
		double maiorCD = 0;
		double max = pareto.S;
		double min = S_MAX;
		
		/*for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula =  iterator.next();
			double cd = particula.solucao.ocupacao;
			if(cd!=Double.MAX_VALUE){
				maiorCD = particula.solucao.ocupacao; 
				break;
			}
		}*/
		
		maiorCD = particulas.get(particulas.size()-1).solucao.ocupacao;
		double menorCD = particulas.get(0).solucao.ocupacao;
		double denominador = maiorCD - menorCD;
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula =  iterator.next();
			double cd = particula.solucao.ocupacao;
			if(cd == Double.MAX_VALUE)
				particula.solucao.S = max;
			else{
				double parte1 = (max-min)*(maiorCD-cd);
				double parte2 = 0;
				if(denominador != 0)
					parte2 = parte1/denominador;
				double S = -1.0*parte2 + max;
				particula.solucao.S = S;
				//System.out.println(particula.solucao.ocupacao + " - " +  S);
			}
		}
		//System.out.println();
	}
	
	/**
	 * Calcula a distancia de Crowding para um conjunto de particulas
	 * @param particulas
	 */
	public void calcularCrowdingDistanceParticula(ArrayList<Particula> particulas){
		
		ArrayList<Solucao> solucoes = new ArrayList<Solucao>();
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula =  iterator.next();
			particula.solucao.crowdDistance = 0;
			solucoes.add(particula.solucao);
		}
		
		for(int m = 0; m<problema.m; m++){
			ComparetorObjetivo comp = new ComparetorObjetivo(m);
			Collections.sort(solucoes, comp);
			Solucao sol1 = solucoes.get(0);
			Solucao solN = solucoes.get(solucoes.size()-1);
			sol1.crowdDistance = Double.MAX_VALUE;
			solN.crowdDistance = Double.MAX_VALUE;
			for(int i = 1; i<solucoes.size()-1; i++){
				Solucao sol = solucoes.get(i);
				Solucao solProx = solucoes.get(i+1);
				Solucao solAnt = solucoes.get(i-1);
				sol.crowdDistance += solProx.objetivos[m] - solAnt.objetivos[m];
			}
		}
		
	}
	
	/**
	 * Para cada particula, conta quantas as particulas estao dentro do raio de valor limite_ocupacao
	 * @param particulas
	 */
	public void contarParticulasLimitesRaio(ArrayList<Particula> particulas){

		for(int k = 0; k<particulas.size(); k++){
			Solucao solucao = ((Particula)particulas.get(k)).solucao;
			solucao.ocupacao = 0;

			for(int i = 0; i<particulas.size(); i++){
				Solucao solucao_i = ((Particula)particulas.get(i)).solucao;
				//Se não é a solução corrente
				if(i != k){
					
					double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
					if(dist<limite_ocupacao)
						solucao.ocupacao++;
				}			
			}
		}

	}
	
	public void setMetodoEscolhaLider(String escolha){
		escolherLider = new EscolherTorneioBinario();
		if(escolha.equals("tb"))
			escolherLider = new EscolherTorneioBinario();
		if(escolha.equals("sigma"))
			escolherLider = new EscolherMetodoSigma();
		if(escolha.equals("ideal"))
			escolherLider = new EscolherIdeal(problema.m);
		if(escolha.equals("op"))
			escolherLider = new EscolherOposto();
		if(escolha.equals("rdm"))
			escolherLider = new EscolherAleatorio();
		if(escolha.equals("WSum"))
			escolherLider = new EscolherWSum();
		if(escolha.equals("NWSum"))
			escolherLider = new EscolherNWSum();
		if(escolha.equals("NGN"))
			escolherLider = new EscolherNGN();
		if(escolha.equals("ED"))
			escolherLider = new EscolherED();
		
		
			
	}
	
	public void imprimirParticulas(ArrayList<Particula> particulas) throws IOException{
		PrintStream ps = new PrintStream("fronteiras/particulas.txt");
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(particula.solucao.objetivos[i] + "\t");
			}
			ps.println();
		}
		ps.close();
		
	}
	
	public void escolherParticulasMutacao(){
		SMPSO smpso = (SMPSO) this;
		smpso.escolherParticulasMutacao();
	}
	
	public void evolutionaryLoop(){
		SMPSO smpso = (SMPSO) this;
		smpso.lacoEvolutivo();
	}
	
	public void setUsedObjectives(boolean[] uo){
		for (int i = 0; i < uo.length; i++) {
			used_objectives[i] = uo[i];
		}
	}
	
	public String toString(){
		return ID;
	}
	
	
}
