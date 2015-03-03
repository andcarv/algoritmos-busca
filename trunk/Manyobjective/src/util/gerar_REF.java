package util;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Random;

public class gerar_REF{
	static double intervalo, limInf, limSup;
	static double margemErro=1-0.99999; //aceitar uma pequena margem de erro por falhas de arredondamento que impediam a geração de 100 ou mais pontos
	static int dimensoes, divisoes, layers;
	static int cont=0;
	static PrintStream ps;
	
	
	public static void main(String[] args) {
		try{
			 ps = new PrintStream("temp.txt");
		/*if(args.length < 2){
			System.out.println("Uso: front_real <dimensoes(obj)> <divisoes(h)>");
			System.exit(1);
		}*/		
		dimensoes= 10;
		divisoes=4;
		layers=1;
		
		// REF, DTLZ1, DTLZ2, DTLZ3, DTLZ4
		//	dim		div		lay	pontos
		//	2		10.000	1	10.001+100.000
		//	3		200		1	20.301+100.000
		//	5		25		1	23.751+100.000
		//	8		12		1	50.388+100.000
		//	9		11		1	75.582+100.000
		//	10		10		1	92.378+100.000					184.756
		//	15		7		1	116280+100.000					232.560
		//	20		6		1	177100+100.000					354.200
		
		
		
		//Pesoss - 100
		//	dim		div		pontos
		//	2		99		100+0
		//	3		12		91+9
		//	5		4		70+30
		//	8		2		36+64
		//	9		2		45+55
		//	10		2		55+45
		//	15		1		15+85
		//	20		1		20+80
		
		//Pesos - 1500
		//	dim		div		pontos
		//	5		11		1365+135
		//	8		5		792+708
		//	9		5		1287+213
		
		//	10		4		715+785
		//	15		3		680+820
		//	20		2		210+1290
		

		REF();
		System.err.println(cont);
		aleatorioNormalizado();
		System.out.println();
	} catch(IOException ex) {ex.printStackTrace();}
	}

	public static void REF(){
		for(int l=0;l<layers;l++){
			if(l>0){
				limInf=(1.0/(dimensoes+l));
				limSup=(1-limInf);
			}else{
				limInf=0;
				limSup=1;
			}
			intervalo=((1-(dimensoes*limInf))/(double)divisoes);
			
			limSup+=margemErro;
			double vetor[] = new double[dimensoes];
			Arrays.fill(vetor, limInf);
			
			while(vetor[0] < limSup){ //repete ate o fim

				while(vetor[vetor.length-1] < limSup){ //repete ate estourar o ultimo elemento
					double somaCol=0;
					for(int j=0;j<vetor.length;j++)
						somaCol+=vetor[j];

					if(somaCol>1-margemErro && somaCol<1+margemErro){
						
						for(int j=0;j<vetor.length;j++){
							//System.out.print(Math.sqrt(vetor[j])+" ");
							//System.out.print((vetor[j])+" ");
							ps.print((vetor[j])+" ");
						}
						//System.out.println();
						ps.println();
						cont++;
					}
				
					vetor[vetor.length-1]+=intervalo;
				}

				//so chega aqui se o ultimo elemento estiver estourado
				for(int i=vetor.length-1;i>0;i--){ //verifica de tras pra frente se os elementos estao estourados para incrementar o anterior
					double somaCol=0;
					for(int j=0;j<vetor.length;j++)
						somaCol+=vetor[j];
						
					if(vetor[i] >= limSup || somaCol > 1+margemErro){// se o elemento estiver estourado ou se toda a linha estiver acima do limite, incrementa o proximo
						vetor[i]=limInf;
						vetor[i-1]+=intervalo;
					}
				}
			}
		}
	}
	public static void aleatorioNormalizado(){
		int linhas=100000;
		int tamArch=1500;
		
		double[] vetor=new double[dimensoes];
		Random rand = new Random();
		rand.setSeed(0);
		for(int i=0;i<(linhas);i++){
			double soma=0;
			for(int o=0;o<dimensoes;o++){
				vetor[o]=rand.nextDouble();
				soma+=vetor[o];
			}

			if(cont<tamArch){
				for(int o=0;o<dimensoes;o++)
				//System.out.print(Math.sqrt(vetor[o]/soma)+"\t");
					//System.out.print(((vetor[o]/soma))+"\t");
					ps.print(((vetor[o]/soma))+"\t");
				//System.out.println();
				ps.println();
				cont++;
			}
			else{
				System.err.println(cont);
				System.exit(0);
			}
		}
	}
}