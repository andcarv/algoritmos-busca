package objetivo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import solucao.Solucao;

public abstract class Criterios extends FuncaoObjetivo {

	public int[][] dados = null;
	
	public Criterios(String arquivoCriterios){
		try{
			BufferedReader buff = new BufferedReader(new FileReader(arquivoCriterios));
			int[] dimensoes = obterDimensoesMatriz(buff);
			buff = new BufferedReader(new FileReader(arquivoCriterios));
			dados = new int[dimensoes[0]][dimensoes[1]];
			preencherMatrizDados(buff);
			
		}catch(IOException ex){ex.printStackTrace();}
		
	}
	
	@Override
	public abstract double calcularObjetivo(Solucao solucao);


	
	public void preencherMatrizDados(BufferedReader buff) throws IOException{
		int i = 0;
		while(buff.ready()){
			String linha = buff.readLine().trim();
			if(!linha.isEmpty()){
				String[] linhaArray = linha.split("\t");
				for (int j = 0; j < linhaArray.length; j++) {
					dados[i][j] = new Integer(linhaArray[j]);
				}

				i++;
			}
		}
	}
	
	public int[] obterDimensoesMatriz(BufferedReader buff) throws IOException{
		String linha1 = buff.readLine().trim();
		int j = linha1.split("\t").length;
		int i = 1;
		while(buff.ready()){
			String linha = buff.readLine();
			if(!linha.isEmpty())
				i++;
		}
		
		int[] retorno = new int[2];
		retorno[0] = i;
		retorno[1] = j;
		buff.close();
		return retorno;
	}
	
	
	public void OR(int[] vetor1, int[] vetor2, int[] vetorResultado){
		
		for (int i = 0; i < vetor1.length; i++) {
			int val1 = vetor1[i];
			int val2 = vetor2[i];
			int sum = val1 + val2;
			if(sum == 0)
				vetorResultado[i] = 0;
			else
				vetorResultado[i] = 1;
			
		}
		
	}
	
	


}
