package kernel;

public class MatrizConfusao {
	
	public double tp;
	public double tn;
	public double fp;
	public double fn;
	
	double precisaop;
	double precisaon;
	double precisaot;
	
	public MatrizConfusao(){
		tp = 0;
		tn = 0;
		fp = 0;
		fn = 0;
		
		precisaop = -1;
		precisaon = -1;
		precisaot = -1;
	}
	
	public double getPrecisaoPositiva(){
		double temp = tp+fn;
		if(temp == 0)
			return 0;
		else{
			precisaop = tp/(temp);
			return precisaop;
		}
	}
	
	public double getPrecisaoNegativa(){
		double temp = fp+tn;
		if(temp == 0)
			return 0;
		else{
			precisaon = tn/(temp);
			return precisaon;
		}
	}
	
	public double getPrecisaoTotal(){
		int numElementos = (int)(tp+tn+fp+fn);
		precisaot = (tp+tn)/(numElementos);
		return precisaot;
	}
	
	//Medidas do artigo elish2008
	
	public double getAccuracy(){
		int numElementos = (int)(tp+tn+fp+fn);
		double acc = (tp+tn)/(numElementos);
		return acc;
	}
	
	public double getPrecision(int classIndex){
		//Classe positiva
		if(classIndex == 0){
			double denomidador = (tp+fp);
			double prec = 0;
			if(denomidador!=0)
				prec = (tp)/denomidador;
			return prec;
		}
		else{
			double denomidador = (tn+fn);
			double prec = 0;
			if(denomidador!=0)
				prec = (tn)/denomidador;
			return prec;
		}
	}
	
	public double getRecall(int classIndex){
//		Classe positiva
		if(classIndex == 0){
			double denomidador = (tp+fn);
			double rec = 0;
			if(denomidador!=0)
				rec = (tp)/denomidador;
			return rec;
		} else{
			double denomidador = (tn+fp);
			double rec = 0;
			if(denomidador!=0)
				rec = (tn)/denomidador;
			return rec;
		}
	}
	
	public double getFmeasure(int classIndex){
		double prec = getPrecision(classIndex);
		double rec = getRecall(classIndex);
		double denomidador = (prec+rec);
		double fm = 0;
		if(denomidador!=0)
			fm = (2*prec*rec)/denomidador;
		return fm;
		
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		//str.append("\tTP:\tFN\n");
		str.append(new Double(tp).toString().replace('.', ',') +"\t" + new Double(fn).toString().replace('.', ',') + "\n");
		//str.append("\tFP\tTN\n");
		str.append(new Double(fp).toString().replace('.', ',')+"\t"+ new Double(tn).toString().replace('.', ',')+"\n");
		
		return str.toString();
		
	}
	
	

}
