package kernel.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.Problema;

import rank.RankDominancia;
import solucao.ComparetorCrowdedOperator;
import solucao.ComparetorRank;
import solucao.ComparetorRankDistancia;
import solucao.SolucaoBinaria;
import solucao.SolucaoNumerica;
import solucao.Solucao;

import kernel.AlgoritmoAprendizado;

public class NSGA2 extends AlgoritmoAprendizado {
	
	public ArrayList<Solucao> populacao;
	public ArrayList<Solucao> offspring;
	
	private ComparetorRank compRank = new ComparetorRank();
	private ComparetorCrowdedOperator compCrwd = new ComparetorCrowdedOperator();
	private ComparetorRankDistancia compDist = new ComparetorRankDistancia();
	
	
	public String tipoSolucao = null;
	
	private String[] maxmim = null;
	
	public String tipoPoda = "p-crowd";
	
	
	
	public NSGA2(int n, Problema prob, int g, int a, int t, String s, String ts, String[] maxmim, String tRank, String tPoda, double eps, int tamRep, boolean eval_analysis){
		super(n,prob,g, a,t,  maxmim,tRank, eps, tamRep, tPoda, eval_analysis );
		metodoRank = new RankDominancia(problema.m);
		this.maxmim = maxmim;
		pareto = new FronteiraPareto(new Double(s).doubleValue(), maxmim,rank, this.eps, problema, archiveSize);
		metodoRank.setPareto(pareto);
		problema = prob;
		tipoSolucao = ts;
		
		tipoPoda = tPoda;;

	}

	@Override
	public ArrayList<Solucao> executar() {
		
		
		pareto = new FronteiraPareto(pareto.S, maxmim, rank,  pareto.eps, problema, pareto.archiveSize);
		populacao = new ArrayList<Solucao>();
		offspring = new ArrayList<Solucao>();
		
		iniciarPopulacao();
		metodoRank.rankear(populacao, -1);
		
		
		gerarOffsping(populacao, compRank);
		ArrayList<Solucao> populacaoCombinada = new ArrayList<Solucao>();
		
		for(int g = 0; g<geracoes; g++){
			if(g%10 == 0)
				System.out.print(g + " ");
			lacoEvolutivo(populacaoCombinada);
		}
		
		/*for (Iterator<Solucao> iterator = populacao.iterator(); iterator
				.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			pareto.add(solucao);
			
		}
		
		return pareto.getFronteira();*/
		
		return populacao;
	}
	
	
	public ArrayList<Solucao> executarAvaliacoes() {
		
		populacao = new ArrayList<Solucao>();
		offspring = new ArrayList<Solucao>();
		
		iniciarPopulacao();
		metodoRank.rankear(populacao, -1);
		
		problema.avaliacoes = 0;

		
		gerarOffsping(populacao, compRank);
		ArrayList<Solucao> populacaoCombinada = new ArrayList<Solucao>();
		
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%1000 == 0)
				System.out.print(problema.avaliacoes + " - " + numeroavalicoes + " ");
			lacoEvolutivo(populacaoCombinada);
		}
		
		for (Iterator<Solucao> iterator = populacao.iterator(); iterator
		.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			pareto.add(solucao, archiver);

		}

		return pareto.getFronteira();
	}

	private void lacoEvolutivo(ArrayList<Solucao> populacaoCombinada) {
		populacaoCombinada.addAll(populacao);
		populacaoCombinada.addAll(offspring);
		metodoRank.rankear(populacaoCombinada, -1);
		
		calcularCrowdingDistanceFront(populacaoCombinada);
		
		if(tipoPoda.equals("p-ideal")){
			calcularDistanciaIdeal(populacaoCombinada);
		    Collections.sort(populacaoCombinada, compDist);
		}
		else
			Collections.sort(populacaoCombinada, compCrwd);

		
		populacao.clear();
		for(int i = 0; i<populationSize; i++){
			Solucao solucao = populacaoCombinada.get(i);
			populacao.add(solucao);
		}
	
		calcularCrowdingDistanceFront(populacao);
		gerarOffsping(populacao, compCrwd);
		populacaoCombinada.clear();
		
	}
	
	/**
	 * Metodo que calcula a crowding distance de cada front separadamente
	 * @param populacao
	 */
	public void calcularCrowdingDistanceFront(ArrayList<Solucao> populacao){
		
		ComparetorRank comp = new ComparetorRank();
		Collections.sort(populacao, comp);
		int rank = 0;
		ArrayList<Solucao> temp = new ArrayList<Solucao>();
		for (Iterator<Solucao> iterator = populacao.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			if(solucao.rank == rank)
				temp.add(solucao);
			else{				
				calcularCrowdingDistance(temp, problema.m);
				temp.clear();
				temp.add(solucao);
				rank++;				
			}			
		}
		
		
		 
	}
	
	public void gerarOffsping(ArrayList<Solucao> solucoes, Comparator<Solucao> comp){
		offspring.clear();
		for(int i = 0; i<populationSize; i++){
			Solucao pai1 = escolherPaiBinaryTournament(solucoes, comp); 
			Solucao pai2 = escolherPaiBinaryTournament(solucoes, comp);
			Solucao filho = recombinacao(pai1, pai2);
			mutacao(PROB_MUT_COD, filho);
			if(filho.isNumerica())
				((SolucaoNumerica)filho).truncar();
			problema.calcularObjetivos(filho);
			offspring.add(filho);
		}
	}
	
	public void iniciarPopulacao(){
		populacao = new ArrayList<Solucao>();
		for(int i = 0; i<populationSize; i++){
			Solucao s = null;
			if(tipoSolucao.equals("numerica"))
				s = new SolucaoNumerica(n, problema.m);
			else
				s = new SolucaoBinaria(n, problema.m);
			s.iniciarSolucaoAleatoria();
			problema.calcularObjetivos(s);
			populacao.add(s);
		}
	}
	

	
	/**
	 * Faz a recombinacao de duas solucoes pais
	 * @param solucao1 Pai 1
	 * @param solucao2 Pai 2
	 * @return Solucao com a combinacao dois pais
	 */
	public Solucao recombinacao(Solucao solucao1, Solucao solucao2){
		//Solucao retorno
		Solucao novaSolucao = null;
		if(solucao1.isNumerica())
			novaSolucao = recombinacaoNumerica((SolucaoNumerica)solucao1, (SolucaoNumerica)solucao2);
		else
			novaSolucao = recombinacaoBinaria((SolucaoBinaria) solucao1, (SolucaoBinaria) solucao2);
		return novaSolucao;
	}
	
	public SolucaoBinaria recombinacaoBinaria(SolucaoBinaria solucao1, SolucaoBinaria solucao2){
		int fator = (int)Math.ceil(Math.log10(n));
		int pontoJuncao = (int)((Math.random()*fator) % n);
		SolucaoBinaria novaSolucao = (SolucaoBinaria)solucao1.clone();
		
		
		for (int i = pontoJuncao; i < solucao2.n; i++) {
			novaSolucao.setVariavel(i,  solucao2.getVariavel(i));
		}
		
		return novaSolucao;
	}

	
	public SolucaoNumerica recombinacaoNumerica(SolucaoNumerica solucao1, SolucaoNumerica solucao2){
		int fator = (int)Math.ceil(Math.log10(n));
		int pontoJuncao = (int)((Math.random()*fator) % n);
		SolucaoNumerica novaSolucao = (SolucaoNumerica)solucao1.clone();
		for (int i = pontoJuncao; i < solucao2.n; i++) {
			novaSolucao.setVariavel(i,  solucao2.getVariavel(i));
		}
		return novaSolucao;
	}
	
	/**
	 * Escolhe uma solucao atraves do metodo Binary Tournament
	 * @param solucoes Conjunto em que uma solucao sera escolhida
	 * @param comp Comparetor que define como uma solucao eh melhor que a outra
	 * @return Solucao escolhida
	 */
	public Solucao escolherPaiBinaryTournament(ArrayList<Solucao> solucoes, Comparator<Solucao> comp){
		int ordem = (int)Math.ceil(Math.log10(solucoes.size()));
		int indice1 = (int)(Math.random()*(Math.pow(10, ordem))%solucoes.size());
		int indice2 = (int)(Math.random()*(Math.pow(10, ordem))%solucoes.size());
		Solucao solucao1 = solucoes.get(indice1);
		Solucao solucao2 = solucoes.get(indice2);
		int result  = comp.compare(solucao1, solucao2);
		if(result == -1)
			return solucao1;
		if(result == 1)
			return solucao2;
		
		//Caso as solucoes nao se dominem a escolha eh aleatoria
		int indice = (int)(Math.random()*10%2);
		if(indice ==0)
			return solucao1;
		else
			return solucao2;
	}
	


}
