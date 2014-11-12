package archive;

import java.util.ArrayList;

import solucao.Solucao;

public abstract class PreciseArchiver {
	
	public String ID;
	
	public abstract void filter(ArrayList<Solucao> front, Solucao new_solution);

}
