package regra;

public class MatrizContingencia {
	
	private double[] matrizContigencia = null;
	private double numElementos = -1;
	
	
	public MatrizContingencia(){
		matrizContigencia = new double[8];
		for (int i = 0; i < matrizContigencia.length; i++) {
			matrizContigencia[i] = 0;	
		}
	}
	
	public void limparMatrizContingencia(){
		if(matrizContigencia != null){
			for(int i = 0; i<matrizContigencia.length; i++)
				matrizContigencia[i] = 0;
			numElementos = -1;
		}
	}
	
	public double getNumElementos(){
		if(numElementos == -1)
			numElementos =  matrizContigencia[0] + matrizContigencia[1] + matrizContigencia[2] + 
		matrizContigencia[3];
		
		return numElementos;
	}
	
	public double getB(){
		return matrizContigencia[4];
	}
	
	public double getNotB(){
		return matrizContigencia[5];
	}
	
	public double getH(){
		return matrizContigencia[6];
	}
	
	public double getNotH(){
		return matrizContigencia[7];
	}
	
	public double getH_B(){
		return matrizContigencia[0];	
	}
	
	public void incH_B(){
		matrizContigencia[0]++;
		//b
		matrizContigencia[4]++;
		//h
		matrizContigencia[6]++;
	}
	
	public double getNotH_B(){
		return matrizContigencia[1];	
	}
	
	public void incNotH_B(){
		matrizContigencia[1]++;
		//b
		matrizContigencia[4]++;
		//not h
		matrizContigencia[7]++;
	}
	
	public double getH_NotB(){
		return matrizContigencia[2];	
	}
	
		
	public void incH_NotB(){
		matrizContigencia[2]++;
		//not b
		matrizContigencia[5]++;
		//h
		matrizContigencia[6]++;
	}
	
	public double getNotH_NotB(){
		return matrizContigencia[3];	
	}
	
		
	public void incNotH_NotB(){
		matrizContigencia[3]++;
		//not b
		matrizContigencia[5]++;
		//not h
		matrizContigencia[7]++;
	}
	
	public boolean equals(MatrizContingencia m){
		double hb1 = matrizContigencia[0];
		double hLb1 = matrizContigencia[1];
		double hbL1 = matrizContigencia[2];
		double hLbL1 = matrizContigencia[3];
		
		double hb2 = m.matrizContigencia[0];
		double hLb2 = m.matrizContigencia[1];
		double hbL2 = m.matrizContigencia[2];
		double hLbL2 = m.matrizContigencia[3];
		
		if(hb1 == hb2 && hLb1 == hLb2 && hbL1 == hbL2 && hLbL1 == hLbL2)
			return true;
		else return false;

	}
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append("\tH\t    H'\n");
		str.append("B\t" + matrizContigencia[0] + "   \t" + matrizContigencia[1] + "   \t"  + matrizContigencia[4] + "\n");
		str.append("B'\t" + matrizContigencia[2] +"   \t" + matrizContigencia[3] +"   \t"  + matrizContigencia[5] +"\n");
		str.append( "\t" + matrizContigencia[6] + "   \t" + matrizContigencia[7] + "   \t"  + getNumElementos());
		
		return str.toString();
	}
	
	public Object clone(){
		MatrizContingencia cont = new MatrizContingencia();
		for (int i = 0; i < cont.matrizContigencia.length; i++) {
			cont.matrizContigencia[i] = matrizContigencia[i];
		}
		return cont;
		
	}
}
