package indicadores;

public class PontoFronteira{
	
	public double[] objetivos = null;
	
	public double soma;
	public int objetivosAcimaMedia;
	
	//Used in some sorting methods
	public double rank;
	
	public PontoFronteira(double[] o){
		objetivos = new double[o.length];
		objetivosAcimaMedia = 0;
		soma =  0;
		for (int i = 0; i < objetivos.length; i++) {
			objetivos[i] = o[i];
			soma += objetivos[i];
		}
	}
	
	public boolean equals(PontoFronteira pf){
		for (int i = 0; i < objetivos.length; i++) {
			if(objetivos[i] != pf.objetivos[i])
				return false;
		}
		return true;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < objetivos.length; i++) {
			buff.append(objetivos[i] + "\t");			
		}
		buff.append("\n");
		return buff.toString();
	}
	
}	