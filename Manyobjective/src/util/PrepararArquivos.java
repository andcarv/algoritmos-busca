package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;


public class PrepararArquivos {
	
	public static int numObjHiper = 10;
	
	
	public void juntarFronteira(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		
		boolean entrou = false;
		for (int j = 0; j < algoritmos.length; j++) {
			String arqfronteira = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_fronteira.txt";
			
			PrintStream psFronteira = new PrintStream(arqfronteira);
			
			for (int i = 0; i < exec; i++) {
				String arqfronteiraTemp = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
				algoritmos[j] + "/" + i + "/" + metodo + "" + problema + "_" + objetivo + "_fronteira.txt";
				//System.out.println(arqfronteiraTemp);
				BufferedReader buff = new BufferedReader(new FileReader(arqfronteiraTemp));
				while(buff.ready()){
					String linha = buff.readLine().replace("\t", " ");
					if(!linha.isEmpty()){
						psFronteira.println(linha);
						entrou = true;
					}
				}
				
				psFronteira.println();
				if(entrou){
					System.out.println(i);
				}
				
				entrou = false;
			}
		}
	}
	
	/**
	 * M�todo que prepara um arquivo contendo um fronteira de pareto para ser executado no PISA
	 * @param dir
	 * @param problema
	 * @param objetivo
	 * @param algoritmos
	 * @param exec
	 * @param metodo
	 * @throws IOException
	 */
	public void inverterMaxMim(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		for (int j = 0; j < algoritmos.length; j++) {
			
			
			String arqfronteira = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_fronteira.txt";
			
			System.out.println(arqfronteira);
			
			/*BufferedReader buff = new BufferedReader(new FileReader(arqfronteira));
			double maior = 0;
			while(buff.ready()){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					String[] valores = linha.split(" ");
					for (int i = 0; i < valores.length; i++) {
						maior = Math.max(maior, new Double(valores[i].replace(',', '.')).doubleValue());
					}
				}
			}*/
			
			BufferedReader buff = new BufferedReader(new FileReader(arqfronteira));
			
			PrintStream ps = new PrintStream("teste_" + algoritmos[j] + ".txt");
			while(buff.ready()){
				String linha = buff.readLine();
				if(linha.isEmpty()){
					ps.println();
				} else {
					String[] valores = linha.split(" ");
					for (int i = 0; i < valores.length; i++) {
						//double novo_valor = Math.max(0.0001, maior - new Double(valores[i].replace(',', '.')).doubleValue());
						Double novo_valor = new Double(valores[i].replace(',', '.')).doubleValue();
						if(novo_valor>4)
							System.out.println(novo_valor);
						if((novo_valor < 0.001) && novo_valor!=0){
							DecimalFormat formatador = new DecimalFormat("0.0000000000000000");  
							
							ps.print(formatador.format(novo_valor) + " ");
						} else
							ps.print(novo_valor + " ");
					}
					ps.println();
				}
			}
		}
	}
	
	public void gerarComando(String dirEntrada, String dirSaida, String problema, int objetivo, String[] algoritmos , int exec, String metodo,String ind ) throws IOException{
		
		Double[][] valores = new Double[algoritmos.length][exec];
		

		BufferedReader buff;


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dirEntrada + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind + ".txt";

		

			System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready() && tam<exec){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					Double val = new Double(linha);
					//try{
					valores[j][tam++] = val;
					//} catch(ArrayIndexOutOfBoundsException x){x.printStackTrace();}
				}
			}
		}  
		
		
		
		for (int i = 0; i < algoritmos.length; i++) {
			StringBuffer comando = new StringBuffer();
			String id = metodo + "_" + problema + "_" + objetivo + "_" + algoritmos[i] + "_" + ind;
			String arqfronteira = dirSaida + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[i] + "/" +  id + "_comando.txt";
			comando.append(id + "<-c(");
			for (int j = 0; j < exec; j++) {
				comando.append(valores[i][j] + ",");
			}
			
			comando.deleteCharAt(comando.length()-1);
			comando.append(")\n");
			
			PrintStream ps = new PrintStream(arqfronteira);
			
			ps.println(comando);
		}
			
	}
	
	public void setReferenceFile(String dirEntrada, String dirSaida, String problema, int objetivos[], String algoritmo, int exec, String metodo, String ind, PrintStream psSaida) throws IOException{
		ArrayList<double[]> reference_points = new ArrayList<double[]>();
		

		BufferedReader buff;


		for (int j = 0; j < objetivos.length; j++) {
			
			int objetivo = objetivos[j];

			String arq = dirEntrada + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmo + "/" + metodo + "_" + problema + "_" + objetivo + "_" + algoritmo + "_" + ind + ".txt";
					

			System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready() && tam<exec){
				String line = buff.readLine();
				if(!line.isEmpty()){
					
					String line_split[] = line.trim().split("\t");
					double reference[] = new double[line_split.length];
							
					for (int i = 0; i < line_split.length; i++) {
						String line_i = line_split[i];
						if(line_i.contains(",")){
							line_i = line_i.replace(',', '.');
						}
						Double val = new Double(line_i);
						reference[i] = val;
					}
					reference_points.add(reference);
				}
			}
		}  
		
		
		
		
		String caminhoDirExec = dirSaida + "medidas/";
		
		

		if(psSaida == null)
			psSaida = new PrintStream(caminhoDirExec + metodo + "_" + problema + "_"+ algoritmo + "_"+ ind + ".txt");
		
		for (Iterator<double[]> iterator = reference_points.iterator(); iterator
				.hasNext();) {
			double[] ds = (double[]) iterator.next();
			for (int i = 0; i < ds.length; i++) {
				psSaida.print(ds[i] + "\t");
			}
			psSaida.println();
			
		}

	}
	
		
	
	public void preparArquivosEval(String dirEntrada, String dirSaida, String problema, String objetivo, String[] algoritmos , int exec, String metodo[], PrintStream psSaida) throws IOException{
		Double[][] valores = new Double[algoritmos.length][1];
		
		String ind = "eval";

		BufferedReader buff;


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dirEntrada + "resultados/" + metodo[j] + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind + ".txt";

			System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready() && tam<exec){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					if(linha.contains(","))
						linha = linha.replace(',', '.');
					Double val = new Double(linha);
					valores[j][0] = val;
					break;
				}
			}
		}  
		
		
		
		
		String caminhoDirExec = dirSaida + "medidas/";
		
		

		if(psSaida == null)
			psSaida = new PrintStream(caminhoDirExec + "medidas/" + metodo[0] + problema + "_"+ ind + "_" + objetivo + "_indicadores.txt");
		

		for (int i = 0; i < algoritmos.length; i++) {
			try{
				psSaida.print(valores[i][0] + "\t");		
			}catch(NullPointerException ex){ex.printStackTrace();}
		}


		psSaida.println();
	}


	
	public void preparArquivosIndicadores(String dirEntrada, String dirSaida, String problema, String objetivo, String[] algoritmos, int exec, String metodo[], String ind, PrintStream psSaida) throws IOException{
		Double[][] valores = new Double[algoritmos.length][exec];
		

		BufferedReader buff;


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dirEntrada + "results/" + metodo[j] + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind + ".txt";
			
			//trucamento dos valores para 8 casas decimais
			/*String arqComando2 = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind + "_comando.txt";
			
			PrintStream psComando2 = new PrintStream(arqComando2);
			
			StringBuffer comando2 = new StringBuffer();
			
			comando2.append(metodo + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind  +"<- c(");*/

		

			System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready() && tam<exec){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					if(linha.contains(","))
						linha = linha.replace(',', '.');
					Double val = new Double(linha);
					//try{
					valores[j][tam++] = val;
					
					/*BigDecimal b = new BigDecimal(val);		 
					val = (b.setScale(8, BigDecimal.ROUND_UP)).doubleValue();
					comando2.append(val + ",");*/
					//} catch(ArrayIndexOutOfBoundsException x){x.printStackTrace();}
				}
			}
			
			/*comando2.deleteCharAt(comando2.length()-1);
			comando2.append(")");
			psComando2.println(comando2);*/
		}  
		
		
		
		
		String caminhoDirExec = dirSaida + "medidas/";
		
		

		if(psSaida == null)
			psSaida = new PrintStream(caminhoDirExec + "medidas/" + metodo[0] + problema + "_"+ ind + "_" + objetivo + "_indicadores.txt");
		




		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
				try{
					psSaida.print(valores[i][j] + "\t");		
				}catch(NullPointerException ex){ex.printStackTrace();}
			}


			psSaida.println();
		}


	}
	
	
	public void executarFriedman(String dirEntrada, String dirSaida, String problema, int objetivo, String[] algoritmos, int exec, String metodo[], String ind, PrintStream psSaida) throws IOException{
		Double[][] valores = new Double[algoritmos.length][exec];
		

		BufferedReader buff;


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dirEntrada + "resultados/" + metodo[j] + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind + ".txt";
			
			//System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready() && tam<exec){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					if(linha.contains(","))
						linha = linha.replace(',', '.');
					Double val = new Double(linha);
					//try{
					valores[j][tam++] = val;
					
					/*BigDecimal b = new BigDecimal(val);		 
					val = (b.setScale(8, BigDecimal.ROUND_UP)).doubleValue();
					comando2.append(val + ",");*/
					//} catch(ArrayIndexOutOfBoundsException x){x.printStackTrace();}
				}
			}	
		}  
		
		double melhorMedia = Double.MAX_VALUE;
		int index = -1;
		
		for (int i = 0; i < valores.length; i++) {
			Double[] medida_alg = valores[i];
			double media = 0;
			for (int j = 0; j < medida_alg.length; j++) {
				media += medida_alg[j];
			}
			media = media/medida_alg.length;
			if(media<= melhorMedia){
				melhorMedia = media;
				index = i;
			}
		}
		
		String arqCSV = dirSaida + "medidas/friedman_" + metodo[0] + problema + objetivo + "_" + ind + ".csv";
		

		//if(psSaida == null)
			//psSaida = new PrintStream(caminhoDirExec + "medidas/" + metodo[0] + problema + "_"+ ind + "_" + objetivo + "_indicadores.txt");
		
		BufferedReader buffCSV = new BufferedReader(new FileReader(arqCSV));
		buffCSV.readLine();
		
		int num = algoritmos.length;
		
		int[] melhores = new int[algoritmos.length];
		melhores[index] = 1;
		
		index++;
		System.out.println("index: " + index);
		
		
		 

		int inicio = 0;
		int current_line = 0;
		for(int i = 0; i<index-1; i++){
			int index_line = (inicio) + (index -2-i);
			inicio = inicio + (num-(i+1));
			while(current_line < index_line){
				buffCSV.readLine();
				current_line++;
			}
			
			String line = buffCSV.readLine();
			//System.out.println(line);
			String[] line_split = line.split(";");
			if(line_split[5].equals("FALSE")){
				String equivalentes = line_split[0].replaceAll("\"", "");
				//System.out.println(line_split[0]);
				String equivalentes_split[] = equivalentes.split("-");
				int index_equivalentes = new Integer(equivalentes_split[0]).intValue()-1;
				melhores[index_equivalentes] = 1;
			}
			current_line++;
		}
		
		while(current_line < inicio){
			buffCSV.readLine();
			current_line++;
		}
		
		int remain = num - index;
		
		
		
		
		for(int i = 0; i< remain; i++){
			String line = buffCSV.readLine();
			//System.out.println(line);
			String[] line_split = line.split(";");
			if(line_split[5].equals("FALSE")){
				String equivalentes = line_split[0].replaceAll("\"", "");
				
				//System.out.println(line_split[0]);
				String equivalentes_split[] = equivalentes.split("-");
				int index_equivalentes = new Integer(equivalentes_split[1]).intValue()-1;
				melhores[index_equivalentes] = 1;
			}
		}
		
		for (int i = 0; i < melhores.length; i++) {
			if(melhores[i] == 1)
				System.out.println( (i+1)+": "+ algoritmos[i]);
		}
		
		System.out.println();

	}
	
	public void preparArquivoTempo(String dirEntrada, String dirSaida, String problema, String objetivo, String[] algoritmos , int exec, String metodo[], PrintStream psSaida) throws IOException{
		Double[][] valores = new Double[algoritmos.length][exec];
		

		BufferedReader buff;


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dirEntrada + "resultados/" + metodo[j] + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_"+ algoritmos[j] + "_texec.txt";

		

			System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready() && tam<exec){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					String[] tempos = linha.split("\t");
					Double tInicio = new Double(tempos[1])/1000; 
					Double tFim = new Double(tempos[2])/1000;
					Double val = new Double(tFim - tInicio);
					//try{
					valores[j][tam++] = val;
					//} catch(ArrayIndexOutOfBoundsException x){x.printStackTrace();}
				}
			}
		}  
		
		


		if(psSaida == null)
			psSaida = new PrintStream(dirSaida + "medidas/" + metodo[0] + problema + "_tempo_" + objetivo + "_indicadores.txt");
		



		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
				try{
					psSaida.print(valores[i][j].toString().replace(".", ",") + "\t");		
				}catch(NullPointerException ex){ex.printStackTrace();}
			}


			psSaida.println();
		}


	}
	

	public void preparArquivosIndicadoresTodos(String dirEntrada, String dirSaida, String problema, String[] algoritmos , int exec, String metodo[], String ind, int[] objs) throws IOException{
		
		String caminhoDirExec = dirSaida + "medidas/";
		
		File diretorio = new File(caminhoDirExec);
		diretorio.mkdirs();
		
		System.out.println(metodo[0] +"_" + problema + "_"+ ind + "_indicadores_all.txt");

		
		PrintStream psSaida = new PrintStream(caminhoDirExec + metodo[0] +"_" + problema + "_"+ ind + "_indicadores_all.txt");
		
		for (int i = 0; i < objs.length; i++) {
			System.out.println(objs[i]);
			
			
			String objetivo = objs[i] + "";
			
			if(ind.equals("tchebycheff") || ind.equals("dist") )
				preparArquivosTcheb(dirEntrada, dirSaida,  problema, objetivo, algoritmos, exec, metodo, ind);
			else{
				if(ind.equals("tempo")){
					preparArquivoTempo(dirEntrada, dirSaida, problema, objetivo, algoritmos, exec, metodo, psSaida);
					psSaida.println("\n\n\n\n\n\n");
				} else{
					if(ind.equals("eval"))
						preparArquivosEval(dirEntrada, dirSaida, problema, objetivo, algoritmos, exec, metodo, psSaida);
					else{
						preparArquivosIndicadores(dirEntrada, dirSaida, problema, objetivo, algoritmos, exec, metodo, ind, psSaida);
						psSaida.println("\n\n\n\n\n\n");
					}

				//preparArquivosComandosFriedman(dir, dir2,  problema, objetivo, algs, exec, metodo, ind);
				}
			}
			
		}
		
	}

	public void preparArquivosTcheb(String dirEntrada, String dirSaida, String problema, String objetivo, String[] algoritmos , int exec, String metodo[], String indicator) throws IOException{

		BufferedReader buff;
		
		PrintStream psSaida = new PrintStream(dirSaida + "medidas/" + metodo[0] + "_" + problema + "_" + indicator +"_" + objetivo + "_indicadores.txt");
		
		ArrayList<ArrayList<String>> todosArquivos = new ArrayList<ArrayList<String>>();
		
		double maiorTamanho = Double.NEGATIVE_INFINITY;
		
		double soma[] = new double[algoritmos.length];
		
		for (int i = 0; i < soma.length; i++) {
			soma[i] = 0;
		}

		for (int j = 0; j < algoritmos.length; j++) {

			//String arq = dirEntrada + "resultados/" + metodo[j] + "/" + problema + "/" + objetivo + "/" +			
			//algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + indicator + ".txt";
			
			String arq = dirEntrada + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + indicator + ".txt";

		

			System.out.println(arq);
			
			ArrayList<String> arquivo = new ArrayList<String>();
			
			arquivo.add(algoritmos[j] + "\t");
			
			buff = new BufferedReader(new FileReader(arq));
			
			
			
			int tam = 0;
			while(buff.ready()){
				String linha = buff.readLine();
				
				linha = linha.substring(0, linha.length());
				if(!linha.isEmpty()){
					arquivo.add(linha);
					tam++;
					String[] valores = linha.split("\t");
					try{
						soma[j] += new Double(valores[1]).doubleValue();
					}
					catch(ArrayIndexOutOfBoundsException x){x.printStackTrace();}
					
				}
			}
			
			todosArquivos.add(arquivo);
			maiorTamanho = Math.max(maiorTamanho, tam);
			
			
		}  
		
		//ArrayList<ArrayList<String>> todosArquivosNorm = normalizarTcheb(todosArquivos, soma);
		
		todosArquivos = normalizarTcheb(todosArquivos, soma);

		for(int l =0; l<maiorTamanho; l++){
			for (Iterator<ArrayList<String>> iterator = todosArquivos.iterator(); iterator.hasNext();) {
				ArrayList<String> arrayList = (ArrayList<String>) iterator.next();
				if(l<arrayList.size()){
					psSaida.print( arrayList.get(l) + "\t");
				} else{
					psSaida.print("\t\t");
				}
				psSaida.print("\t");
			}
			
			psSaida.println();
		}
		
		



		

	}

	private ArrayList<ArrayList<String>> normalizarTcheb(ArrayList<ArrayList<String>> todosArquivos,
			double[] soma) {
		
		
		
		ArrayList<ArrayList<String>> saida = new ArrayList<ArrayList<String>>();
		int k = 0;
		for (Iterator<ArrayList<String>> iterator = todosArquivos.iterator(); iterator.hasNext();) {
			ArrayList<String> arrayList = (ArrayList<String>) iterator.next();
			
			ArrayList<String> novoAlg = new ArrayList<String>();
			
			for (Iterator<String> iterator2 = arrayList.iterator(); iterator2.hasNext();) {
				String linha = (String) iterator2.next();
				String valores[] = linha.split("\t");
				if(valores.length !=1){		
					double valor = new Double(valores[1]).doubleValue();
					double novoValor = valor/soma[k];
					String novaLinha = valores[0] + "\t" + new Double( novoValor).toString().replace('.', ',');
					novoAlg.add(novaLinha);
				} else
					novoAlg.add(linha);
				
			}
			
			saida.add(novoAlg);
			
			k++;
		}
		
		return saida;
	}
	
	public void preparArquivosComandosWilcox(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		

		BufferedReader h, s, g;
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/hyper_" + objetivo + "_comando.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/spread_" + objetivo + "_comando.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/igd_" + objetivo + "_comando.txt");
		
		StringBuffer comandosHyp = new StringBuffer();
		StringBuffer comandosSpread = new StringBuffer();
		StringBuffer comandosigd = new StringBuffer();
		
		String algCompHyp = metodo + problema + "_" + objetivo + "0.5_hipervolume";
		String algCompSpread = metodo + problema + "_" + objetivo + "0.5_spread";
		String algCompigd = metodo + problema + "_" + objetivo + "0.5_igd";
		
		int obj = Integer.parseInt(objetivo);

		for (int j = 0; j < algoritmos.length; j++) {
			String arqHyp = "";
			if(obj<=numObjHiper)
				arqHyp = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume_comando.txt";
			String arqSpread = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread_comando.txt";
			String arqigd = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_igd_comando.txt";
			
			if(!algoritmos[j].equals("0.5")){
				String algHyp = metodo + problema + "_" + objetivo + algoritmos[j] + "_hipervolume";
				String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
				String algigd = metodo + problema + "_" + objetivo + algoritmos[j] + "_igd";
				if(obj<=numObjHiper)
					comandosHyp.append("wilcox.test(" + algHyp + "," + algCompHyp + ")\n");
				comandosSpread.append("wilcox.test(" + algSpread + "," + algCompSpread + ")\n");
				comandosigd.append("wilcox.test(" + algigd + "," + algCompigd + ")\n");
			}
			
			
			if(obj<=numObjHiper)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = new BufferedReader(new FileReader(arqSpread));
			s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqigd));
			while(s.ready()){
				if(obj<=numObjHiper)
					hypSaida.println(h.readLine());
				spreSaida.println(s.readLine());
				igdSaida.println(g.readLine());
			}
			
		}  
		
		if(obj<=numObjHiper)
			hypSaida.println();
		spreSaida.println();
		igdSaida.println();
		
		if(obj<=numObjHiper)
			hypSaida.println(comandosHyp);
		spreSaida.println(comandosSpread);
		igdSaida.println(comandosigd);

			
	}
	
	public String preparArquivosComandosFriedman(String dir, String dir2, String problema, String objetivo, String[] algoritmos , int exec, String metodo[], String ind) throws IOException{
		

		BufferedReader buff;
		
		String id = metodo[0] + problema + "_" + ind +"_" + objetivo;
		
		String arquivo_saida = dir2 + "medidas/" + id + "_comando_friedman.r"; 

		PrintStream psSaida = new PrintStream(arquivo_saida);

		System.out.println(dir2 + "medidas/" + metodo[0] + problema + "_" + ind +"_" + objetivo + "_comando_friedman.txt");
		
		StringBuffer comandos = new StringBuffer();
		
		
		StringBuffer comandosBox = new StringBuffer();
		
		
		
		comandos.append("require(pgirmess)\n AR1 <-cbind(");
		
		
		comandosBox.append("boxplot(");
		

		for (int j = 0; j < algoritmos.length; j++) {
			String arq =  dir + "resultados/" + metodo[j]  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind +"_comando.txt";
			
			
			
			String alg = metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_" + ind;
			
			
			comandos.append(alg + ",");
			
			
			
			comandosBox.append(alg + ",");
			
			
			
			buff = new BufferedReader(new FileReader(arq));
			
			
			
			while(buff.ready()){
			
				psSaida.println(buff.readLine());
				
			}
			
		}  
		
		
		psSaida.println();
		
		
		comandosBox.deleteCharAt(comandosBox.length()-1);
		comandosBox.append(")\n");
		
		
		
		comandos.deleteCharAt(comandos.length()-1);
		comandos.append(")\n");
		
		
		
		comandos.append(	"result<-friedman.test(AR1)\n\n" +
							 "m<-data.frame(result$statistic,result$p.value)\n" +
							 "write.csv2(m,file=\"" +  dir2 + "medidas/result_"+ metodo[0] + problema + objetivo+ "_" + ind+ ".csv\")\n\n" +
							 "pos_teste<-friedmanmc(AR1)\n" +
							 "write.csv2(pos_teste,file=\"" + dir2 + "medidas/friedman_"+ metodo[0] + problema+ objetivo+ "_" + ind +".csv\")");
		
		
		
		
		psSaida.println(comandos);
		
		psSaida.println("png(\"" + dir2 + "medidas/" + id + ".png\")");
		
		psSaida.println(comandosBox);
		
		psSaida.println("dev.off()");
		return arquivo_saida;
	}

	public void executarComandoLinux(String arquivo){
		Runtime run = Runtime.getRuntime();
		
		String command = "R CMD BATCH "+ arquivo;
		try{
			run.exec(command);
		} catch (IOException ex) {ex.printStackTrace();}
	} 


	
	public static void main(String[] args) {
		PrepararArquivos pre = new PrepararArquivos();

		//String dirEntrada = "/media/dados/Andre/ref/medidas/";		
		String dirEntrada = "E:\\Andre\\Manyobjective\\";
		String dirSaida = "E:\\Andre\\Dropbox\\temp\\";
		//String dir2 = "/home/andre/gemini/doutorado/experimentos/poda/";
		//int objetivo = 2;
		String problema  = "DTLZ6";
		
		//String[] algs = {"tb_mga_3_ctd","tb_mga_5_ctd","tb_mga_10_ctd","tb_mga_20_ctd", "tb_mga_30_ctd", "tb_mga_3_ctd_r","tb_mga_5_ctd_r","tb_mga_10_ctd_r","tb_mga_20_ctd_r", "tb_mga_30_ctd_r"};
		//String[] algs = {"tb_mga_3_ext","tb_mga_5_ext","tb_mga_10_ext", "tb_mga_30_ext"};

		//String[] algs = {"0.5_NWSum_hyp_a","0.5_NWSum_hyp_ed","0.5_NWSum_hyp_ex","0.5_NWSum_hyp_m","0.5_NWSum_ideal","0.5_tb_crowd"};
		String[] algs = {"tb_mga_5_hypp"};
		//String[] algs = {"0.5_sigma_ideal", "0.5_tb_ideal"};

		//String[] algs = {"0.25_tb_crowd","0.30_tb_crowd","0.35_tb_crowd","0.40_tb_crowd","0.45_tb_crowd","0.5_NWSum_ideal","0.5_tb_crowd"};
		//String[] algs = {"0.5_NWSum_hyp_a","0.5_NWSum_hyp_ex","0.5_NWSum_hyp_ed","0.5_NWSum_hyp_m","0.5_NWSum_ideal","0.5_tb_crowd"};
		//String metodo[] = {"imulti","imulti","imulti","imulti"};
		//String metodo[] = {"smopso","smopso","smopso","smopso","smopso","smopso"};

		String metodo[] = {"imulti","imulti","imulti","smopso","smopso","smopso","smopso","smopso"};
		//String metodo = "smopso";

		
		
		//int objs[] = {3,5,10,15,20,30};
		int objs[] = {5};
		int exec = 10;

		try{

			
			//pre.setReferenceFile(dirEntrada, dirSaida, problema, objs, algs[0], exec, metodo[0], "reference", null);
			

			
			pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "gd", objs);
			pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "igd", objs);
			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "spacing", objs);
			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "ld", objs);
			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "con", objs);
			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "np", objs);

			
			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "eval", objs, algs,lider);

			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "tchebycheff", objs);
			//pre.preparArquivosIndicadoresTodos(dirEntrada, dirSaida, problema, algs, exec, metodo, "dist", objs, algs,lider);

		
			
			/*for (int i = 0; i < objs.length; i++) {
				System.out.println(objs[i]);
				
				System.out.println("Gerando scripts .R");
				String arquivo = pre.preparArquivosComandosFriedman(dirEntrada, dirSaida,  problema, ""+objs[i], algs, exec, metodo, "gd");
				System.out.println("Executando o script");
				pre.executarComandoLinux(arquivo);
				pre.executarFriedman(dirEntrada, dirSaida, problema, objs[i], algs, exec, metodo, "gd", null);
				
				
				//pre.preparArquivosComandosFriedman(dirEntrada, dirSaida,  problema, ""+objs[i], algs, exec, metodo, "igd");
				//pre.preparArquivosComandosFriedman(dirEntrada, dirSaida,  problema, ""+objs[i], algs, exec, metodo, "spacing");
				//pre.preparArquivosComandosFriedman(dirEntrada, dirSaida,  problema, ""+objs[i], algs, exec, metodo, "ld");
				//pre.preparArquivosComandosFriedman(dirEntrada, dirSaida,  problema, ""+objs[i], algs, exec, metodo, "con");
				//pre.gerarComando(dirEntrada, dirSaida, problema, objs[i], algs, exec, metodo, "hypervolume");
				//pre.preparArquivosComandosFriedman(dirEntrada, dirSaida,  problema, ""+objs[i], algs, exec, metodo, "hypervolume");
			}
			
			System.out.println("Esperando execução do script");
			Thread.sleep(1000);
			
			for (int i = 0; i < objs.length; i++) {
				System.out.println(objs[i]);
				System.out.println("Pós teste do Friedman");
				pre.executarFriedman(dirEntrada, dirSaida, problema, objs[i], algs, exec, metodo, "gd", null);
			}	

			//pre.juntarFronteira(dir, problema, objetivo, algs, exec, metodo);
			//pre.inverterMaxMim(dir, problema, objetivo, algs, exec, metodo);
			//pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "gd", null);
			//pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "igd", null);
			//pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "spread", null);
			//pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "pnf", null);
			//pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "np", null);
			//pre.preparArquivoTempo(dir, problema, ""+objetivo, algs, exec, metodo,  null);

			//pre.preparArquivosComandosFriedman(dir, dir2,   problema, ""+objetivo, algs, exec, metodo, "gd");
			//pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "igd");
			//pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "spread");
			//pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "pnf");
			//pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "np");

			
			//pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "pnf");
			//pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "np");
			
			*/

		} catch (Exception ex){ex.printStackTrace();}
		
		
	}

}
