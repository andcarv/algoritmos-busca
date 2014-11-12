package regra;

import java.util.Comparator;

public class ComparatorX implements Comparator<Regra>{
	
	public int compare(Regra r1, Regra r2){

		if(r1.getConfidence()<r2.getConfidence()){
			return -1;
		} else{
			if(r1.getConfidence()>r2.getConfidence())
				return 1;
			else{
				if(r1.getSup()<r2.getSup())
					return -1;
				else{
					if(r1.getSup()>r2.getSup())
						return 1;
					else{
						if(r1.getNumAtributosNaoVazios()<r2.getNumAtributosNaoVazios())
							return -1;
						else{
							if(r1.getNumAtributosNaoVazios()> r2.getNumAtributosNaoVazios())
								return 1;
							else
								return 0;
						}
					}
				}
				
			}
		}
		
	}

}
