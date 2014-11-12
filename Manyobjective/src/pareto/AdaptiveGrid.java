package pareto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa um grid adaptativo proposto por Knowles e Corne
 * @author Andre
 *
 */
public class AdaptiveGrid {
	
	//Array de numero reais que representa os pontos do grid
	public double[][] grid = null;
	//Tabela hash que cont�m as solucoes de cada c�lula do grid
	public HashMap<Integer, ArrayList<Solucao>> solucoes = null;
	//Tabela hash que cont�m qtas solucoes existem em cada celula
	public HashMap<Integer, Integer> lotacao = null;
	//N�mero de intervalos do grid passado como parametro
	public int numIntervalos;
	//Array que contem os maiores valores encontrados ate entao para cada dimensao do grid
	public double[] maxValGrid = null;
	//Populacao maxima do grid
	public int MAX_POP;
	
	
	/**
	 * Construtor da classe. 
	 * @param m N�mero de objetivos do problema
	 * @param p N�mero de parti��es do grid
	 */
	public AdaptiveGrid(int m, int p, int tamanho){
		maxValGrid = new double[m];
		grid = new double[m][p+1];
		
		numIntervalos = p;
		
		solucoes = new HashMap<Integer, ArrayList<Solucao>>();
		lotacao = new HashMap<Integer, Integer>();
		
		MAX_POP = tamanho;
	}
	
	/**
	 * Metodo que adapta o grid aos novos objetivos que serao inseridos
	 * Reinsere os elementos no grid de acordo com as novas celulas
	 * @param objetivos
	 */
	public void construirGrid(double[] objetivos){
		boolean modificaoGrid = false;
		//Verifica se os valores do objetivos excedem os limites do grid
		for (int i = 0; i < objetivos.length; i++) {
			double objetivo = objetivos[i];
			if(objetivo>maxValGrid[i]){
				modificaoGrid = true;
				maxValGrid[i] = objetivo;
			}
		}
		//Caso sim, calcula as novas c�lulas do grid
		if(modificaoGrid){
			for(int i = 0; i<objetivos.length; i++){
				double intervalo = maxValGrid[i]/numIntervalos;
				grid[i][0] = 0;
				for(int j = 1; j<=numIntervalos; j++)
					grid[i][j] = grid[i][j-1] + intervalo;		 
			}
			//Reinsere os elementos no novo grid
			HashMap<Integer, ArrayList<Solucao>> clone = new HashMap<Integer, ArrayList<Solucao>>();
			clone.putAll(solucoes);
			solucoes = new HashMap<Integer, ArrayList<Solucao>>();
			lotacao = new HashMap<Integer, Integer>();
			for (Iterator<ArrayList<Solucao>> iterator = clone.values().iterator(); iterator.hasNext();) {
				ArrayList<Solucao> sols = iterator.next();
				for (Iterator<Solucao> iterator2 = sols.iterator(); iterator2.hasNext();) {
					SolucaoNumerica solucao = (SolucaoNumerica) iterator2.next();
					//add(solucao);
					Integer cel = new Integer(obterCelula(solucao.objetivos));
					ArrayList<Solucao> solKey = solucoes.get(cel);
					if(solKey == null){
						solKey = new ArrayList<Solucao>();
					}
					solKey.add(solucao);
					solucoes.put(cel, solKey);
					
					Integer contCelula = lotacao.get(cel);
					if(contCelula == null)
						contCelula = new Integer(0);
					contCelula++;
					lotacao.put(cel, contCelula);
				}
			}
			/*fo//r(int i = 0; i<objetivos.length; i++){
				System.out.print(i + ": ");
				for(int j = 0; j<=numIntervalos; j++)
					System.out.print(grid[i][j] + " ");
				System.out.println();

			}*/
		}
	}
	
	/**
	 * Obt�m qual c�lula a solu��o ser� inserida. A c�lula � definda por um ponto no grid.
	 * @param objetivos Valores dos objetivos da solu��o
	 * @return O �ndice da c�lula 
	 */
	public int obterCelula(double[] objetivos){
		int indices[] = new int[objetivos.length];
		//Obt�m qual ponto do grid os objetivos est�o associados
		//Representado pelo indice do ponto na matriz grid
		for (int i = 0; i < objetivos.length; i++) {
			double objetivo = objetivos[i];
			int indice = 0;
			for (int j = 1; j < grid[i].length; j++) {
				double ponto = grid[i][j];
				if(objetivo<ponto)
					break;
				else
					indice++;
			}
			indices[i] = indice;
		}
		//Obt�m um identificador �nica para cada c�lula de acordo com o ponto do grid	
		int quad = indices[0];
		for (int k = 1; k < indices.length; k++) {
			quad +=   indices[k]*(Math.pow(numIntervalos, k)); 
		}
		//System.out.println(quad);
		
		return quad;
	}
	
	/**
	 * Metodo que adiciona um elemento no grid
	 * @param solucao Solucao a ser adicionada
	 * @return True caso sim, false caso nao
	 */
	public boolean add(SolucaoNumerica solucao){
		construirGrid(solucao.objetivos);
		Integer cel = new Integer(obterCelula(solucao.objetivos));
		ArrayList<Solucao> solKey = solucoes.get(cel);
		if(solKey == null){
			solKey = new ArrayList<Solucao>();
		}
		//Se a solucao ja existe no hash nao a adiciona
		if(solKey.contains(solucao))
			return false;
		
		
		//Se o grid estiver cheio retira um elemento da celula mais cheia
		if(isFull()){
			Integer mostCrowded = obterMostCrowded();
			//Se a solucao pertence a calula mais cheia nao a adiciona
			if(cel.equals(mostCrowded))
			   return false;
			//Retira uma solucao da celula mais cheia aleatoriamente
			ArrayList<Solucao> celulaCrowded = solucoes.get(mostCrowded);
			int elementoEliminado = (int)((Math.random() * MAX_POP) % celulaCrowded.size());
			celulaCrowded.remove(elementoEliminado);
			solucoes.put(mostCrowded, celulaCrowded);
			
			Integer contCelula = lotacao.get(mostCrowded)-1;
			lotacao.put(mostCrowded, contCelula);
				
		}
		
		
		//Adiciona a solucao no grid
		solKey.add(solucao);
		solucoes.put(cel, solKey);
		
		Integer contCelula = lotacao.get(cel);
		if(contCelula == null)
			contCelula = new Integer(0);
		contCelula++;
		lotacao.put(cel, contCelula);
		
		return true;
	}
	
	/**
	 * Metodo que obtem qual celula esta mais cheia no grid
	 * @return
	 */
	public Integer obterMostCrowded(){
		int mostCrowded = 0;
		int crowdedCel = -1;
		for (Iterator<Integer> iterator = lotacao.keySet().iterator(); iterator.hasNext();) {
			Integer celula = (Integer) iterator.next();
			Integer celCrowded = lotacao.get(celula);
			if(celCrowded>mostCrowded){
				mostCrowded = celCrowded.intValue();
				crowdedCel = celula.intValue(); 
			}
				
			
		}
		return new Integer(crowdedCel);
	}
	
	public int obterLotacao(Integer celula, SolucaoNumerica solucao){
		Integer lot = lotacao.get(celula);
		return lot.intValue();
	}
	
	
	/**
	 * Retorna a media de ocupacao de cada celula do grid
	 * @return Media
	 */
	public double obterMediaOcupacao(){
		int soma = 0;
		Set<Integer> lot = lotacao.keySet();
		for (Iterator<Integer> iterator = lot.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			soma+= integer;
		}
		return soma/lot.size();
	}
	
	/**
	 * Metodo que retorna todas as solucoes dso grid em uma so lista	
	 * @return Lista com todas as solucoees do grid
	 */
	public ArrayList<Solucao> getAll(){
		ArrayList<Solucao> retorno = new ArrayList<Solucao>();
		for (Iterator<ArrayList<Solucao>> iterator = solucoes.values().iterator(); iterator.hasNext();) {
			ArrayList<Solucao> sols = iterator.next();
			retorno.addAll(sols);
		}
		return retorno;
	}
	
	
	public boolean isFull(){
		if(size()>=MAX_POP)
			return true;
		else 
			return false;
	}
	
	public int size(){
		int soma = 0;
		for (Iterator<Integer> iterator = lotacao.keySet().iterator(); iterator.hasNext();) {
			Integer celula = (Integer) iterator.next();
			soma += lotacao.get(celula);
		}
				
		return soma;
	}
	
	public int contains(Object o){
		SolucaoNumerica solucao = (SolucaoNumerica) o;
		for (Iterator<Integer> iterator = lotacao.keySet().iterator(); iterator.hasNext();) {
			Integer celula = (Integer) iterator.next();
			ArrayList<Solucao> solKey = solucoes.get(celula);
			if(solKey.contains(solucao)){
				return celula;
			}
		}
		return -1;
		
	}
	
	public static void main(String[] args) {
		
			
	}

}
