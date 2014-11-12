package kernel.mopso.multi;

import indicadores.Dominance;
import indicadores.GD;
import indicadores.IGD;
import indicadores.PontoFronteira;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import archive.UnboundArchive;
import kernel.AlgoritmoAprendizado;
import kernel.mopso.MOPSO;
import kernel.mopso.Particula;
import kernel.mopso.SMPSO;
import pareto.FronteiraPareto;
import principal.Principal;
import problema.Problema;
import seeds.InitializeCentroidReset;
import seeds.InitializeExtremesReset;
import seeds.InitializeHyperplanePreserve;
import seeds.InitializeHyperplaneReset;
import seeds.InitializeRandomDistribute;
import seeds.InitializeRandomPreserve;
import seeds.InitializeRandomReset;
import seeds.InitializeSwarms;
import solucao.ComparetorDistancia;
import solucao.Solucao;
import solucao.SolucaoNumerica;



public class IteratedMultiSwarm extends AlgoritmoAprendizado {
	
	
	public MOPSO basis_mopso;
	
	public MOPSO[] swarms;

	
	//Defines the range of the region that each swarm must search
	public double box_range = 0.1;
	
	private double update_box_range_tax;
	
	private  double box_range_min = 0.1;
	
	private  double box_range_max = 0.1;
	
	public int iterations = 10;
	
	public GD gd_reset;
	public IGD igd_reset;
	
	public boolean reset = false;
	
	public String initialize = "ctdr";
	
	public InitializeSwarms initializeMethod = null;
	
	/**
	 * 
	 * @param n Decison variables
	 * @param prob Problem
	 * @param generations Number of iterations
	 * @param evaluations Number of evaluations (if different of -1, runs the evaluations instead the iterations)
	 * @param popSize Population size
	 * @param S Parameter of the CDAS method, default S = 0.5
	 * @param maxmim Define if the objective are maximization or minimization
	 * @param repositorySize Size of the external archive
	 * @param tRank Ranking method (default false)
	 * @param archiving Archiving method
	 * @param leaderChoice Leader's choice
	 * @param eps Epsilon values for eapp and eaps
	 * @param numberSwarms Number of swarms
	 */
	public IteratedMultiSwarm(int n, Problema prob, int generations, int evaluations, int popSize, String S, String[] maxmim, int repositorySize, String tRank, String archiving, String leaderChoice, double eps, int numberSwarms, double range_mim, double range_max, int pop_swarm, int rep_swarm, int split_iterations, boolean eval_analysis, boolean reset, String init){
		super(n,prob,generations,evaluations, popSize, maxmim,tRank, eps, repositorySize, archiving, eval_analysis);
				
		box_range = box_range_min = range_mim;
		
		box_range_max = range_max;
		
		iterations = split_iterations;
		
		update_box_range_tax = (box_range_max - box_range)/(iterations-1);
		
		this.reset = reset;
		
		initialize = init;
		
			
		//If the parameters are different from the number of swarms, the remaining swarms are defined with the parameters of the SMPSO algorithm
		String[] S_i = S.split(";");
		if(S_i.length < numberSwarms+1){
			String[] temp = S_i.clone();
			String default_ = "0.5";
			S_i = new String[numberSwarms+1];
			for (int i = 0; i < S_i.length; i++) {
				if(i<temp.length)
					S_i[i] = temp[i];
				else
					S_i[i] = default_;
			}
		}
		
		pareto = new FronteiraPareto(new Double(S_i[0]).doubleValue(), maxmim,rank, eps, problema, archiveSize);
		String[] leaderChoice_i = leaderChoice.split(";");
		if(leaderChoice_i.length < numberSwarms+1){
			String[] temp = leaderChoice_i.clone();
			String default_ = leaderChoice_i[leaderChoice_i.length-1];
			leaderChoice_i = new String[numberSwarms+1];
			for (int i = 0; i < leaderChoice_i.length; i++) {
				if(i<temp.length)
					leaderChoice_i[i] = temp[i];
				else
					leaderChoice_i[i] = default_;
			}
		}
		String[] archiving_i = archiving.split(";");
		if(archiving_i.length < numberSwarms+1){
			String[] temp = archiving_i.clone();
			String default_ = archiving_i[archiving_i.length-1];
			archiving_i = new String[numberSwarms+1];
			for (int i = 0; i <archiving_i.length; i++) {
				if(i<temp.length)
					archiving_i[i] = temp[i];
				else
					archiving_i[i] = default_;
			}
		}
		
		
		
		basis_mopso = new SMPSO(n, prob, generations, evaluations, popSize, S_i[0], maxmim, repositorySize, tRank, new Double(S_i[0]).doubleValue(), archiving_i[0], leaderChoice_i[0], eps, eval_analysis);
		System.out.println("Basis: " + archiving_i[0] + "\t|\t" + leaderChoice_i[0] + "\t|\t" + S_i[0]);
		swarms = new MOPSO[numberSwarms];
		
		for (int i = 0; i < swarms.length; i++) {
			String s = S_i[i+1];
			String archiver = archiving_i[i+1];
			String lc = leaderChoice_i[i+1];
			System.out.println("Swarm " + i + ": " + archiver + "\t|\t" +lc + "\t|\t" + s);

			swarms[i] = new SMPSO(n, prob, generations, evaluations, pop_swarm, s, maxmim, rep_swarm, tRank, new Double(s).doubleValue(), archiver, lc, eps, eval_analysis);

		}
		
		setInitializeMethod();
		
		String caminhoDir = "evaluations/";
		String temp = "temp";
		ArrayList<PontoFronteira> pftrue= Principal.carregarFronteiraPareto(System.getProperty("user.dir"), problema.problema, problema.m);
		
		gd_reset = new GD(problema.m, caminhoDir, temp, pftrue);
		gd_reset.preencherObjetivosMaxMin(maxmim);
		
		igd_reset = new IGD(problema.m, caminhoDir, temp, pftrue);
		igd_reset.preencherObjetivosMaxMin(maxmim);
		
	}
	
	public void  setInitializeMethod(){
		switch (initialize) {
		case "ctdr":{
			initializeMethod = new InitializeCentroidReset(swarms, problema);
			break;
		}
		case "extr":{
			initializeMethod = new InitializeExtremesReset(swarms, problema);
			break;
		}
		case "rndr":{
			initializeMethod = new InitializeRandomReset(swarms, problema);
			break;
		}
		case "rndp":{
			initializeMethod = new InitializeRandomPreserve(swarms, problema);
			break;
		}
		case "rndd":{
			initializeMethod = new InitializeRandomDistribute(swarms, problema);
			break;
		}
		case "hypr":{
			initializeMethod = new InitializeHyperplaneReset(swarms, problema);
			break;
		}
		case "hypp":{
			initializeMethod = new InitializeHyperplanePreserve(swarms, problema);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public ArrayList<Solucao> executar() {

		try{
			PrintStream comunication = new PrintStream("evaluations/comunication.txt");

			resetExecution();

			ArrayList<Solucao> initial_front = basis_mopso.executar();
			if(eval_analysis)
			   evaluationAnalysis(initial_front);
			System.out.println();
			int com = 0;
			for(int k = 0; k<iterations; k++){
				System.out.println("Iteration: " + k);
				comunication.println(com++ + "\t" + problema.avaliacoes);
				System.out.println(box_range);
				
				PrintStream psTemp = new PrintStream("solucoes.txt");
				for (Iterator<Solucao> iterator = initial_front.iterator(); iterator
						.hasNext();) {
					SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
					for(int i = 0; i<problema.n; i++)
						psTemp.print(new Double(solucao.getVariavel(i)).toString().replace('.', ',') + "\t");
					psTemp.println();
				}
				
				psTemp.close();
				initializeMethod.initializeSwarms(initial_front, box_range);
				for(int i = 0; i<geracoes; i++){
					if(i%10 == 0)
						System.out.print(i + " ");

					for (int s = 0; s < swarms.length; s++) {
						if(i%10 == 0)
							System.out.print( "s" + s + " ");
						swarms[s].evolutionaryLoop();
					}
					if(i % 10 ==0)
						System.out.println();

					if(eval_analysis){

						for (int s_i = 0; s_i < swarms.length; s_i++) {
							ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
							for (Iterator<Solucao> iterator = swarm_pareto_i
									.iterator(); iterator.hasNext();) {
								Solucao solucao = (Solucao) iterator.next();
								pareto.add(solucao, new UnboundArchive());
							}
						}

						evaluationAnalysis(pareto.getFronteira());
						pareto.getFronteira().clear();
					}
				}
				
				/*double[][] indicators_swarm = new double[swarms.length][2];
				
				for (int s_i = 0; s_i < swarms.length; s_i++) {
					ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
					calculate_indicators_swarms_reset(swarm_pareto_i,indicators_swarm,s_i);
				}
				
				reset_dominated_swarms(indicators_swarm);*/
				
				pareto.getFronteira().clear();
				
				for (int s_i = 0; s_i < swarms.length; s_i++) {
					ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
					PrintStream psSwarm = new PrintStream("evaluations/swarm_" + s_i + ".txt");
					for (Iterator<Solucao> iterator = swarm_pareto_i
							.iterator(); iterator.hasNext();) {
						SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
						if(!pareto.getFronteira().contains(solucao))
							pareto.add((Solucao)solucao.clone(), new UnboundArchive());
						for(int o = 0; o< solucao.objetivos.length; o++)
							psSwarm.print(solucao.objetivos[o] + "\t");
						psSwarm.println();
					}
					psSwarm.close();
				}
				
				
				
				initial_front.clear();
				initial_front.addAll(pareto.getFronteira());
				
				if(reset)
					reset_similar_swarms(similarity());
				
				updateBoxRange();
			}
			if(eval_analysis)
				evaluationAnalysis(pareto.getFronteira());
			comunication.close();
			return pareto.getFronteira();
		}catch(IOException ex) {ex.printStackTrace(); return null;}
	}

	@Override
	public ArrayList<Solucao> executarAvaliacoes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void resetExecution(){
		pareto = new FronteiraPareto(pareto.S, pareto.maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
		basis_mopso.pareto = new FronteiraPareto(basis_mopso.pareto.S, basis_mopso.pareto.maxmim, basis_mopso.pareto.rank, basis_mopso.pareto.eps, problema, basis_mopso.pareto.archiveSize);
		
		
		for(int i = 0; i<swarms.length; i++){
			swarms[i].populacao = new ArrayList<Particula>();
			
			swarms[i].pareto = new FronteiraPareto(swarms[i].pareto.S, swarms[i].pareto.maxmim, swarms[i].pareto.rank, swarms[i].pareto.eps, problema, swarms[i].pareto.archiveSize);
			
		}
		
		problema.avaliacoes =0; 
		
		box_range = box_range_min;
	}
	
	public void updateBoxRange(){
		box_range= box_range + update_box_range_tax;
	}
	
	public void calculate_indicators_swarms_reset(ArrayList<Solucao> solutions, double[][] indicators_swarm, int s_i){
		ArrayList<PontoFronteira> solutions_pf = new ArrayList<PontoFronteira>();

		for (Iterator<Solucao> iterator = solutions.iterator(); iterator.hasNext();) {
			Solucao solution = (Solucao) iterator.next();
			double objectives_temp[] = new double[solution.objetivos.length];
			for(int i = 0; i < solution.objetivos.length; i++)
				objectives_temp[i] = solution.objetivos[i];

			PontoFronteira pf = new PontoFronteira(objectives_temp);
			solutions_pf.add(pf);
		}
		
		
		 gd_reset.fronteira =  igd_reset.fronteira = solutions_pf;
		 
		 indicators_swarm[s_i][0] = gd_reset.calcular();
		 indicators_swarm[s_i][1] = igd_reset.calcular();
	}
	
	public void reset_dominated_swarms(double[][] indicators_swarm){
		for(int i = 0; i< indicators_swarm.length; i++){
			double[] swarm_i = indicators_swarm[i];
			int[] cont = new int[2];
			for(int j = 0; j< indicators_swarm.length; j++){
				if(i!=j){
					double[] swarm_j = indicators_swarm[j];
					if(swarm_j[0]<swarm_i[0])
						cont[0]++;
					if(swarm_j[1]<swarm_i[1])
						cont[1]++;
				}
			}
			if(cont[0]>=(swarms.length/2) && cont[1]>=(swarms.length/2)){
				System.out.println(i);
				swarms[i].pareto.getFronteira().clear();
				for(int j = 0; j<swarms[i].archiveSize; j++){
					int random_index = (int) (Math.random() * pareto.getFronteira().size());
					SolucaoNumerica solution = (SolucaoNumerica)pareto.getFronteira().get(random_index);
					if(!swarms[i].pareto.getFronteira().contains(solution))
						swarms[i].pareto.add((Solucao)solution.clone(), swarms[i].archiver);
				}
			}
		}
	}
	
	public void reset_similar_swarms(double[][] similarity){
		boolean[] flag_reseted = new boolean[swarms.length];
		
		for(int i = 0; i<similarity.length-1; i++){
			ArrayList<Integer> similars = new ArrayList<Integer>();
			similars.add(i);
			for(int j = i+1; j<similarity.length; j++){
				if(!flag_reseted[j]){
					if(similarity[i][j] < 0.35){
						similars.add(j);
						flag_reseted[j] = true;
					}
				}
			}
			
			if(similars.size()>1){
				reset_random(similars);
				//reset_opposite(similars);
			}
			
		}
		
	}
	
	public void reset_random(ArrayList<Integer> similars){
		
		Dominance dominance = new Dominance(problema.m);
		dominance.objetivosMaxMin = pareto.objetivosMaxMin;
		
		double best_dominance = 0;
		double index_dominance = -1;
		
		for(int i = 0; i< similars.size()-1;i++){
			int index_i = similars.get(i);
			for(int j = i+1; j<similars.size();j++){
				int index_j = similars.get(j);
				double[] dom = dominance.calcular2(swarms[index_i].pareto.getFronteira(), swarms[index_j].pareto.getFronteira());
				for(int d = 0; d<dom.length; d++){
					if(dom[d]>best_dominance){
						best_dominance = dom[d];
						if(d == 0)
							index_dominance = index_i;
						else
							index_dominance = index_j;
					}
				}
			}
		}
		
		for(int i = 0; i<similars.size(); i++){
			if(similars.get(i).intValue()!=index_dominance){
				int index = similars.get(i);
				swarms[index].pareto.getFronteira().clear();
				for(int j = 0; j<swarms[index].archiveSize; j++){
					int random_index = (int) (Math.random() * pareto.getFronteira().size());
					SolucaoNumerica solution = (SolucaoNumerica)pareto.getFronteira().get(random_index);
					if(!swarms[index].pareto.getFronteira().contains(solution))
						swarms[index].pareto.add((Solucao)solution.clone(), swarms[index].archiver);
				}
			}
		}
		
	}
	
	public void reset_opposite(ArrayList<Integer> similars){
		
		Dominance dominance = new Dominance(problema.m);
		dominance.objetivosMaxMin = pareto.objetivosMaxMin;
		
		double best_dominance = 0;
		double index_dominance = -1;
		
		for(int i = 0; i< similars.size()-1;i++){
			int index_i = similars.get(i);
			for(int j = i+1; j<similars.size();j++){
				int index_j = similars.get(j);
				double[] dom = dominance.calcular2(swarms[index_i].pareto.getFronteira(), swarms[index_j].pareto.getFronteira());
				for(int d = 0; d<dom.length; d++){
					if(dom[d]>best_dominance){
						best_dominance = dom[d];
						if(d == 0)
							index_dominance = index_i;
						else
							index_dominance = index_j;
					}
				}
			}
		}
		
		
		for(int i = 0; i<similars.size(); i++){
			if(similars.get(i).intValue()!=index_dominance){
				
				int index = similars.get(i);
				System.out.println(index);
				swarms[index].pareto.getFronteira().clear();
				for(int j = 0; j< pareto.getFronteira().size(); j++){
					Solucao solution = pareto.getFronteira().get(j);
					solution.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(solution.objetivos, swarms[index].pareto.average_point);
				}
				
				ComparetorDistancia comp = new ComparetorDistancia();
				Collections.sort(pareto.getFronteira(), comp);
				
				for(int j = 0; j<swarms[index].archiveSize; j++){
					SolucaoNumerica solution = (SolucaoNumerica)pareto.getFronteira().get(i);
					if(!swarms[index].pareto.getFronteira().contains(solution))
						swarms[index].pareto.add((Solucao)solution.clone(), swarms[index].archiver);
				}
			}
		}
		
	}
	
	
	
	public double[][] similarity(){
		
		double[][] average_solution = new double[swarms.length][problema.m];
				
		for (int s_i = 0; s_i < swarms.length; s_i++) {
			ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
			double[] average_solution_i = new double[problema.m];
			for (Iterator<Solucao> iterator = swarm_pareto_i
					.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				for(int i = 0; i<problema.m; i++){
					average_solution_i[i] += solucao.objetivos[i];
				}
			}
			for (int i = 0; i < average_solution_i.length; i++) {
				average_solution_i[i] = average_solution_i[i]/swarm_pareto_i.size(); 
				//System.out.print(average_solution_i[i] + "\t");
			}
			//System.out.println();
			average_solution[s_i] = average_solution_i;
			swarms[s_i].pareto.average_point = average_solution_i;
		}
		
		double[][] similarity = new double[swarms.length][swarms.length];
		
		for(int i = 0; i<swarms.length-1; i++){
			for(int j = i+1; j<swarms.length; j++){
				double difference = 0;
				for(int o = 0; o<average_solution[i].length; o++)
					difference += Math.abs(average_solution[i][o] - average_solution[j][o]);
				similarity[i][j] = difference;
				similarity[j][i] = difference;
			}
		}
		
		System.out.println();
		
		return similarity;
		
	}

}
