package solucao;

import java.util.Random;

public class SolucaoBinaria extends Solucao {

		
	public SolucaoBinaria(int n, int m){
		super(n,m);
		variaveis = new double[n];
	}
	
	@Override
	public void iniciarSolucaoAleatoria() {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for(int i = 0; i<n; i++){
			double temp = rand.nextDouble();
			if(temp >=0.5)
				variaveis[i] = 1;
			else
				variaveis[i] = 0;
		}

	}
	
	public void setVariavel(int i, int valor){	
		if(valor!=0 && valor!=1)
			System.err.println("Valor nao eh binario");
		variaveis[i] = valor;
	}
	
	public double getVariavel(int i){
		return variaveis[i];
	}
	
	public double[] getVariaveis(){
		return variaveis;
	}
	
	public static void main(String[] args) {
		SolucaoBinaria sol = new SolucaoBinaria(10, 3);
		sol.iniciarSolucaoAleatoria();
	}
	
	public boolean  isNumerica(){
		return false;
	}
	
	public boolean  isBinaria(){
		return true;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		//buff.append("\nVariaveis: ");
		for (int i = 0; i < variaveis.length; i++) {
			buff.append(variaveis[i] + "\t");
			//buff.append(variaveis[i] + "\t");
		}
		/*buff.append("\t");
		if(objetivos.length>0){
			for (int i = 0; i < objetivos.length; i++) {
				//buff.append(new Double(variaveis[i]).toString().replace('.', ',') + "\n");
				buff.append(objetivos[i] + "\t");
			}	
		}*/
		
		//if(rank!=-1)
		//	buff.append("\n" + rank + "\t");
		
		//buff.append("\n" + crowdDistance + "\t");
		
		return buff.toString();
	}
	
	public Object clone(){
		SolucaoBinaria novaSolucao = new SolucaoBinaria(n, m);
		for(int i = 0; i<variaveis.length; i++){
			novaSolucao.variaveis[i] = variaveis[i];
		}
		
		
		for(int i = 0; i<m; i++){
			novaSolucao.objetivos[i] = objetivos[i];
		}
		
		
		novaSolucao.rank = rank;
		novaSolucao.crowdDistance = crowdDistance;
		novaSolucao.numDominacao = numDominacao;
				
		
		
		return novaSolucao;
	}
	
	public void mutacaoSimples(double prob_mutacao){
		for (int i = 0; i < variaveis.length; i++) {
			double prob = Math.random();
			if(prob<prob_mutacao){
				if(variaveis[i] == 0)
						variaveis[i] = 1;
				else
					variaveis[i] = 0;
			}
		}	
	}

}
