package seeds;

import java.util.ArrayList;

import problema.Problema;
import solucao.Solucao;
import kernel.mopso.MOPSO;

public abstract class InitializeSwarms {
	
	public MOPSO[] swarms = null;
	
	public Problema problema = null;
	
	public String ID = null; 
	
	public InitializeSwarms(MOPSO[] s, Problema p){
		swarms = new MOPSO[s.length];
		for (int i = 0; i < s.length; i++) {
			swarms[i] = s[i];
		}
		problema = p;
	}
	
	public abstract void initializeSwarms(ArrayList<Solucao> solutions, double box_range);

}
