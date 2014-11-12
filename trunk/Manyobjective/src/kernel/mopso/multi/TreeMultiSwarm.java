package kernel.mopso.multi;


import java.util.ArrayList;
import java.util.Iterator;

import archive.UnboundArchive;

import kernel.AlgoritmoAprendizado;


import kernel.mopso.MOPSO;
import kernel.mopso.Particula;
import kernel.mopso.SMPSO;
import pareto.FronteiraPareto;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class TreeMultiSwarm extends AlgoritmoAprendizado {
	
	public MOPSO[] swarms;
	
	
	//Indicates in which iteration the global archiver will be updated by the archivers from the swarm
	public int update = 1;
	
	public static int index_swarm = 0;
	
	public int[] tree_structure;
	
	public String[] S_i, leaderChoice_i, archiving_i, maxmim;
	
	
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
	public TreeMultiSwarm(int n, Problema prob, int generations, int evaluations, int popSize, String S, String[] maxmim, int repositorySize, String tRank, String archiving, String leaderChoice, double eps, int numberSwarms, int update, boolean eval_analysis){
		super(n,prob,generations,evaluations, popSize, maxmim,tRank, eps, repositorySize, archiving, eval_analysis);
		
		
		numberSwarms = (2*problema.m - 1);
		
		swarms = new MOPSO[numberSwarms];
		
		tree_structure = new int[numberSwarms];
		
		this.update = update;
		
		this.maxmim = maxmim;
		
		//If the parameters are different from the number of swarms, the remaining swarms are defined with the parameters of the SMPSO algorithm
		S_i = S.split(";");
		if(S_i.length != numberSwarms){
			String[] temp = S_i.clone();
			String default_ = "0.5";
			S_i = new String[numberSwarms];
			for (int i = 0; i < S_i.length; i++) {
				if(i<temp.length)
					S_i[i] = temp[i];
				else
					S_i[i] = default_;
			}
		}
		
		pareto = new FronteiraPareto(new Double(S_i[0]).doubleValue(), maxmim,rank, eps, problema, archiveSize);
		leaderChoice_i = leaderChoice.split(";");
		if(leaderChoice_i.length != numberSwarms){
			String[] temp = leaderChoice_i.clone();
			String default_ = "tb";
			leaderChoice_i = new String[numberSwarms];
			for (int i = 0; i < leaderChoice_i.length; i++) {
				if(i<temp.length)
					leaderChoice_i[i] = temp[i];
				else
					leaderChoice_i[i] = default_;
			}
		}
		archiving_i = archiving.split(";");
		if(archiving_i.length != numberSwarms){
			String[] temp = archiving_i.clone();
			String default_ = "ideal";
			archiving_i = new String[numberSwarms];
			for (int i = 0; i <archiving_i.length; i++) {
				if(i<temp.length)
					archiving_i[i] = temp[i];
				else
					archiving_i[i] = default_;
			}
		}
		
		swarms = new MOPSO[numberSwarms];
		
		/*for (int i = 0; i < swarms.length; i++) {
			String s = S_i[i];
			System.out.println("Swarm " + i + ": " + archiving_i[i] + "\t|\t" + leaderChoice_i[i] + "\t|\t" + s);
			
			swarms[i] = new SMPSO(n, prob, generations, evaluations, popSize, s, maxmim, repositorySize, tRank, new Double(s).doubleValue(), archiving_i[i], leaderChoice_i[i], eps, false);

		}*/
		
		
		defineSwarms(0, 0, problema.m);
		System.out.println();
	}

	@Override
	public ArrayList<Solucao> executar() {

		resetExecution();
		System.out.println(pareto.getFronteira().size());

		//Inicia a populcaao
		initializeSwarms();

		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " ");

			for (int s = 0; s < swarms.length; s++) {
				if(i%10 == 0)
					System.out.print( "s" + s + " ");
				swarms[s].evolutionaryLoop();

			}


			//Exchange information between the swarms in every #this.update# iterations
			//Tree topology
			if(i % update == 0 || i == geracoes - 1){
				for (int s_i = 0; s_i < swarms.length/2; s_i++) {
					FronteiraPareto swarm_pareto_i = swarms[s_i].pareto;
					for(int son = 1; son<=2; son++){
						FronteiraPareto swarm_pareto_son = swarms[2*s_i+son].pareto;

						for (Iterator<Solucao> iterator = swarm_pareto_son.getFronteira()
								.iterator(); iterator.hasNext();) {
							SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
							SolucaoNumerica clone_solution = ((SolucaoNumerica)solucao.clone());
							clone_solution.used_objectives = swarms[s_i].used_objectives;
							swarm_pareto_i.add(clone_solution,  swarms[s_i].archiver);
						}
					}
				}
			}



			if(i % 10 ==0)
				System.out.println();
		}

		
		for (int s_i = 0; s_i < swarms.length; s_i++) {
			ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
			for (Iterator<Solucao> iterator = swarm_pareto_i
					.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				SolucaoNumerica clone_solution = ((SolucaoNumerica)solucao.clone());
				clone_solution.used_objectives = used_objectives;
				pareto.add(clone_solution, new UnboundArchive());
			}
		}

		

		return pareto.getFronteira();
	}

	@Override
	public ArrayList<Solucao> executarAvaliacoes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void defineSwarms(int index_tree,int index_b, int index_e){
		tree_structure[index_tree] = index_swarm++;
		
		
		/*for(int i = index_b; i< index_e; i++)
			System.out.print(i + ":true\t");
		System.out.println();*/
		
		createSwarm(index_tree, index_b, index_e);
		
		if((index_e-index_b)>1){
			int middle = (index_e - index_b)/2;
			int index_son1 = 2*index_tree + 1;
		
			defineSwarms(index_son1,index_b, (index_b+middle));
			int index_son2 = 2*index_tree + 2;
			defineSwarms(index_son2,(index_b+middle), index_e);
		}
			
	}
	
	public void createSwarm(int index, int index_b, int index_e){
		
		int num_obj = index_e - index_b;
		boolean used_objectives[] = new boolean[problema.m];
		for(int i = index_b; i< index_e; i++)
			used_objectives[i] = true;
		
		int popSize = populationSize * num_obj;
		
		System.out.println("Swarm " + tree_structure[index] + ": " + archiving_i[index] + "\t|\t" + leaderChoice_i[index] + "\t|\t" + S_i[index]+ "\t|\t" + index_b + "-" + index_e);
		
		swarms[index] = new SMPSO(n, problema, geracoes, numeroavalicoes, popSize, S_i[index], maxmim, populationSize, tipoRank, new Double(S_i[index]).doubleValue(), archiving_i[index], leaderChoice_i[index], eps, eval_analysis);
		swarms[index].ID = tree_structure[index] + "";
		swarms[index].used_objectives = used_objectives;
	}
	
	
	
	public void initializeSwarms(){
		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];
			swarm.inicializarPopulacao();
			swarm.atualizarRepositorio();
			calcularCrowdingDistance(swarms[i].pareto.getFronteira(), problema.m);
		}
		
		
		for (int i = 0; i < swarms.length; i++) {
			//In each swarm, the population chooses the leader according to its Pareto front  
			swarms[i].escolherLider.escolherLideres(swarms[i].populacao, swarms[i].pareto.getFronteira());
			//Initial mutation for swarm i
			swarms[i].escolherParticulasMutacao();
		}


	}
	
	public void resetExecution(){
		pareto = new FronteiraPareto(pareto.S, pareto.maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
		
		for(int i = 0; i<swarms.length; i++){
			swarms[i].populacao = new ArrayList<Particula>();
			swarms[i].pareto = new FronteiraPareto(swarms[i].pareto.S, swarms[i].pareto.maxmim, swarms[i].pareto.rank, swarms[i].pareto.eps, problema, swarms[i].pareto.archiveSize);
			
		}
		
		problema.avaliacoes =0; 
	}
	


}
