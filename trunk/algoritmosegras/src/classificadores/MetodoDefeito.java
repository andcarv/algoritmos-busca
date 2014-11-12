package classificadores;

public class MetodoDefeito implements Comparable {
	
	public int indice;
	public double dist;
	
	public MetodoDefeito(int i, double d){
		indice = i;
		dist = d;
	}
	
	public int compareTo(Object o){
		MetodoDefeito m = (MetodoDefeito) o;
		if(dist<m.dist)
			return -1;
		else{
			if(dist>m.dist)
				return 1;
			else
				return 0;
		}
			
		
	}
	
	public String toString(){
		return indice + ": " + dist;
	}

}
