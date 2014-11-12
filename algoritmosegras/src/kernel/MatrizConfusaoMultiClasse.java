package kernel;

/**
 * Matriz de confusao multiclasses
 * @author Matheus 
 */
public class MatrizConfusaoMultiClasse {
	
	public double[][] matriz;
	public int tamanho;
	
	public MatrizConfusaoMultiClasse(int tamanho){
		this.tamanho = tamanho;
		this.matriz = new double[tamanho][tamanho];
	}
	
	public double getAccuracy(){
		double totalAcertos = 0;
		double totalGeral = 0;
		for (int i = 0; i < this.tamanho; i++) {
			for (int j = 0; j < this.tamanho; j++) {
				totalGeral += matriz[i][j];
			}
		}
		for (int i = 0; i < this.tamanho; i++) {
			totalAcertos += matriz[i][i];
		}
		return totalAcertos/totalGeral;
	}
	
	public double getRecall(int classIndex){
		double tp =  matriz[classIndex][classIndex];
		double totalColuna = 0;
		for (int i = 0; i < this.tamanho; i++) {
			totalColuna += matriz[classIndex][i];
		}
		return tp / totalColuna;
	}	
	
	public double getPrecision(int classIndex){
		double tp =  matriz[classIndex][classIndex];
		double totalLinha = 0;
		for (int i = 0; i < this.tamanho; i++) {
			totalLinha += matriz[i][classIndex];
		}
		if(totalLinha == 0){
			return 0;
		} else {
			return tp / totalLinha;
		}
	}
	
	public double getFmeasure(int classIndex){
		double prec = getPrecision(classIndex);
		double rec = getRecall(classIndex);
		double denomidador = (prec+rec);
		double fm = 0;
		if(denomidador!=0)
			fm = (this.tamanho * prec * rec)/denomidador;
		return fm;		
	}
}
