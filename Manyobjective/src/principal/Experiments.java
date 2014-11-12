package principal;

import java.io.IOException;

import problema.DTLZ2;


public class Experiments {
	
	public static void main(String[] args) {
		Principal principal = new Principal();
		int k = 10;
		int objs[] = {2,3,4,5,6};
		String fronts[] = {"edge", "knee"};
	
		
		
		
		principal.dirExec = "sub/";
		principal.indicador = "all";
		principal.prob  = "dtlz2";
		
		
		try{
			for(int j = 0; j < fronts.length; j++){
				String front = fronts[j];
				for (int i = 0; i < objs.length; i++) {
					
					principal.m = objs[i];
					principal.front = principal.prob + "_" + principal.m + "_" + front;
					System.out.println(principal.front);
					principal.maxmimObjetivos = new String[principal.m];
					for (int l = 0; l < principal.maxmimObjetivos.length; l++) {
						principal.maxmimObjetivos[l] = "-";
					}
					
					DTLZ2 dtlz2 = new DTLZ2(principal.m, k);
					principal.problema = dtlz2;

					principal.executarIndicador();

				}
			}
		}catch(IOException ex){ex.printStackTrace();}
		
	}
}
