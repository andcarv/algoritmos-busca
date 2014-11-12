package kernel.mopso.lider;

import java.util.ArrayList;

import kernel.mopso.Particula;

import solucao.Solucao;


public abstract class EscolherLider {
	
	public String id;
	
	public abstract void escolherLideres(ArrayList<Particula> populacao, ArrayList<Solucao> lideres);

}
