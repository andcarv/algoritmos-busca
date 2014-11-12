package pareto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import archive.PreciseArchiver;

import problema.Problema;
import kernel.AlgoritmoAprendizado;

import solucao.ComparetorDistancia;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class FronteiraPareto {
	
	private ArrayList<Solucao> front = null;
	
	//public ArrayList<Particula> fronteiraNuvem = null;
	
	public double S;
	
	public double limite_ocupacao;
	
	public boolean rank;
	
	public double[] objetivosMaxMin = null;
	
	public String[] maxmim = null;
	
	

	public double fator;
	
	public double eps;
	
	
	
	public Problema problema;
	
	public int archiveSize;
	
	public static final int DEFAULT_VALUE = -1; 
	
	
	
	public static int DOMINATED_BY = -1;
	public static int DOMINATES = 1;
	public static int NON_DOMINATED = 0;
	public static int EQUALS = 2;
	
	public double[] average_point;
	
	/*public FronteiraPareto(double s){
		fronteira = new ArrayList<Solucao>();
		fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		
	}*/
	
	public FronteiraPareto(String[] maxmim){
		preencherObjetivosMaxMin(maxmim);
	}
	
	public FronteiraPareto(double s, String[] maxmim, boolean r, double e, Problema prob, int as){
		front = new ArrayList<Solucao>();
		//fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		rank= r;
		this.maxmim = maxmim;
		
		eps = e;
		
		problema = prob;
		
		preencherObjetivosMaxMin(maxmim);
		
		archiveSize = as;
	}
	
	
	
	
	
	

	/**
	 * Metodo que define para cada objetivo se ele e de maximizacao ou minimizacao
	 * @param maxmim
	 */
	public void preencherObjetivosMaxMin(String[] maxmim){
		objetivosMaxMin = new double[maxmim.length];
		for (int i = 0; i < maxmim.length; i++) {
			if(maxmim[i].equals("+"))
				objetivosMaxMin[i] = 1;
			else
				objetivosMaxMin[i] = -1;
		}
	}
	
	public void setFronteira(ArrayList<Solucao> temp){
		front.clear();
		for (Iterator<Solucao> iter = temp.iterator(); iter.hasNext();) {
			Solucao s = (Solucao) iter.next();
			front.add(s);
			
		}
	}
	
	
	
	public void apagarFronteira(){
		front.clear();
	}
	
	
	
	/**
	 * Metodo que adiciona um nova solucao na fronteira de pareto - Arquivador precise
	 * Some Multiobjective Optimizers are Better than Others - Corne and Knowles
	 * @param Solcao solucaos a ser adicionada
	 * @return Valor double que indica por quantas solucoes o elemento eh dominado 
	 */
	@SuppressWarnings("unchecked")
	public double add(Solucao solucao, PreciseArchiver archiver){
		solucao.numDominacao = 0;
		solucao.numDominadas = 0;
		if(front.size()==0){
			front.add(solucao);
			return solucao.numDominacao;
		}

		if(!archiver.ID.equals("eapp") && !archiver.ID.equals("eaps")){

			int comp;

			ArrayList<SolucaoNumerica> cloneFronteira = (ArrayList<SolucaoNumerica>)front.clone();

			double[] novosObjetivosSolucao = new double[solucao.objetivos.length];
			if(S!=0.5 && S>=0.25){
					novosObjetivosSolucao = modifySelectedObjectives(modificacaoDominanciaParetoCDAS(solucao.objetivos, S), solucao.used_objectives);
			} else{
				//novosObjetivosSolucao  = modificacaoDominanciaParetoEqualizar(solucao.objetivos, fator);
				
					//scdas(S);
					novosObjetivosSolucao  = modifySelectedObjectives(solucao.objetivos, solucao.used_objectives);
				
				//System.out.println();
			}

			for (Iterator<SolucaoNumerica> iter = cloneFronteira.iterator(); iter.hasNext();) {
				SolucaoNumerica temp = (SolucaoNumerica) iter.next();

				double[] novosObjetivosTemp = new double[temp.objetivos.length];

				if(S!=0.5 && S>=0.25){
						novosObjetivosTemp =  modifySelectedObjectives(modificacaoDominanciaParetoCDAS(temp.objetivos, S), temp.used_objectives);
				} else{
				
						//scdas(S);
						novosObjetivosTemp = modifySelectedObjectives(temp.objetivos, temp.used_objectives);
				
				}				
				//novosObjetivosTemp = modificacaoDominanciaParetoEqualizar(temp.objetivos, fator);
				
				compareSelectedObjectives(solucao, temp);

				comp = compareObjectiveVector(novosObjetivosSolucao, novosObjetivosTemp);

				if(comp == DOMINATED_BY || comp == EQUALS){
					solucao.numDominacao++;
					//	System.out.println("dominada por: " +temp.indice);
				}
				if(comp == DOMINATES){
					front.remove(temp);
					solucao.numDominadas++;
					//System.out.println("domina: " + temp.indice);
				}

			}
			if(solucao.numDominacao == 0){
				if(solucao.numDominadas>0)
					front.add(solucao);
				else{
					if(front.size()>=archiveSize)
						archiver.filter(front, solucao);
					else
						front.add(solucao);
				}

			}			
		} else{
			if(archiver.ID.equals("eapp"))
				return adpativeEpsApprox(solucao);
			else
			if(archiver.ID.equals("eaps"))
				return adpativeEpsParetoSet(solucao);
		}
		return solucao.numDominacao;
	}
	
	/**
	 * Epsilon dominance algorithm (Algorithm 1)- Combining Convergence and Diversity in Evolutionary Multi-Objective Optimization
	 * Laumanns, Thiele, Deb , 2002
	 * @param solution Candidate solution to be added to the archive
	 * @return Number of solutions that dominate the candidate
	 */
	public double adpativeEpsApprox(Solucao solution){
		int comp, comp2;
		
		ArrayList<Solucao> dominated = new ArrayList<Solucao>();

		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();) {
			SolucaoNumerica solution_archive = (SolucaoNumerica) iter.next();

			double[] newObjSolArchive = new double[solution_archive.objetivos.length];

			
			newObjSolArchive = modificacaoDominanciaParetoEpsilon(solution_archive.objetivos, eps);				
			

			comp = compareObjectiveVector(solution.objetivos, newObjSolArchive);
			comp2 = compareObjectiveVector(solution.objetivos, solution_archive.objetivos);

			if(comp == DOMINATED_BY){
				solution.numDominacao++;
				//	System.out.println("dominada por: " +temp.indice);
			}
			if(comp2 == DOMINATES){
				dominated.add(solution_archive);
				solution.numDominadas++;
				//System.out.println("domina: " + temp.indice);
			}
		}
		
		//If the candidate is not eps-dominated by any other f' in the archive
		if(solution.numDominacao == 0){
			front.add(solution);
			//If any f' is dominated by the candidate, it is removed from the front
			for (Iterator<Solucao> iterator = dominated.iterator(); iterator.hasNext();) {
				Solucao dominada = (Solucao) iterator.next();
				front.remove(dominada);				
			}

		}				
		return solution.numDominacao;
	}
	
	public double adpativeEpsParetoSet(Solucao solution){

		int comp, comp2;
		ArrayList<Solucao> box_dominated = new ArrayList<Solucao>();
		ArrayList<Solucao> objective_dominated = new ArrayList<Solucao>();

		double[] box = box(solution); 
		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();) {
			SolucaoNumerica solution_archive = (SolucaoNumerica) iter.next();
			double box_archive[] = box(solution_archive);
			comp = compareObjectiveVector(box, box_archive);
			if(comp == DOMINATES){
				box_dominated.add(solution_archive);
				solution.numDominadas++;
			}

			comp2 = compareObjectiveVector(solution.objetivos, solution_archive.objetivos);

			//If box(f') == box(f) and f dominates f'
			if(AlgoritmoAprendizado.vector_equality(box, box_archive) && comp2 == DOMINATES){
				objective_dominated.add(solution_archive);
			} 

			//If box(f') == box(f) or box(f') dominates box(f)
			if(AlgoritmoAprendizado.vector_equality(box, box_archive) || comp == DOMINATED_BY){
				solution.numDominacao++;
			}
		}

		if(box_dominated.size()>0){
			// A' = A U f / D
			front.add(solution);
			for (Iterator<Solucao> iterator = box_dominated.iterator(); iterator.hasNext();) {
				Solucao dominated = (Solucao) iterator.next();
				front.remove(dominated);				
			}
		} else{	
			//If box(f') == box(f) and f dominates f'
			if(objective_dominated.size()>0){
				// A' = A u f /f'
				front.add(solution);
				for (Iterator<Solucao> iterator = objective_dominated.iterator(); iterator.hasNext();) {
					Solucao dominated = (Solucao) iterator.next();
					front.remove(dominated);				
				}
			} else{
				//If don't exist f' | box(f') == box(f) or box(f') dominates box(f)
				if(solution.numDominacao == 0)
					//A' = A U f
					front.add(solution);
			}
		}
		
		return solution.numDominacao;

	}
	
	public double[] box(Solucao solution){
		double[] box = new double[solution.objetivos.length];
		for (int i = 0; i < solution.objetivos.length; i++) {
			double num = solution.objetivos[i];
			box[i] = Math.floor(Math.log(num)/Math.log(1+eps));
			//box[i] = Math.floor(num/eps);
		}
		return box;
	}

	public void addRank(Solucao solucao){
		if(front.size()==0){
			front.add(solucao);
		}
		
		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();) {
			Solucao temp = (Solucao) iter.next();
			if(solucao.rank< temp.rank){
				front.add(solucao);
				break;
			}
		}
		
		if(front.size() > archiveSize)
			podarLideresCrowdedOperator();
		
	}

	
	public void podarLideresCrowdedOperator(){
			
		AlgoritmoAprendizado.calcularCrowdingDistance(front, problema.m);
		
		double highCDValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.crowdDistance > highCDValue){
				highCDValue = solucao.crowdDistance;
				index = i;
			}
		}
		
		front.remove(index);
		
	}
	
	public void archivingSPEA2(Solucao nova_solucao){
		

		front.add(nova_solucao);
		
		int k = 1;
		
		AlgoritmoAprendizado.calculateKNeareastNeighbour(front, k);

		double highKNNValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.knn > highKNNValue){
				highKNNValue = solucao.knn;
				index = i;
			}
		}
		
		front.remove(index);

	}
	
	public void podarLideresAleatorio(Solucao nova_solucao){
		
		front.add(nova_solucao);
		double num = AlgoritmoAprendizado.random.nextDouble();
		int indice = (int) (Math.round(num*front.size())) % front.size();
		
		front.remove(indice);
			
			
		
	}
	
	/**
	 * Metodo que deixa que poda o repositorio em tamanhoRepositorio, com as solucoes com menor distancia
	 * A distancia pode ser calculada atraves de diferentes metodos
	 */
	public void podarLideresDistancia(Solucao nova_solucao){
		front.add(nova_solucao);
			
		double highDistanceValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.menorDistancia > highDistanceValue){
				highDistanceValue = solucao.menorDistancia;
				index = i;
			}
		}
		
		front.remove(index);
	}

	
	
	/**
	 * Seleciona as solucoes mais proximas ao extremo e mais proximas a solucao ideal, em partes iguais
	 * @param archiveSize
	 * @param m
	 * @param ideal
	 */
	public void podarLideresIdeal(Solucao nova_solucao, Solucao ideal){
		
		front.add(nova_solucao);
		//Para cada solucao calcula sua distancia em relacao a solucao ideal
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(ideal.objetivos, solucao.objetivos);
			//Arredonda a distancia para 4 casas decimais para que a distancia de crowding seja utilizada para diferenciar as solucoes proximas a ideal
			BigDecimal b = new BigDecimal(solucao.menorDistancia);		 
			solucao.menorDistancia = (b.setScale(4, BigDecimal.ROUND_UP)).doubleValue();
		}

		//Ordena as solucoes em relacao a distancia do idal
		ComparetorDistancia comp = new ComparetorDistancia();
		Collections.sort(front, comp);
		front.remove(front.remove(front.size()-1));
		

	}
	
	
	
	/**
	 * Seleciona as solucoes mais proximas ao extremo e mais proximas a solucao ideal, em partes iguais
	 * @param tamanhoRepositorio
	 * @param m
	 * @param ideal
	 */
	public void podarLideresExtremosIdeal(Solucao nova_solucao, Solucao ideal, int m, int tamanhoRepositorio){
		front.add(nova_solucao);
		ArrayList<Solucao> solucoes = getFronteira();
		//Se o numero de solucoes eh maior que o tamanho definido para o repositorio

		//Calcula a proporcao de solucoes selecionadas para cada extremo e para o ideal
		double proporcao = 1.0/(m+1);
		int num_sol = (int)(tamanhoRepositorio*proporcao);

		ArrayList<Solucao> selecionadas = new ArrayList<Solucao>();

		//Percorre todos os objetivo obtende as solucoes com menores valores (nos extremos)
		for(int i = 0; i< m; i++){
			int contador = 0;
			//Ordena as solcoes de acordo com o objetivo i
			ComparetorObjetivo comp = new ComparetorObjetivo(i);
			Collections.sort(solucoes, comp);
			int j = 0;
			//Preenche a lista "selecionadas" com as menore solucoes por objetivo. Evita colocar solucoes repetidas
			while(contador<num_sol){
				Solucao solucao = solucoes.get(j++);
				if(!selecionadas.contains(solucao)){
					selecionadas.add(solucao);
					contador++;
				}
			}
		}

		//Para cada solucao calcula sua distancia em relacao a solucao ideal
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(ideal.objetivos, solucao.objetivos);				
		}

		//Ordena as solucoes em relacao a distancia do idal
		ComparetorDistancia comp = new ComparetorDistancia();
		Collections.sort(solucoes, comp);

		int contador = 0;
		int j = 0;
		//Preenche o resto das solucoes selecionadas
		int tamanho = tamanhoRepositorio - selecionadas.size();
		while(contador<tamanho){
			Solucao solucao = solucoes.get(j++);
			if(!selecionadas.contains(solucao)){
				selecionadas.add(solucao);
				contador++;
			}
		}
		setFronteira(selecionadas);
	}
		
	
	public ArrayList<Solucao> getFronteira(){
		return front;
	}
	
	public String toString(){
		return front.toString();
	}
	/**
	 * M�todo que verifica se uma solucao domina a outra
	 * @param sol1 Solucao que sera comparada com as regras pertencentes a fronteira de pareto
	 * @param sol2 Solucao pertencente a fronteira de pareto
	 * @return -1 Se sol1 for dominada, 0 se a sol1 nao domina nem eh dominada, 1 sol1 domina sol2 
	 */
	public int compareObjectiveVector(double[] sol1, double[] sol2){
		//Contador que marca quantos valores da solucao 1 sao maiores que os da solucao 2
		//Se cont for igual ao tamanho dos elementos da solucao 1 entao a solucao 2 eh dominada pela sol1
		//Se cont for igual a 0 a sol2 domina a sol1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		
		int cont = 0; 
		int cont2 = sol1.length;
		for (int i = 0; i < sol1.length; i++) {
			
			double sol1_i = sol1[i];
			double sol2_i = sol2[i];
			
			if(sol1_i*objetivosMaxMin[i]>sol2_i*objetivosMaxMin[i]){
				++cont;
			} else {
				if(sol1_i==sol2_i){
					--cont2;
				}
			}
		}
		if(cont == 0){	
			if(cont2 == 0)
				return EQUALS;
			else
				return DOMINATED_BY;
		}
		else{
			if(cont>0 && cont<cont2)
				return NON_DOMINATED;
			else return DOMINATES;
		}
	}
	
	/**
	 * Check if the two solutions are optimizing the same set of objectives
	 * @param solution1
	 * @param solution2
	 * @return
	 */
	public boolean compareSelectedObjectives(Solucao solution1, Solucao solution2){
		for(int i = 0; i<problema.m;i++){
			if(solution1.used_objectives[i]){
				if(!solution2.used_objectives[i]){
					System.err.println("Solutions with different selected objectives");
					System.exit(0);
					return false;
				}
			} else{
				if(solution2.used_objectives[i]){
					System.err.println("Solutions with different selected objectives");
					System.exit(0);
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public double[] modifySelectedObjectives(Solucao solution){
		double[] new_objective_value = new double[problema.m];
		for (int i = 0; i < new_objective_value.length; i++) {
			if(solution.used_objectives[i])
				new_objective_value[i] = solution.objetivos[i];
			else
				new_objective_value[i] = DEFAULT_VALUE;
		}
		
		return new_objective_value;
	}
	
	public double[] modifySelectedObjectives(double[] solution_objectives, boolean[] used_objectives){
		double[] new_objective_value = new double[problema.m];
		for (int i = 0; i < new_objective_value.length; i++) {
			if(used_objectives[i])
				new_objective_value[i] = solution_objectives[i];
			else
				new_objective_value[i] = DEFAULT_VALUE;
		}
		
		return new_objective_value;
	}
	
	/*public int compararMedidas(Solucao sol1, Solucao sol2){
		if(sol1.rank>sol2.rank)
			return -1;
		else{
			if(sol1.rank<sol2.rank)
				return 1;
				else
					return 0;
		}
	}*/
	
	/**
	 * Modificacao da fronteira de pareto pelo metodo CDAS
	 */
	public static double[] modificacaoDominanciaParetoCDAS(double[] fx, double S){
		double r = r(fx);
		double[] retorno = new double[fx.length];
		for (int i = 0; i < fx.length; i++) {
			retorno[i] = modificacaoCDASValor(fx[i], r, S*Math.PI);
		}
		
		return retorno;
	}
	
	public double[][] getReferenciasScdas(double delta){
		int m = problema.m;
		double referencia[][]=new double[m][m];
		
		double menores[]=new double[m]; //menor valor de cada objetivo
		
		for (int i = 0; i < menores.length; i++) {
			menores[i] = Double.MAX_VALUE;
		}
		double maiores[]=new double[m];
		
		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();){//percorrendo as particulas
			Solucao particula = iter.next();
			for(int i=0;i<m;i++){//percorrendo os objetivos e pegando os maiores e menores
				if(particula.objetivos[i]<=menores[i]){
					menores[i]= Math.max(particula.objetivos[i]-delta,0);
				}
				if(particula.objetivos[i]>=maiores[i]){
					maiores[i]=particula.objetivos[i]-delta;
				}
			}
		}
		for(int i=0;i<m;i++){//montando os conjuntos de referencia
			referencia[i]=menores.clone();
			referencia[i][i]=maiores[i];
		}
		
		return referencia;
	}
	
	/**
	 * Método S-CDAS
	 */
	public void scdas(double delta){
		Solucao[] frontOriginal=new Solucao[front.size()];
		int part=0;
		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();){
			Solucao particula=iter.next(); //copiando front
			frontOriginal[part] = particula;
			part++;
		}
		double[][][] frontModificado=calcularScdas(delta);
		//refinarScdas(frontOriginal, frontModificado);	
		refinarScdasOriginal(frontOriginal, frontModificado);
	}
	/*
	 * Método que refina o front verificando a dominância entre
	 * as partículas do front modificado
	 * @param frontOriginal - cópia do front original, uma vez que ele vai ser destruído
	 * @param frontModificado - front modificado utilizando a fórtuma do S-cdas
	 */
	public void refinarScdasOriginal(Solucao[] frontOriginal, double[][][] frontModificado){
		front.clear();
		
		int adicionar[]  = new int[frontOriginal.length];
		for(int p=0;p<frontOriginal.length;p++){
			for(int k=0;k<frontModificado[p].length;k++){
				if(p != k){
					//if((frontModificado[p][0]==0 && frontModificado[p][1]==0) || (frontModificado[k][0]==0 && frontModificado[k][1]==0) || (Double.compare(frontModificado[p][0],Double.NaN)==0 || Double.compare(frontModificado[p][1],Double.NaN)==0) || (Double.compare(frontModificado[k][0],Double.NaN)==0 || Double.compare(frontModificado[k][1],Double.NaN)==0)){
						//if que elimina todos os pontos [0,0] e todos os pontos com NaN
						//System.out.println("erro!");
					//}else{
					
						//System.out.println("comp: p: "+p+" ["+frontOriginal[p].objetivos.clone()[0]+", "+frontOriginal[p].objetivos.clone()[1]+"] == k:"+k+" ["+frontModificado[k][0]+", "+frontModificado[k][1]+"]\torig: ["+frontOriginal[k].objetivos[0]+", "+frontOriginal[k].objetivos[1]+"]");
					double[] vetor_original = frontModificado[p][p];
					double[] vetor_modificado = frontModificado[p][k];
					
						int comp = compareObjectiveVector(vetor_original, vetor_modificado);
	
						if(comp == 1){ //(x,y) - y dominada por x
							adicionar[k] = 1;
						}
					//}
				}
			}
			//if(dominada == 0){
				//front.add(frontOriginal[p]);
			//}
		}
		
		for (int i = 0; i < adicionar.length; i++) {
			if(adicionar[i]!=1)
				front.add(frontOriginal[i]);
		}
		
		//percorre flag[]
			//	adiconar no front fronteiraOriginal[i] se flag[i]!=1

	}
	/*
	 * Método que refina o front verificando a dominância entre
	 * as partículas do front modificado
	 * @param frontOriginal - cópia do front original, uma vez que ele vai ser destruído
	 * @param frontModificado - front modificado utilizando a fórtuma do S-cdas
	 */
	public void refinarScdas(Solucao[] frontOriginal, double[][] frontModificado){
		front.clear();
		for(int p=0;p<frontModificado.length;p++){
			int dominada=0;
			for(int k=0;k<frontModificado.length;k++){
				if(p != k){
					//if((frontModificado[p][0]==0 && frontModificado[p][1]==0) || (frontModificado[k][0]==0 && frontModificado[k][1]==0) || (Double.compare(frontModificado[p][0],Double.NaN)==0 || Double.compare(frontModificado[p][1],Double.NaN)==0) || (Double.compare(frontModificado[k][0],Double.NaN)==0 || Double.compare(frontModificado[k][1],Double.NaN)==0)){
						//if que elimina todos os pontos [0,0] e todos os pontos com NaN
						//System.out.println("erro!");
					//}else{
					
						//System.out.println("comp: p: "+p+" ["+frontModificado[p][0]+", "+frontModificado[p][1]+"] == k:"+k+" ["+frontModificado[k][0]+", "+frontModificado[k][1]+"]\torig: ["+frontOriginal[k].objetivos[0]+", "+frontOriginal[k].objetivos[1]+"]");
						int comp = compareObjectiveVector(frontModificado[p], frontModificado[k]);
	
						if(comp == -1){ //(x,y) - x dominada por y
							dominada++;
							break;
						}
					//}
				}
			}
			if(dominada == 0){
				front.add(frontOriginal[p]);
			}
		}

	}
	/*
	 * Método que gera o front modificado pela fórmula do S-cdas
	 * @param delta - valor do delta da fórmula do artigo
	 */
	public double[][][] calcularScdas(double delta){
		double [][]referencia = getReferenciasScdas(delta);
		int m=problema.m;
		double[][][] frontModificado=new double[front.size()][front.size()][m];
		double fi[]=new double[m];
		double menores[] = new double[m];
		
		menores = referencia[0].clone();
		
		menores[0] = referencia[1][0];
		
		for(int i = 0; i<referencia.length; i++){
			double[] ref = referencia[i];
			for(int j = 0; j<ref.length; j++)
				ref [j] = ref[j] - menores[j];
		}
		
		
		for (int p = 0; p< front.size(); p++){//percorrendo as particulas
			Solucao particula = front.get(p);
			double[] fix=new double[m];
			for(int i=0;i<m;i++){
				fix[i]=Math.max(particula.objetivos[i]-menores[i], 0);
			}

			double r = r(fix);
			for(int i=0;i<m;i++){//percorrendo os objetivos
				double l = AlgoritmoAprendizado.distanciaEuclidiana(fix,referencia[i]);
				//double temp2= Math.abs(fix[i]-referencia[i][i])/l;
				//fi[i] = Math.acos(temp2);
				double cosWi = fix[i]/r;
				double cosWi2 = cosWi*cosWi;
				double senWi = Math.sqrt(1-cosWi2);
				fi[i] = Math.asin((r*senWi)/l);
				if(Double.compare(fi[i],Double.NaN) ==0)
					//System.out.println("errado "+"---("+r+"*"+senWi+")/"+l+" dif: "+((r*senWi)/l));
					fi[i] = Math.asin(1); //resulta em NaN quando (r*senWi)/l é maior que 1. esta diferenca e ignorada agora
				if(Double.compare(fi[i],0) ==0)
					fi[i] = 2*Math.PI;
			}
			
			for(int y = 0; y<front.size(); y++){
				Solucao solucao_y = front.get(y);
				double[] fiy=new double[m];
				for(int i=0;i<m;i++){
					fiy[i]=Math.max(solucao_y.objetivos[i]-menores[i], 0);
				}

				double r_y = r(fiy);

				for (int i = 0; i < fi.length; i++) {
					double temp = modificacaoCDASValor(fiy[i], r_y, fi[i]);

					frontModificado[p][y][i] = temp;
				}

			}




		}
		return frontModificado;
	}
	/**
	 * Modificao da da dominancia de Pareto proposta por Sato
	 * Derivacao do Sen do Wi atraves do Cos
	 * @param fix Valor original da funcao de objetivo de indice i
	 * @param r Norma do vetor de objetivos
	 * @param si Paremetro da modificacao da dominacia (Varia entre 0.25 e 0.75)
	 * @return
	 */
	public static double modificacaoCDASValor(double fix, double r, double fi){
		double cosWi = fix/r;
		double cosWi2 = cosWi*cosWi;
		
		double senWi = Math.sqrt(1-cosWi2);
		if(cosWi2 == 1)
			senWi = 0;
		double senFi = Math.sin(fi);
		double cosFi = Math.cos(fi);
		
		//Formula: r*sen(Wi+SiPi)/sen(SiPi)
		double numerador = r*((senWi*cosFi)+(cosWi*senFi));
		double novoFix = numerador/senFi;
		
		/*double diff = fix - novoFix;
		
		novoFix = fix + diff;*/
		
		if(Double.compare(novoFix,Double.NaN) == 0)
			System.out.println("FGDFGDFGDSF");
		
		return Math.max(novoFix, 0);
	}
	
	/**
	 * Método que modifica os valores dos objetivos de acordo com a diferença entre cada valor
	 * Visa deslocar os objetivos para o centro do espaco de objetivos
	 * Um valor de objetivo pequeno e muito deslocado se existe um valor grande.
	 * Um valor grande nao sofre deslocamente
	 * Solucoes com valores proximos sao privilegiadas
	 * 
	 * @param fx vetor objetivo
	 * @return vetor objetivo modificado
	 */
	public double[] modificacaoDominanciaParetoEqualizar(double[] fx, double fator){
		if(fator !=0){
			double[] retorno = new double[fx.length];
			double maiorValor = 0;
			double menorValor = Double.MAX_VALUE;
			
			//Procura o maior e o menor valor dos objetivos
			for (int i = 0; i < fx.length; i++) {
				double d = fx[i];
				if(d > maiorValor)
					maiorValor = d;
				if(d< menorValor)
					menorValor = d;
			}

			double diferenca = maiorValor - menorValor;
			//Modifica a cada objetivo de acordo com sua relação a diferenca
			for (int i = 0; i < fx.length; i++) {
				double d = fx[i];
				//Obtem a relacao entre a maior diferenca dos objetivos e o valor do objetivo i
				double relacao = diferenca/d;
				//Calcula a variacao do objetivo. Em funcao do valor do objetivo e a relacao com a diferenca
				//Quanto menor eh o objetivo em relacao a deferenca, maior sera o aumento
				double variacao = (Math.abs(1-relacao)*d)/fator;
				retorno[i] = d+variacao; 
			}

			return retorno;
		}
		else return fx;
	}
	
	/**
	 * Aplica a epsilon-dominance - Minimizacao (fx / (1+epsilon)) - Maximizacao ((1+eps) * fx)  
	 * @param fx
	 * @param epsilon
	 * @return
	 */
	public double[] modificacaoDominanciaParetoEpsilon(double[] fx, double epsilon){
		double[] retorno = new double[fx.length];
	
		for (int i = 0; i < fx.length; i++) {
			double novo_valor = 0;
			if(maxmim[i].equals("-"))
				novo_valor = fx[i]/(1+epsilon);
			else
				novo_valor = fx[i]*(1+epsilon);
			retorno[i] = novo_valor;
		}
		
		return retorno;
	}
	
	public static double r(double[] objetivos){
		double soma = 0;
		for (int i = 0; i < objetivos.length; i++) {
			double fix = objetivos[i];
			soma+=fix*fix;
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * M�todo que retorna de quantas solu��es a solu��o passada como par�metro � dominada
	 * @param solucao Solu��o a ser contada o numero de domina��o		
	 * @return Quantas solu��es a solu��o passada como parametro � dominada
	 */
	public double obterNumDomincao(Solucao solucao, ArrayList<Solucao> solucoes){
		
		int numDominacao = 0;
		
		int comp;
	
		double[] novosObjetivosSolucao =  modificacaoDominanciaParetoCDAS(solucao.objetivos, S);
	
		
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao temp = iter.next();
			
			double[] novosObjetivosTemp = modificacaoDominanciaParetoCDAS(temp.objetivos, S);
			
			comp = compareObjectiveVector(novosObjetivosSolucao, novosObjetivosTemp);
			if(comp == DOMINATED_BY)
				numDominacao++;
		}
		
		return numDominacao;
	}
	
	public double obterNumDomincaoRank(Solucao solucao, ArrayList<Solucao> solucoes){
		
		int numDominacao = 0;
		
		int comp;
	
		
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao temp = iter.next();
			
			comp = compareObjectiveVector(solucao.combRank, temp.combRank);
			if(comp == DOMINATED_BY)
				numDominacao++;
		}
		
		return numDominacao;
	}
	
	/**
	 * Verifica se a solucao tem os mesmos valores de objetivo (+ ou - var) que alguma solucao no repositorio
	 * @param solucao
	 * @param var
	 * @return
	 */
	public boolean contemSolucaoVariacao(Solucao solucao, double var){
		double[] limite_sup = new double[solucao.objetivos.length];
		double[] limite_inf = new double[solucao.objetivos.length];
		
		for (int i = 0; i < solucao.objetivos.length; i++) {
			limite_sup[i] = solucao.objetivos[i] + var; 
			limite_inf[i] =  Math.max(solucao.objetivos[i] - var, 0);;
		}
		
		boolean dentro = false;
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao sol_fronteira = (Solucao) iterator.next();
			for (int i = 0; i < sol_fronteira.objetivos.length && !dentro; i++) {
				double obj = sol_fronteira.objetivos[i];
				if(obj>=limite_inf[i] && obj<=limite_sup[i])
					dentro = true;
				else{
					dentro = false;
					break;
				}					
			}
			
		}
		
		return dentro;
	}
	
	/**
	 * Verifica se a solucao tem os mesmos valores de objetivo (+ ou - var) que alguma solucao no repositorio
	 * @param solucao
	 * @param var
	 * @return
	 */
	public boolean contemSolucaoVariacaoEspacobusca(SolucaoNumerica solucao, double var){
		double[] limite_sup = new double[solucao.getVariaveis().length];
		double[] limite_inf = new double[solucao.getVariaveis().length];
		
		for (int i = 0; i < solucao.getVariaveis().length; i++) {
			limite_sup[i] = solucao.getVariavel(i) + var; 
			limite_inf[i] =  Math.max(solucao.getVariavel(i) - var, 0);;
		}
		
		boolean dentro = false;
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			SolucaoNumerica sol_fronteira = (SolucaoNumerica) iterator.next();
			for (int i = 0; i < sol_fronteira.getVariaveis().length && !dentro; i++) {
				double obj = sol_fronteira.getVariavel(i);
				if(obj>=limite_inf[i] && obj<=limite_sup[i])
					dentro = true;
				else{
					dentro = false;
					break;
				}					
			}
			
		}
		
		return dentro;
	}

}
