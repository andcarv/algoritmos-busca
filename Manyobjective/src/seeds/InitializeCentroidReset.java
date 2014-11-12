package seeds;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import kernel.mopso.MOPSO;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class InitializeCentroidReset extends InitializeSwarms {

	
	public final static int PARAMETER_SPACE = 0;
	public final static int OBJECTIVE_SPACE = 1;
	
	public InitializeCentroidReset(MOPSO[] s, Problema p){
		super(s,p);
		ID = "ctdr";
	}
	
	
	/**
	 * Clusters the solutions into k cluster
	 * @param front Solutions to be clustered
	 * @param clusteringSpace Defines the space of the cluster: objective space or parameter space
	 * @param k Number of clusters
	 * @return The centroids of each cluster
	 */
	public ArrayList<double[]> clustering(ArrayList<Solucao> front, int clusteringSpace, int k, int[] groups){
		try{
			
			String temp = "temp/temp" + System.currentTimeMillis()+".arff";
			PrintStream front_weka_file = new PrintStream(temp);
			front_weka_file.println("@RELATION front");
			front_weka_file.println();
			if(clusteringSpace == PARAMETER_SPACE){
				for(int i = 0; i < problema.n; i++){
					front_weka_file.println("@ATTRIBUTE A" + i + " REAL");

				}
			} else{
				for(int i = 0; i < problema.m; i++){
					front_weka_file.println("@ATTRIBUTE A" + i + " REAL");

				}
			}

			front_weka_file.println("\n@DATA");
			for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				StringBuffer data = new StringBuffer();
				if(clusteringSpace == PARAMETER_SPACE){
					for(int i = 0; i<solucao.n; i++)
						data.append(solucao.getVariavel(i) + ",");
				} else{
					for(int i = 0; i<solucao.objetivos.length; i++)
						data.append(solucao.objetivos[i] + ",");
				}

				data.deleteCharAt(data.length()-1);
				front_weka_file.println(data);
			}
			front_weka_file.flush();
			front_weka_file.close();
			
			
			FileReader file_front = new FileReader(temp);
			Instances front_weka = new Instances(file_front);
			//System.out.println();
			
			SimpleKMeans kmeans = new SimpleKMeans();
			kmeans.setNumClusters(k);
			
			kmeans.buildClusterer(front_weka);
			
			//System.out.println(kmeans);
			
			for(int i = 0; i<front_weka.numInstances(); i++){
				groups[i] = kmeans.clusterInstance(front_weka.instance(i));
			}
			
			Instances centroids = kmeans.getClusterCentroids();
			
			ArrayList<double[]> front_centroids = new ArrayList<double[]>();
			
			for(int i = 0; i< centroids.numInstances(); i++){
				double[] centroid_i = centroids.instance(i).toDoubleArray();
				front_centroids.add(centroid_i);
			}
			
			return front_centroids;
		} catch(Exception ex){ex.printStackTrace(); return null;}
		
		
	}
	
	public void initializeSwarms(ArrayList<Solucao> solutions, double box_range) {
		
		int groups[] = new int[solutions.size()];

		ArrayList<double[]> centroids = clustering(solutions, PARAMETER_SPACE,swarms.length, groups);

		//saveCentroids(centroids);


		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];

			double[][] new_limits = new double[2][problema.n];

			double[] centroid = null;
			if(i<centroids.size())
				//Get the centroid guide for the swarm i
				centroid = centroids.get(i);
			else{
				int index_guide = (int)(Math.random() * solutions.size());
				//Get the centroid guide for the swarm i
				centroid = ((SolucaoNumerica)solutions.get(index_guide)).getVariaveis();
			}
			for(int j = 0; j<problema.n; j++){
				//Defines the lower and upper limits of the new search space. The values can't overcome the maximum values
				//of the search space of the problem
				new_limits[0][j] = Math.max(centroid[j] - box_range, problema.MIN_VALUE);
				new_limits[1][j] = Math.min(centroid[j] + box_range, problema.MAX_VALUE);

			}

			if(i<centroids.size()){
				for(int j = 0; j< groups.length; j++){

					if(groups[j] == i){
						swarm.pareto.add(solutions.get(j), swarm.archiver);
						/*if(swarm.populacao.size()<swarm.tamanhoPopulacao){
								SolucaoNumerica s = (SolucaoNumerica)initial_front.get(j);
								Particula particula = new Particula();
								particula.iniciarParticulaAleatoriamente(problema, s);
								problema.calcularObjetivos(s);
								particula.localBestObjetivos = particula.solucao.objetivos;
								swarm.populacao.add(particula);
							}*/
					}

				}
			} else{

				for(int j = 0; j< swarm.archiveSize; j++){
					int random_index = (int)(Math.random() * solutions.size());
					Solucao random_solution = solutions.get(random_index);
					if(!swarm.pareto.getFronteira().contains(random_solution))
						swarm.pareto.add(random_solution, swarm.archiver);
				}
			}

			swarm.populacao.clear();
			swarm.inicializarPopulacao(new_limits);
			swarm.atualizarRepositorio();



		}
				
			
				for (int i = 0; i < swarms.length; i++) {
					//In each swarm, the population chooses the leader according to its Pareto front  
					swarms[i].escolherLider.escolherLideres(swarms[i].populacao, swarms[i].pareto.getFronteira());
					//Initial mutation for swarm i
					swarms[i].escolherParticulasMutacao();
				}


	}


	public void saveCentroids(ArrayList<double[]> centroids)
			throws FileNotFoundException {
		PrintStream psCen = new PrintStream("centroids.txt");
		for (Iterator<double[]> iterator = centroids.iterator(); iterator
				.hasNext();) {
			double[] ds = (double[]) iterator.next();
			for (int i = 0; i < ds.length; i++) {
				double d = ds[i];
				psCen.print(new Double(d).toString().replace('.', ',')  + "\t");
			}
			psCen.println();
		}
		psCen.close();
	}

}
