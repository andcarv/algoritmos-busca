package kernel;

import weka.core.Instance;

public class InstanceVotacao implements Comparable<InstanceVotacao>{
	
	public Instance exemplo;
	public double votacao;
	public int codigo;
	
	public InstanceVotacao(Instance i, double v){
		exemplo = i;
		votacao = v;
	}
	
	public String toString(){
		return ""+votacao; 
	}
	
	public int compareTo(InstanceVotacao temp){
		if(votacao<temp.votacao)
			return -1;
		else{
			if(votacao>temp.votacao)
				return 1;
			else return 0;
		} 
			
	}

}
