package archive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import problema.Problema;

import kernel.AlgoritmoAprendizado;

import solucao.ComparetorDistancia;
import solucao.ComparetorObjetivo;
import solucao.Solucao;

public class DistributedArchiver extends PreciseArchiver {

	
	
	private Problema problem;
	private int archiveSize;
	
	public DistributedArchiver(Problema prob, int as) {
		problem = prob;
		archiveSize = as;
		ID = "dist";
	}
	
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {


		Solucao ideal = AlgoritmoAprendizado.obterSolucoesExtremasIdeais(front
				, true, problem).get(problem.m).get(0);

		front.add(new_solution);
		ArrayList<Solucao> solucoes = front;
		//Se o numero de solucoes eh maior que o tamanho definido para o repositorio

		//Calcula a proporcao de solucoes selecionadas para cada extremo e para o ideal
		double proporcao = 1.0/(problem.m+1);
		int num_sol = (int)(archiveSize*proporcao);

		ArrayList<Solucao> selecionadas = new ArrayList<Solucao>();

		//Percorre todos os objetivo obtende as solucoes com menores valores (nos extremos)
		for(int i = 0; i< problem.m; i++){
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
		int tamanho = archiveSize - selecionadas.size();
		while(contador<tamanho){
			Solucao solucao = solucoes.get(j++);
			if(!selecionadas.contains(solucao)){
				selecionadas.add(solucao);
				contador++;
			}
		}
		
		front.clear();
		
		front.addAll(selecionadas);
	}
}
