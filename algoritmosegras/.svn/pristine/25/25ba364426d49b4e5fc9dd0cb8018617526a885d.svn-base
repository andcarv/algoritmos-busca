package votacao;


import java.util.ArrayList;
import java.util.Collections;

import kernel.Classe;
import regra.ComparatorEspecificidade;
import regra.ComparatorRegraLaplace;
import regra.ComparatorSensitividade;
import regra.Regra;
import weka.core.Instance;

	public class VotacaoEspecificidadeOrdenacao extends Votacao {
		private int metodoOrdenacao;
		private static int SENSITIVIDADE = 1;
		private static int ESPECIFICIDADE = 2;

		public VotacaoEspecificidadeOrdenacao() {
			
			System.out.println("Votação: Especificidade com Especificidade");
		}

		/**
		 * Mï¿½todo que restringe a votaï¿½ï¿½o ï¿½ somente k regras por classe, onde k
		 * representa o nï¿½mero de classes do problema. Inicialmente ordena todas as
		 * regras, depois executa a votaï¿½ï¿½o.
		 */
		public double votacao(ArrayList<Regra> regras, Instance exemplo,
				String classePositiva) {
			double positivo = 0;
			double negativo = 0;

			ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
			ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
			double k = exemplo.classAttribute().numValues();

			obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras,
					exemplo, classePositiva);

			ComparatorEspecificidade comp = new ComparatorEspecificidade();

			Collections.sort(regrasVotacaoPositiva, comp);
			Collections.sort(regrasVotacaoNegativa, comp);

			// Votaï¿½ï¿½o das k melhores regras para cada classe

			positivo = votarEspecifidadeMedia(regrasVotacaoPositiva, k);
			negativo = votarEspecifidadeMedia(regrasVotacaoNegativa, k);

			return positivo - negativo;
		}

		@Override
		public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras,
				Instance exemplo, ArrayList<Classe> classes) {

			ArrayList<Integer> classesMaisVotadas = new ArrayList<Integer>();
			int classeMaisVotada = 0;
			int[] classePontuacao = new int[classes.size()];
			double k = exemplo.classAttribute().numValues();
			ComparatorEspecificidade comp = new ComparatorEspecificidade();

			for (int i = 0; i < classes.size(); i++) {
				ArrayList<Regra> regrasDaClasseQueVotamNoExemplo = new ArrayList<Regra>();
				obterRegrasVotamMultiClasse(regrasDaClasseQueVotamNoExemplo,
						regras, exemplo, classes.get(i).getNome());
				Collections.sort(regrasDaClasseQueVotamNoExemplo, comp);
				classePontuacao[i] += votarSensitividadeMedia(
						regrasDaClasseQueVotamNoExemplo, k);
			}
			// verifica qual foi a mais votada
			for (int i = 0; i < (classes.size() - 1); i++) {
				if (classePontuacao[i + 1] > classePontuacao[i]) {
					classeMaisVotada = i + 1;
				}
			}
			classesMaisVotadas.add(classeMaisVotada);
			// verifica se houve empate entre as classes mais votada e preeche o
			// array classesMaisVotadas em caso positivo
			for (int i = 0; i < classes.size(); i++) {
				if ((classeMaisVotada != i)
						&& (classePontuacao[i] == classePontuacao[classeMaisVotada])) {
					classesMaisVotadas.add(i);
				}
			}
			return classesMaisVotadas;
		}
	}
