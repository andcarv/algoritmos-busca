package problema;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import solucao.Solucao;
import solucao.SolucaoBinaria;

public class Knapsack extends Problema {

    int[][] objectives;
    
    int[] weights;
    
    int capacity;
    
    int numberOfObjectives_, numberOfVariables_, numberOfConstraints_;
    
    double[] lowerLimit_, upperLimit_;

    public Knapsack() {
    	super();
    	try{
    		String filename = "/home/andre/git/algoritmos-busca/Manyobjective/knapsack/instancias_mochila/p5/KP_p-5_n-20_ins-1.dat";
    		System.setIn(new FileInputStream(filename));
    	} catch (IOException ex){ex.printStackTrace();}
        Scanner s = new Scanner(System.in);
        numberOfObjectives_ = s.nextInt();
        numberOfVariables_ = s.nextInt();
        
        m = numberOfObjectives_;
        n = numberOfVariables_;
        objectives = new int[numberOfObjectives_][numberOfVariables_];
        weights = new int[numberOfVariables_];
    	capacity = s.nextInt();
    	for(int i=0; i<numberOfObjectives_; i++) {
    		for(int j=0; j<numberOfVariables_; j++) {
    			objectives[i][j] = s.nextInt();
    		}
    	}
    	for(int i=0; i<numberOfVariables_; i++) {
    		weights[i] = s.nextInt();
    	}
    	
        numberOfConstraints_ = 1;
        problema = "knapsack";

        lowerLimit_ = new double[numberOfVariables_];
        upperLimit_ = new double[numberOfVariables_];

        for (int var = 0; var < numberOfVariables_; var++) {
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 1.0;
        }
        
        s.close();
        
        avaliacoes = 0;
    }

    @Override
    public double[] calcularObjetivos(Solucao solucao){    	
    	if(!solucao.isBinaria())
    		System.err.println("Must be a binary solution.");
    	
    	SolucaoBinaria solution = (SolucaoBinaria) solucao;
    	
        double[] variables = solution.getVariaveis();
        double[] fitness = new double[numberOfObjectives_]; // function values
        for (int var = 0; var < numberOfVariables_; var++) {
            if(variables[var]==1){
            	for(int i=0; i<numberOfObjectives_; i++) {
            		fitness[i] += objectives[i][var];
            	}
                
            }
        } // for
        
        double penalty = evaluateConstraints(solution);
        
        for(int i=0; i<numberOfObjectives_; i++){
        	solution.objetivos[i] = fitness[i] * penalty;
        }
        
        avaliacoes++;
	    
	    return solucao.objetivos;
    }


    public double evaluateConstraints(Solucao solucao){
    	
    	if(!solucao.isBinaria())
    		System.err.println("Must be a binary solution.");
    	
    	SolucaoBinaria solution = (SolucaoBinaria) solucao;
    	
        double[] variables = solution.getVariaveis();
        int total = 0;
        for (int var = 0; var < this.numberOfVariables_; var++) {
            if (variables[var] == 1) {
                total += weights[var];
            }
        }
        if (total > capacity) {
        	return Math.max(1 - (total-capacity)/capacity, 0.0);
        } else
        	return 1.0;
    }
}
