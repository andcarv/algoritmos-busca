package kernel.misa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import pareto.AdaptiveGrid;
import pareto.FronteiraPareto;
import problema.Problema;

import solucao.ComparetorDominacao;
import solucao.ComparetorRank;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;


/**
 * Algoritmo MISA proposto por Coello em: Solving Multiobjective Optimization Problems Using
an Artificial Immune System
 * @author Andre
 *
 */
public class MISA extends AlgoritmoAprendizado {

	
	public ArrayList<Solucao> populacao = null;
	public AdaptiveGrid populacaoSecundaria = null;
	public ArrayList<Solucao> clones = null;
	
	//Total de elementos ap�s a clonagem
	public int totalClonagem;
	
	public double s;
	public double fator;

	//Taxa de clonagem
	public int taxaClonagem = 1;

	///Probabilidade da mutacao nao uniforme
	public double non_uniform_prob;
	public double decremento;
	
	private String[] maxmim = null;
	
	public int tamanhoGrid;
	
	/**
	 * 
	 * @param n N�mero de variaveis
	 * @param prob Problema
	 * @param g N�mero de gera��es
	 * @param t Tamanho m�ximo da popula��o
	 * @param s Valor do S do m�todo do Sato
	 * @param tc Taxa de clonagem
	 * * @param pg N�mero de partes da divis�o do grid da popula��o secund�ria
	 */
	public MISA(int n, Problema prob, int g, int a, int t, String s, int tc, int pg, String[] maxmim, String tRank, int tamanhoGrid, double e, String tPoda,boolean eval_analysis){
		super(n,prob,g,a,t, maxmim, tRank, e, tamanhoGrid, tPoda, eval_analysis);
		
		taxaClonagem = tc;
		totalClonagem = taxaClonagem * populationSize;
		partesGrid = pg;
		
		this.s = new Double(s).doubleValue();
		
		eps = e;
		
		this.maxmim = maxmim;
		
		//Probabilidade inicial da mutacao n�o uniforme
		non_uniform_prob = 0.6;
		//Decremento da muta��o n�o uniforme, variando do incial at� 1/n
		double diff = non_uniform_prob - PROB_MUT_COD;
		decremento = diff/(double) t;
		
		this.tamanhoGrid = tamanhoGrid;;
		
	}
	
	@Override
	
	public ArrayList<Solucao> executar() {
		
		//Inicio aleat�rio da popula��o
		iniciarPopulacao();
		
		problema.avaliacoes = 0;
		
		
		//La�o evolutivo
		for(int g = 0; g<geracoes; g++){
			if(g%10 == 0)
				System.out.print(g + " ");
			//Sele��o das melhores solu��es
			lacoEvolutivo();	
		}
		return populacao;
	}
	
	public ArrayList<Solucao> executarAvaliacoes() {
		
		//Inicio aleat�rio da popula��o
		iniciarPopulacao();
		//System.out.println(this.toString());
		problema.avaliacoes = 0;
		
		
		//La�o evolutivo
		while(problema.avaliacoes<numeroavalicoes){
			lacoEvolutivo();
			if(problema.avaliacoes % 10 == 0)
				System.out.print(problema.avaliacoes + " - ");
		}
		System.out.println();
		
		
	
		return populacao;
	}

	private void lacoEvolutivo() {
		encontrarSolucoesNaoDominadas(populacao, pareto);
		ArrayList<Solucao> melhores = obterMelhoresAnticorpos(pareto, populacao, 0.05);
		//Adiciona as melhores solu��es do problema no grid da populacao secundaria
		for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			solucao.aceita = populacaoSecundaria.add(solucao);
		}
		//Obt�m as solu��es presentes no grid
		clones = populacaoSecundaria.getAll();
		
		//Colna os melhores anticorpos
		clonarMelhoresAnticorpos(clones);	
		//Aplica uma muta��o em todos os clones
		mutacao(clones, PROB_MUT_COD);
		//Aplica uma muta��o n�o uniform nos clones dominados
		//mutacaoSolucoesNaoTaoBoas(clones);			
		//Adiciona todos os clones na popula��o atual
		populacao.addAll(clones);
		//Obt�m os novos l�deres da popula��o
		FronteiraPareto paretoTemp = new FronteiraPareto(pareto.S, maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
		encontrarSolucoesNaoDominadas(populacao, paretoTemp);
		//Reduz a popula��o com o tamanho passado como parametro
		reduzirPopulacao(populacao, paretoTemp);
	}
	
	/*for (Iterator iterator = clones.iterator(); iterator.hasNext();) {
		Solucao solucao = (Solucao) iterator.next();
		for (int i = 0; i < solucao.variaveis.length; i++) {
			if(solucao.variaveis[i]<0)
				System.out.println();
		}	
	}*/
	
	/**
	 * M�todo que inicia a popula��o aleatoriamente e inicia a popula��o secund�ria como vazia
	 */
	public void iniciarPopulacao(){
		populacao = new ArrayList<Solucao>();
		for(int i = 0; i<populationSize; i++){
			SolucaoNumerica s = new SolucaoNumerica(n, problema.m);
			s.iniciarSolucaoAleatoria();
			populacao.add(s);
			problema.calcularObjetivos(s);
		}
		
		populacaoSecundaria = new AdaptiveGrid(problema.m, partesGrid,tamanhoGrid );
		clones = null;
		pareto = new FronteiraPareto(pareto.S, maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
	}
	

	/**
	 * M�todo que reduz a popula��o para o tamanho m�ximo passado como parametro
	 * @param populacaoFinal Popula��o final da itera��o
	 * @param paretoTemp Conjunto das melhore solu��s encontradas at� ent�o
	 */
	public void reduzirPopulacao(ArrayList<Solucao> populacaoFinal, FronteiraPareto paretoTemp){
		//Se o n�mero das melhores solu��es � menor que o tamanho m�ximo, todas as solu��es s�o adicionadas na popula��o final 
		//e as melhores solu��es (domindas por menos solu��es) dominadas da popula��o
		//Caso contr�rio somente as melhore solu��es s�o adicionadas na popula��o final
		if(paretoTemp.getFronteira().size()<populationSize){
			ArrayList<Solucao> temp = obterMelhoresAnticorpos(paretoTemp, populacaoFinal, 1.0);
			populacaoFinal.clear();
			populacaoFinal.addAll(temp);
		}
		else{
			ArrayList<Solucao> solucoesFinais = paretoTemp.getFronteira();
			//Escolhe as melhores solu��es atraves do metodo AR 
			metodoRank.rankear(solucoesFinais, -1);
			ComparetorRank comp = new ComparetorRank();
			Collections.sort(solucoesFinais, comp);
			
			
			populacaoFinal.clear();
			for(int i = 0; i<populationSize; i++)
				populacaoFinal.add(solucoesFinais.get(i));	
		}
	}
	
	/**
	 * M�todo que obt�m as melhores solu��es. Caso o n�mero de solu��es seja menor que o valor porcentagemaMinima passado como parametro,
	 * as melhores dominadas solu��es s�o adicionadas nas melhores.
	 * @param paretoAtual Solu�oes n�o dominadas
	 * @param porcentagemaMinima Porcentagem m�nima de solu��es que devem ser retornadas
	 * @return
	 */
	public ArrayList<Solucao> obterMelhoresAnticorpos(FronteiraPareto paretoAtual, ArrayList<Solucao> populacao,  double porcentagemaMinima){
		ArrayList<Solucao> melhores = new ArrayList<Solucao>();
		melhores.addAll(paretoAtual.getFronteira());
		int maxMelhores = (int)(porcentagemaMinima*populationSize);
		//Caso o n�mero das melhores solu��es seja menor que a porcentamge tamanhoMelhores da popula��o deve-se preencher os array das melhores solu��es
		if(melhores.size()< maxMelhores){
			ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
			for (Iterator<Solucao> iterator = populacao.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				if(!melhores.contains(solucao)){
					dominadas.add(solucao);
				}
			}
			
			//Ordena as solu��es de acordo com o n�mero de dominacao de cada solucao
			ComparetorDominacao comp = new ComparetorDominacao();
			Collections.sort(dominadas, comp);
			int resto = maxMelhores - melhores.size();
			for(int i = 0; i<resto; i++)
				melhores.add((SolucaoNumerica) dominadas.get(i));	
		}
		return melhores;
	}
	
	/**
	 * Clona as melhores solu��es da itera��o
	 * @param clones Melhores solucoes da itera��o
	 */
	public void clonarMelhoresAnticorpos(ArrayList<Solucao> clones){
		//Calcula o numero estimado de clones para cada elemento a ser colnado
		int numEstimadoClones = (taxaClonagem*populacao.size())/populacaoSecundaria.size();
		clones = new ArrayList<Solucao>();
		if(populacaoSecundaria.isFull())
			clonarFull(numEstimadoClones, populacaoSecundaria.getAll());
		else
			clonarNotFull(numEstimadoClones, populacaoSecundaria.getAll());
		
	}
	
	/**
	 * Clona as melhores solu��es caso a popula��o secund�ria esteja cheia
	 * @param numBase N�mero estimado de clones para cada solu��o
	 * @param melhores Solu��es que ser�o clonadas
	 */
	public void clonarFull(int numBase, ArrayList<Solucao> melhores){
		for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			double fator = 1;
			if(!solucao.aceita)
				fator = 0;
			else{
				//Calcula quanto cada elemento vai ser clonado de acordo com a ocupa��o da c�lula em que o elemento pertence no grid
				double mediaOcupacao = populacaoSecundaria.obterMediaOcupacao();
				Integer celula = populacaoSecundaria.contains(solucao);				
				int crowdCelula = populacaoSecundaria.obterLotacao(celula, solucao);
				if(crowdCelula<mediaOcupacao)
					fator = 2;
				else
					fator = 0.5;
			}
			for(int j = 0; j<numBase*fator; j++){
				SolucaoNumerica novaSolucao = (SolucaoNumerica)solucao.clone();
				clones.add(novaSolucao);
			}
			
		}
		
	}
	
	/**
	 * Clona as melhores solu��es caso a popula��o secund�ria n�o esteja cheia
	 * @param numBase N�mero estimado de clones para cada solu��o
	 * @param melhores Solu��es que ser�o clonadas
	 */
	public void clonarNotFull(int numBase, ArrayList<Solucao> melhores){
		double[][] distancias = new double[melhores.size()][melhores.size()];
		double[] mediasIndividuais = new double[melhores.size()];
		double mediaDistancias = 0;
		double numDist = 0;
		for(int i = 0; i< melhores.size()-1;i++){
			SolucaoNumerica solucao1 = (SolucaoNumerica)melhores.get(i);
			for(int j = i+1; j<melhores.size(); j++){
				SolucaoNumerica solucao2 = (SolucaoNumerica)melhores.get(j);
				distancias[i][j] = distanciaEuclidiana(solucao1.objetivos, solucao2.objetivos);
				mediaDistancias+= distancias[i][j];
				mediasIndividuais[i] += distancias[i][j];
				mediasIndividuais[j] += distancias[i][j];
				numDist++;
			}
		}
		
		mediaDistancias = mediaDistancias/numDist;
		for (int i = 0; i < mediasIndividuais.length; i++) {
			mediasIndividuais[i] = mediasIndividuais[i]/(mediasIndividuais.length-1);
		}
		
		int densidadeAbaixo = 0;
		int densidadeAcima = 0;
		
		for(int i = 0; i< mediasIndividuais.length;i++){
			if(mediasIndividuais[i]<mediaDistancias)
				densidadeAbaixo++;
			else
				densidadeAcima++;
		}
		
		int i = 0;
		for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			double fator = 1;
			//Se a solu��o pertence � parte de baixo
			if(mediasIndividuais[i]<mediaDistancias){
				//Se a parte de baixo � mais densa ent�o reduz o n�mero de clones em 50%
				if(densidadeAbaixo>=densidadeAcima)
					fator = 0.5;
				else
					//Caso contr�rio aumenta o n�mero em 50%
					fator = 1.5;
			} else {
				//Se a solu��o pertence � parte de cima
				if(mediasIndividuais[i]>mediaDistancias)
					//Se a parte de cima � mais densa reduz em 50%, caso contr�rio aumenta em 50%
					if(densidadeAbaixo<densidadeAcima)
						fator = 0.5;
					else
						fator = 1.5;
			}
			i++;
			for(int j = 0; j<numBase*fator; j++){
				SolucaoNumerica novaSolucao = (SolucaoNumerica)solucao.clone();
				clones.add(novaSolucao);
			}	
		}
	}
	
	/**
	 * Aplica uma muta��o polinomial com probabilidade prob em todos os clones
	 * @param solucoes
	 */
	public void mutacao(ArrayList<Solucao> solucoes, double prob){
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();

			mutacao(prob, solucao);
			
			solucao.truncar();
			
			problema.calcularObjetivos(solucao);		

		}
	}
	
	public void mutacaoSolucoesNaoTaoBoas(ArrayList<Solucao> clones){
		
		FronteiraPareto clonesNaoDominados = new FronteiraPareto(pareto.S, maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
		encontrarSolucoesNaoDominadas(clones, clonesNaoDominados);
		ArrayList<Solucao> melhores = clonesNaoDominados.getFronteira();
		
		ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
		for (Iterator<Solucao> iterator = clones.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			if(!melhores.contains(solucao)){
				dominadas.add(solucao);
			}
		}
		
		mutacao(dominadas, non_uniform_prob);
		non_uniform_prob -= decremento;
		
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		int tamPop = 0;
		int tamClone = 0;
		int tamSec = 0;
		
		
		if(populacao !=null)
			tamPop = populacao.size();
		if(clones !=null)
			tamClone = clones.size();
		if(populacaoSecundaria!=null)	
			tamSec = populacaoSecundaria.size();
			
		buff.append("Populacao: " +tamPop + "\n");
		buff.append("Clones: " +tamClone + "\n");
		buff.append("Secundaria: " + tamSec + "\n");
		buff.append("Pareto: " + pareto.getFronteira().size());
		return buff.toString();
	}
	

	
	public static void main(String[] args) {

		
	}
	

}
