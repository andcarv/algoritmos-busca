package kernel;

/**
 * Classe que contém as informações de uma execução de algum algoritmo
 * Possui informações como a AUC, a matriz de confusão e medidas derivadas desta matriz
 * @author andre
 *
 */
public class DadosExecucao {
	
	public String base;
	public int numFold;
	public int numExec;
	
	public int numRegrasTotal = 0;
	public MatrizConfusao confusao = null;
	
	public double auc;
	public double accuracy;
	
	public double fmeasurePos;
	public double precisionPos;
	public double recallPos;
	
	
	
	public double fmeasureNeg;
	public double precisionNeg;
	public double recallNeg;
	
	
	
	
	public DadosExecucao(String b, int nf, int ne, double auc,  int rt, MatrizConfusao con){
		base = b;
		numFold = nf;
		numExec = ne;
		this.auc = auc;
		numRegrasTotal = rt;
		confusao = con;
		
		fmeasurePos = confusao.getFmeasure(0);
		precisionPos = confusao.getPrecision(0);
		recallPos = confusao.getRecall(0);
		accuracy = confusao.getAccuracy();
		
		fmeasureNeg = confusao.getFmeasure(1);
		precisionNeg = confusao.getPrecision(1);
		recallNeg = confusao.getRecall(1);
	}
	
	public DadosExecucao(String b, int n){
		base = b;
		numFold = n;
	}
	
	public void setAUC(double auc){
		this.auc = auc;
	}
	
	public void setFmeasure(double fm, int classIndex){
		if(classIndex == 0)
			fmeasurePos = fm;
		else
			fmeasureNeg = fm;
	}
	
	public void setPrecision(double prec, int classIndex){
		if(classIndex == 0)
			precisionPos = prec;
		else
			precisionNeg = prec;
	}
	
	public void setRecall(double rec, int classIndex){
		if(classIndex == 0)
			recallPos = rec;
		else
			recallNeg = rec;
	}
	
	public void setAccuracy(double acc){
		accuracy = acc;
	}
	
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		Double auc = new Double(this.auc);
		
		Double precisaoPos = new Double(this.precisionPos);
		Double recallPos = new Double(this.recallPos);
		Double fmeasurePos = new Double(this.fmeasurePos);
		Double accuracy = new Double(this.accuracy);
		
		Double precisaoNeg = new Double(this.precisionNeg);
		Double recallNeg = new Double(this.recallNeg);
		Double fmeasureNeg = new Double(this.fmeasureNeg);
		
		str.append(auc.toString().replace('.',',') + "\t");
		str.append(accuracy.toString().replace('.', ',')+ "\t");
		
		str.append("\t" + precisaoPos.toString().replace('.', ',')+ "\t");
		str.append(recallPos.toString().replace('.', ',')+ "\t");
		str.append(fmeasurePos.toString().replace('.', ',')+ "\t");
		
		
		str.append("\t" + precisaoNeg.toString().replace('.', ',')+ "\t");
		str.append(recallNeg.toString().replace('.', ',')+ "\t");
		str.append(fmeasureNeg.toString().replace('.', ',')+ "\t");
		
		
		if(numRegrasTotal!=0){
			Double regras = new Double(this.numRegrasTotal);
			str.append("\t" +regras.toString().replace('.', ',')+ "\t");
		}
		return str.toString();
	}
	

}
