package util;

import java.io.IOException;
import java.io.PrintStream;

public class GerarArquivosConfiguracao {
	
	
	
	public static void gerarArquivos(int m, String ind, PrintStream psExec) throws IOException{
		
		
		
		String jar = "ref.jar";

		String problema  = "dtlz2";
	



		//String[] algs = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.5"};
		String[] algs = {"0.5"};
		String metodo = "multi";
		String objetivos = "-";
		String exec = "20";
		String g = "100";
		String a = "-1";
		//String a = "-1";
		String p[] = {"200", "50", "20"};
		//String[] p = {"100"};
		String r = "200";
		String rank = "false";
		//String archiver[] = {"hyp_m", "hyp_ex", "ideal", "dist", "eucli", "tcheb", "ar", "crowd", "rand", "ag", "dom", "mga"};
		String archiver[] = {"mga;ideal"};
		String[] eps = {"0.1","0.05", "0.025", "0.01", "0.005", "0.0025", "0.001", "0.0005", "0.00025", "0.0001"  };
		int k = 10;

		String lider = "tb";
		String  direxec = "/home/andre/doutorado/experimentos/multi/";

		String[]  swarms = {"3","10","30"};
		//String[]  swarms = {"6"};
		String shared = "false";
		//String[] update = {"2", "10", "20", "50"};
		String[] update = {"10"};
		String box_range_beg = "0.5";	
		String box_range_end = "0.1";	
		String pop_swarm = "75";
		String rep_swarm = "200";
		String split_iterations = "5";
		String eval_analysis = "false";
		//String[] reset = {"true" , "false"};
		String[] reset = {"false"};
		String initialize[] = {"ext", "rnd", "ctd"};
		//String initialize[] = {"ctd"};


		/*if(!ind.equals("")){
			algs = new String[1];
			algs[0] = "0.5";
		}*/
		if(metodo.equals("imulti")){
			for (int i = 0; i < swarms.length; i++) {
				String sw = swarms[i];
				for(int j = 0; j < initialize.length; j++){
					String init = initialize[j];
					for(int l = 0; l < reset.length; l++){
						String res = reset[l];

						String[] liderTemp = lider.split(";");
						String[] archiveTemp = archiver[0].split(";");
						String arquivo = "";
						String id = "";
						String pos_id = "";
						if(res.equals("true")){
							pos_id = liderTemp[0]  + "_" +  archiveTemp[0]+ "_" + sw + "_" + init +"_r";
							id = "principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ pos_id;
							
							if(ind.equals(""))
								arquivo =  "arquivosMulti/" + id +".txt";
							else
								arquivo =  "arquivosMulti/" + id + "_"+ ind+ ".txt";
						}
						else{
							pos_id = liderTemp[0]  + "_" +  archiveTemp[0]+ "_" + sw + "_" + init;
							id = "principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ pos_id;
							if(ind.equals(""))
								arquivo =  "arquivosMulti/" + id +".txt";
							else
								arquivo =  "arquivosMulti/" + id + "_"+ ind+ ".txt";
						}
						System.out.println(arquivo);
						PrintStream ps = new PrintStream(arquivo);
						
						if(!ind.equals(""))
							psExec.println("nohup java  -Xms128m -Xmx768m -jar " + jar + " " + arquivo + " > logs/" + id+ "_"+ ind+".log &");
						else
							psExec.println("nohup java  -Xms128m -Xmx768m -jar " + jar + " " + arquivo + " > logs/" + id+".log &");


						ps.println("algoritmo = " + metodo);
						ps.println("problema = " + problema);
						
						if(!ind.equals("")){
							String direxec2 = direxec + "resultados/" + metodo + "/" + problema.toUpperCase() + "/" + m + "/" + pos_id + "/";
							System.out.println(direxec2);
							ps.println("direxec =  " + direxec2);
							ps.println("indicador = " + ind);
							ps.println("front = " + metodo + "_" + problema.toUpperCase() + "_" + m + "_" + pos_id);
						} else
							ps.println("direxec =  " + direxec);
						ps.println("m = " + m);
						ps.println("k = " + k);
						ps.println("max_min = " + objetivos);
						ps.println("geracoes = " + g);
						ps.println("populacao = " + p[0]);
						ps.println("repositorio = " + r);
						ps.println("numexec = " + exec);
						ps.println("S =" + algs[0]);
						ps.println("rank = false");
						ps.println("archiver = " + archiver[0]);				
						ps.println("lider = " + lider);	
						ps.println("swarms = " + sw);
						ps.println("box_range_mim = " + box_range_beg);	
						ps.println("box_range_max = " + box_range_end);	
						ps.println("pop_swarm = " + pop_swarm);
						ps.println("rep_swarm = " + rep_swarm);
						ps.println("split_iterations = " + split_iterations);
						ps.println("eval_analysis = " + eval_analysis);
						ps.println("reset = " + res);
						ps.println("initialize = " + init);


						
							
					}
				}
			}
		}
		
		if(metodo.equals("multi")){
			for (int i = 0; i < swarms.length; i++) {
				String sw = swarms[i];
				for(int j = 0; j < update.length; j++){
					String up = update[j];
					for(int l = 0; l < p.length; l++){
						String pop = p[i];
						String[] liderTemp = lider.split(";");
						String id = "principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ up +"_" +  liderTemp[0]  + "_" +  archiver[0].replace(';', '_')+ "_" + sw + "_"+ pop; 
						String arquivo =  "arquivosMulti/" +id+ ".txt";
						PrintStream ps = new PrintStream(arquivo);	

						psExec.println("nohup java  -Xms128m -Xmx768m -jar " + jar + " " + arquivo + " > logs/" + id+".log &");


						ps.println("algoritmo = " + metodo);
						ps.println("problema = " + problema);
						ps.println("direxec =  " + direxec);
						ps.println("m = " + m);
						ps.println("k = " + k);
						ps.println("max_min = " + objetivos);
						ps.println("geracoes = " + g);
						ps.println("populacao = " + pop);
						ps.println("repositorio = " + r);
						ps.println("numexec = " + exec);
						ps.println("S =" + algs[0]);
						ps.println("rank = false");
						ps.println("archiver = " + archiver[0]);				
						ps.println("lider = " + lider);	
						ps.println("swarms = " + sw);
						ps.println("shared = " + shared);
						ps.println("update = " + up);
						ps.println("box_range_mim = " + box_range_beg);	
						ps.println("box_range_max = " + box_range_end);	
						ps.println("eval_analysis = " + eval_analysis);
					}
				}
			}
		}

		
		
		
		if(archiver[0].equals("eaps") || archiver[0].equals("eapp")){
			for (int i = 0; i < eps.length; i++) {
				String e = eps[i];
				String arquivo = "";
				String id = "principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ e  + "_" +  lider+ "_" + archiver[0];
				if(ind.equals(""))
					arquivo = "arquivos/" + id + ".txt";
				
				PrintStream ps = new PrintStream(arquivo);
				

				psExec.println("nohup java  -Xms128m -Xmx768m -jar " + jar + " " + arquivo + " > logs/" + id+".log &");


				ps.println("algoritmo = " + metodo);
				ps.println("problema = " + problema);
				ps.println("direxec =  " + direxec);
				ps.println("m = " + m);
				ps.println("k = " + k);
				ps.println("max_min = " + objetivos);
				ps.println("geracoes = " + g);
				ps.println("populacao = " + p[0]);
				ps.println("repositorio = " + r);
				ps.println("numeroavaliacoes = " + a);
				ps.println("numexec = " + exec);
				ps.println("S = 0.5");
				ps.println("rank = false");
				ps.println("archiver = " + archiver[0]);				
				ps.println("lider = " + lider);	
			}
		} else {
			if(metodo.equals("smopso")){ 
				for (int i = 0; i < algs.length; i++) {
					for (int j = 0; j < archiver.length; j++) {
						
					String archiver_j = archiver[j];
					String s = algs[i];
					String arquivo = "";
					String pos_id  = m + "_"+s + "_" +  lider +"_" + archiver_j;
					String id = "principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ pos_id;;

					if(ind.equals("")){
	
						id = "principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ pos_id;
						arquivo = "arquivos/" + id +".txt";
					}

					else{
						id = pos_id + "_" + ind;
						arquivo = "arquivos/principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_" +  pos_id + "_" + ind+ ".txt";
					}

				
					if(!rank.equals("false"))
						arquivo = "arquivos/principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ s  + "_"+lider + "_" + rank + ".txt";

					PrintStream ps = new PrintStream(arquivo);

					psExec.println("nohup java  -Xms128m -Xmx768m -jar " + jar + " " + arquivo + " > logs/" + id+".log &");

					ps.println("algoritmo = " + metodo);
					ps.println("problema = " + problema);
					ps.println("m = " + m);
					ps.println("k = " + k);
					ps.println("max_min = " + objetivos);
					ps.println("geracoes = " + g);
					ps.println("numeroavaliacoes = " + a);
					ps.println("populacao = " + p[0]);
					ps.println("repositorio = " + r);		
					ps.println("numexec = " + exec);
					ps.println("S = " + s);
					ps.println("rank = " + rank);
					ps.println("archiver = " + archiver_j);				
					ps.println("lider = " + lider);			
					ps.println("direxec =  " + direxec);
				}
				}
			}
		}
	}			
	public static void main(String[] args) {


		int[] ms = {3,5,10,15,20};
		//int[] ms = {10,15,20};		

	
		String ind = "";

		try{
			PrintStream psExec = new PrintStream("exec-arq.txt");
			for (int i = 0; i < ms.length; i++) {
				System.out.println(ms[i]);
				gerarArquivos(ms[i],ind, psExec);
			}
			

		} catch (Exception e) { e.printStackTrace();
		}
	}

}
