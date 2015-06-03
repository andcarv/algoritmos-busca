package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

public class ObterObjetivosFold {
	
	public int numExec;
	public int numFolds;
	public int repeticao = 1;
	
	public TreeSet<ObjetivosRegras> fronteira;
	
	public ArrayList<ObjetivosRegras> obterObjetivos(String nomeArquivo, String c) throws Exception{
		Reader reader = new FileReader(nomeArquivo);
		BufferedReader buff = new BufferedReader(reader);
		ArrayList<ObjetivosRegras> retorno = new ArrayList<ObjetivosRegras>();
		
		while(buff.ready()){
			String linha = buff.readLine();
			String[] dados = linha.split("\t");
			
			int i = 0;
			for (i = 0; i < dados.length; i++) {
				String s = dados[i];
				if(s.equals(""))
					break;
			}
			String classe = dados[i-3];
			if(classe.indexOf(c)!=-1){
				double sens = new Double(dados[i-2].replace(',','.')).doubleValue();
				double spec = new Double(dados[i-1].replace(',','.')).doubleValue();
				ObjetivosRegras obj = new ObjetivosRegras(sens,spec);
				retorno.add(obj);
			}
		}
		
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public void lerArquivos(String nomeBase, String caminhoBase, String algoritmo, String classe) throws Exception{
		System.out.println("Base: " + nomeBase);
		System.out.println("Classe: "+ classe );
		System.out.println("Algoritmo: " + algoritmo);
		fronteira = new TreeSet<ObjetivosRegras>();
		String nomeArquivoSaida2 = caminhoBase + nomeBase + "/nomdom_" +classe +"_" + nomeBase + ".txt";
		PrintStream ps2 = new PrintStream(nomeArquivoSaida2);
		for(int j = 0; j<numFolds; j++){
			System.out.print("Fold: " + j + "|");
			String nomeArquivoSaida = caminhoBase + nomeBase + "/"+classe+"_" + nomeBase + "_" + algoritmo+  "."+j;
			PrintStream ps = new PrintStream(nomeArquivoSaida);
			
			for(int i = 0 ; i<numExec; i++){
				System.out.print(i + " ");
				String nomeArquivo = caminhoBase + nomeBase + "/"+ nomeBase + i + 
				"/regras_" + nomeBase + i + "_" + j+ ".txt";	
				ArrayList<ObjetivosRegras> objetivos = obterObjetivos(nomeArquivo, classe);
				Collections.sort(objetivos);
				for(int z = 0; z<repeticao ;z++){
					for (Iterator iter = objetivos.iterator(); iter.hasNext();) {
						ObjetivosRegras element = (ObjetivosRegras) iter.next();
						add(element);
						ps.println(element);
					}
					ps.println();
				}
				ps.println();
				System.out.println();
			}
			ps.flush();
			ps.close();
		}
		
		for (Iterator iter = fronteira.iterator(); iter.hasNext();) {
			ObjetivosRegras element = (ObjetivosRegras) iter.next();
			ps2.println(element.toString().replace('.',','));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void lerArquivosSemFold(String nomeBase, String caminhoBase, String classe) throws Exception{
		
		fronteira = new TreeSet<ObjetivosRegras>();
		String nomeArquivoSaida2 = caminhoBase + nomeBase + "/nomdom_" + nomeBase + ".txt";
		PrintStream ps2 = new PrintStream(nomeArquivoSaida2);
		String nomeArquivoSaida = caminhoBase + nomeBase + "/fronteira_" + nomeBase +  "_" + classe + ".txt";
		PrintStream ps = new PrintStream(nomeArquivoSaida);
		
		for(int i = 0 ; i<numExec; i++){
			String nomeArquivo = caminhoBase + nomeBase + "/"+ nomeBase + i + 
			"/regras_" + nomeBase + i + ".txt";	
			ArrayList<ObjetivosRegras> objetivos = obterObjetivos(nomeArquivo, classe);
			Collections.sort(objetivos);
			
			
			for (Iterator iter = objetivos.iterator(); iter.hasNext();) {
				ObjetivosRegras element = (ObjetivosRegras) iter.next();
				add(element);
				ps.println(element);
			}
			ps.println();
			
		}
		ps.flush();
		ps.close();

		
		for (Iterator iter = fronteira.iterator(); iter.hasNext();) {
			ObjetivosRegras element = (ObjetivosRegras) iter.next();
			ps2.println(element.toString().replace('.',','));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean add(ObjetivosRegras obj){
		int comp;
		if(fronteira.size()==0){
			fronteira.add(obj);
			return true;
		}
		TreeSet cloneFronteira = (TreeSet)fronteira.clone();
		
		for (Iterator iter = cloneFronteira.iterator(); iter.hasNext();) {
			ObjetivosRegras temp = (ObjetivosRegras) iter.next();
			comp = compararMedidas(obj.getValoresObjetivos(), temp.getValoresObjetivos());
			if(comp == -1)
				return false;
			if(comp == 1){
				fronteira.remove(temp);
			}
		}
		
		fronteira.add(obj);
		return true;

	}
	

	
	public int compararMedidas(double[] regra1, double[] regra2){
		//Contador que marca quantos valores da regra 1 sao maiores que os da regra2
		//Se cont for igual ao tamanho dos elementos da regra 1 entao a regra 2 eh dominada pela regra1
		//Se cont for igual a 0 a regra2 domina a regra1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		int cont = 0; 
		int cont2 = regra1.length;
		for (int i = 0; i < regra1.length; i++) {
			if(regra1[i]>regra2[i]){
				++cont;
			} else {
				if(regra1[i]==regra2[i]){
					--cont2;
				}
			}
				
			
		}
		
		if(cont == 0){
			
			if(cont2 == 0)
				return 0;
			else
				return -1;
		}
		else{
			if(cont>0 && cont<cont2)
				return 0;
			else return 1;
		}
	}

	
	 
	
	public static void main(String[] args) {
		String nomeBase = "vehicle";
		String caminhoBase = "C:\\Andre\\docencia\\experimentos\\";
		String algoritmo = "apriori";
		ObterObjetivosFold o = new ObterObjetivosFold();
		o.numExec = 10;
		o.numFolds = 10;
		o.repeticao = 10;
		//o.classe = "negative";
		
		try{
			String classe = "positive";
			o.lerArquivos(nomeBase, caminhoBase, algoritmo, classe);
			classe = "negative";
			o.lerArquivos(nomeBase, caminhoBase, algoritmo, classe);
			//o.lerArquivosSemFold(nomeBase, caminhoBase);
		} catch(Exception ex){ex.printStackTrace();}
	}
	
	private class ObjetivosRegras implements Comparable<ObjetivosRegras>{
		double sens;
		double spec;
		
		public ObjetivosRegras(double se, double sp){
			sens = se;
			spec = sp;
		}
		
		public int compareTo(ObjetivosRegras or){
			if(sens < or.sens)
				return -1;
			else
				if(sens>or.sens)
					return 1;
				else
					return 0;
		}
		
		public String toString(){
			return sens + "\t" + spec;
		}
		
		public double[] getValoresObjetivos(){
			double[] retorno = new double[2];
			retorno[0] = sens;
			retorno[1] = spec;
			return retorno;
		}
		
				
		
		
	}


}



