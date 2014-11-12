package objetivo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import solucao.Solucao;
import solucao.SolucaoBinaria;

public class Tempo extends FuncaoObjetivo {

	
	public long[] tempo = null;
	
	public Tempo(String arquivoTempo, int numCasosTeste){
		tempo = new long[numCasosTeste];
		try{
			BufferedReader buff = new BufferedReader(new FileReader(arquivoTempo));
			int k = 0;
			while(buff.ready()){
				String linha = buff.readLine().trim();
				if(!linha.isEmpty()){
					tempo[k++] = new Long(linha);
					
				}
			}
		buff.close();
		} catch (IOException ex){ex.printStackTrace();}
	}
	
	@Override
	public double calcularObjetivo(Solucao solucao) {
		SolucaoBinaria sol = (SolucaoBinaria) solucao;
		String[] stringBinaria = sol.getVariaveis();
		
		long tempoTotal = 0;
		for (int i = 0; i < stringBinaria.length; i++) {
			if(stringBinaria[i].equals("1")){
				tempoTotal+=tempo[i];
			}
		}
		return tempoTotal;
	}

}
