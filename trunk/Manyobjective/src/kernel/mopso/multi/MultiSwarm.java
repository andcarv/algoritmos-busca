package kernel.mopso.multi;

import java.io.IOException;
import java.io.PrintStream;
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

public class MultiSwarm extends AlgoritmoAprendizado {
	
	public MOPSO[] swarms;
	
	//Flag that defines if the pareto front of each multiswarm is shared or not
	public boolean shared = false;
	
	//Indicates in which iteration the global archiver will be updated by the archivers from the swarm
	public int update = 1;
	
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
	public MultiSwarm(int n, Problema prob, int generations, int evaluations, int popSize, String S, String[] maxmim, int repositorySize, String tRank, String archiving, String leaderChoice, double eps, int numberSwarms, boolean shared, int update, boolean eval_analysis){
		super(n,prob,generations,evaluations, popSize, maxmim,tRank, eps, repositorySize, archiving, eval_analysis);
		
		this.shared = shared;
		
		this.update = update;
		
		//If the parameters are different from the number of swarms, the remaining swarms are defined with the parameters of the SMPSO algorithm
		String[] S_i = S.split(";");
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
		String[] leaderChoice_i = leaderChoice.split(";");
		if(leaderChoice_i.length != numberSwarms){
			String[] temp = leaderChoice_i.clone();
			String default_ = leaderChoice_i[leaderChoice_i.length-1];
			leaderChoice_i = new String[numberSwarms];
			for (int i = 0; i < leaderChoice_i.length; i++) {
				if(i<temp.length)
					leaderChoice_i[i] = temp[i];
				else
					leaderChoice_i[i] = default_;
			}
		}
		String[] archiving_i_basis = archiving.split(";");
		String[] archiving_i = new String[numberSwarms];
		for(int i = 0; i< numberSwarms; i++){
			int index = i % archiving_i_basis.length;
			archiving_i[i] = archiving_i_basis[index];
		}
		
		swarms = new MOPSO[numberSwarms];
		
		for (int i = 0; i < swarms.length; i++) {
			String s = S_i[i];
			System.out.println("Swarm " + i + ": " + archiving_i[i] + "\t|\t" + leaderChoice_i[i] + "\t|\t" + s);
			if(!shared)
				swarms[i] = new SMPSO(n, prob, generations, evaluations, popSize, s, maxmim, repositorySize, tRank, new Double(s).doubleValue(), archiving_i[i], leaderChoice_i[i], eps, false);
			else
				swarms[i] = new SMPSO(n, prob, generations, evaluations, popSize, s, maxmim, repositorySize, tRank, new Double(s).doubleValue(), archiving_i[i], leaderChoice_i[i], eps, pareto, false);
		}
		
	}

	@Override
	public ArrayList<Solucao> executar() {
		
		try{
		PrintStream comunication = new PrintStream("evaluations/comunication.txt");
		int com = 0;
		
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
			
			if(eval_analysis){
				if(!shared){
					for (int s_i = 0; s_i < swarms.length; s_i++) {
						ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
						for (Iterator<Solucao> iterator = swarm_pareto_i
								.iterator(); iterator.hasNext();) {
							Solucao solucao = (Solucao) iterator.next();
							pareto.add(solucao, new UnboundArchive());
						}
					}
				}
				evaluationAnalysis(pareto.getFronteira());
				if(!shared)
					pareto.getFronteira().clear();
			}
			
			if(!shared){
				//Exchange information between the swarms in every #this.update# iterations
				//Ring topology
				if(i % update == 0 || i == geracoes - 1){
					comunication.println(com++ + "\t" + problema.avaliacoes);
					@SuppressWarnings("unchecked")
					ArrayList<Solucao> pareto_swarm_0 = (ArrayList<Solucao>)swarms[0].pareto.getFronteira().clone();
					for (int s_i = 0; s_i < swarms.length; s_i++){
						ArrayList<Solucao> new_pareto_swarm_i = null;
						if(s_i!= swarms.length -1)
							new_pareto_swarm_i = swarms[s_i+1].pareto.getFronteira();
						else
							new_pareto_swarm_i = pareto_swarm_0;
						ArrayList<Solucao> pareto_swarm_i = swarms[s_i].pareto.getFronteira();
						pareto_swarm_i.clear();
						for (Iterator<Solucao> iterator = new_pareto_swarm_i
								.iterator(); iterator.hasNext();) {
							Solucao solucao = (Solucao) iterator.next();
							pareto_swarm_i.add(solucao);
						}
					}
					/*
					for (int s_i = 0; s_i < swarms.length; s_i++) {
						FronteiraPareto swarm_pareto_i = swarms[s_i].pareto;
						for (int s_j = 0; s_j < swarms.length; s_j++) {
							if(s_i!=s_j){
								ArrayList<Solucao> swarm_pareto_j = swarms[s_j].pareto.getFronteira();
								for (Iterator<Solucao> iterator = swarm_pareto_j
										.iterator(); iterator.hasNext();) {
									Solucao solucao = (Solucao) iterator.next();
									swarm_pareto_i.add(solucao,  swarms[s_i].archiver);
								}
							}

						}
					}*/
				}
			}
			
			if(i % 10 ==0)
				System.out.println();
		}

		if(!shared){
			for (int s_i = 0; s_i < swarms.length; s_i++) {
				ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
				for (Iterator<Solucao> iterator = swarm_pareto_i
						.iterator(); iterator.hasNext();) {
					Solucao solucao = (Solucao) iterator.next();
					pareto.add(solucao, new UnboundArchive());
				}
			}

		}
		
		comunication.close();
		return pareto.getFronteira();
		}catch(IOException ex) {ex.printStackTrace(); return null;}
	}

	@Override
	public ArrayList<Solucao> executarAvaliacoes() {
		return null;
	}

	
	
	public void initializeSwarms(){
		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];
			swarm.inicializarPopulacao();
			swarm.atualizarRepositorio();
			if(!shared)
				calcularCrowdingDistance(swarms[i].pareto.getFronteira(), problema.m);
		}
		
		if(shared)
			calcularCrowdingDistance(pareto.getFronteira(), problema.m);
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
			if(!shared)
				swarms[i].pareto = new FronteiraPareto(swarms[i].pareto.S, swarms[i].pareto.maxmim, swarms[i].pareto.rank, swarms[i].pareto.eps, problema, swarms[i].pareto.archiveSize);
			else
				swarms[i].pareto = pareto;
		}
		
		problema.avaliacoes =0; 
	}

}
