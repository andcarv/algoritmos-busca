package archive;

import java.util.ArrayList;

import solucao.Solucao;

public class UnboundArchive extends PreciseArchiver {

	public UnboundArchive() {
		ID = "ub";
	}
	
	
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		front.add(new_solution);
	}

}
