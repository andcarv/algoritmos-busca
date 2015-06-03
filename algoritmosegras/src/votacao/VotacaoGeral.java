package votacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ordenacao.Extremos;
import ordenacao.Joelho;
import kernel.Classe;
import kernel.ObterRegras;
import regra.ComparatorEspecificidade;
import regra.ComparatorRegraSupportConfidence;
import regra.ComparatorSensitividade;
import regra.Regra;
import weka.core.Instance;

public class VotacaoGeral extends Votacao {
	private Comparator<Regra> comparatorOrdenacao;
	private String mostrarMetodo;
	private int votacaoM = -1;
	private int ordenacao = -1;
	private int medida = -1;

	public VotacaoGeral(int ordenacao, int medida, int votacaoMedia) {
		this.medida = medida;
		this.ordenacao = ordenacao;
		this.votacaoM = votacaoMedia;
		if (ordenacao == ObterRegras.KNEE) {
			mostrarMetodo = "Joelho - ";
		} else if (ordenacao == ObterRegras.EXTR) {
			mostrarMetodo = "Extremo - ";
		}

		if (medida == ObterRegras.CSA) {
			mostrarMetodo += "CSA - ";
		} else if (medida == ObterRegras.SENS) {
			mostrarMetodo += "Sensitividade - ";
		} else if (medida == ObterRegras.SPEC) {
			mostrarMetodo += "Especificidade - ";
		}

		if (votacaoMedia == ObterRegras.LP) {
			mostrarMetodo += "LaPlace";
		} else if (votacaoMedia == ObterRegras.SENS) {
			mostrarMetodo += "Sensitividade";
		} else if (votacaoMedia == ObterRegras.SPEC) {
			mostrarMetodo += "Especificidade";
		}
		System.out.println("Votação: " + mostrarMetodo);
	}

	public VotacaoGeral(int ordenacao, int votacaoMedia) {
		this.votacaoM = votacaoMedia;
		this.ordenacao = ordenacao;
		switch (ordenacao) {
		case ObterRegras.CSA:
			comparatorOrdenacao = new ComparatorRegraSupportConfidence();
			mostrarMetodo = "CSA - ";
			break;

		case ObterRegras.SENS:
			comparatorOrdenacao = new ComparatorSensitividade();
			mostrarMetodo = "Sensitividade - ";
			break;

		case ObterRegras.SPEC:
			comparatorOrdenacao = new ComparatorEspecificidade();
			mostrarMetodo = "Especificidade - ";
			break;
		}

		switch (votacaoMedia) {
		case ObterRegras.LP:
			mostrarMetodo += "LaPlace";
			break;
		case ObterRegras.SENS:
			mostrarMetodo += "Sensitividade";
			break;
		case ObterRegras.SPEC:
			mostrarMetodo += "Especificidade";
			break;
		}
		System.out.println("Votação: " + mostrarMetodo);

	}

	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0, negativo = 0;
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras,
				exemplo, classePositiva);
		if (this.medida == -1) {// indica método sem joelho ou extremo
			Collections.sort(regrasVotacaoPositiva, comparatorOrdenacao);
			Collections.sort(regrasVotacaoNegativa, comparatorOrdenacao);
		} else {
			if (ordenacao == ObterRegras.KNEE) {
				regrasVotacaoPositiva = new Joelho().obterJoelho(
						regrasVotacaoPositiva, medida, (int) k);
				regrasVotacaoNegativa = new Joelho().obterJoelho(
						regrasVotacaoNegativa, medida, (int) k);
			} else if (ordenacao == ObterRegras.EXTR) {
				regrasVotacaoPositiva = Extremos.obterExtremo(
						regrasVotacaoPositiva, medida, (int) k);
				regrasVotacaoNegativa = Extremos.obterExtremo(
						regrasVotacaoNegativa, medida, (int) k);
			}
		}
		positivo = getResultadotVotacao(regrasVotacaoPositiva, k);
		negativo = getResultadotVotacao(regrasVotacaoNegativa, k);

		return positivo - negativo;
	}

	@Override
	public ArrayList<Integer> votacaoMultiClasse(ArrayList<Regra> regras,
			Instance exemplo, ArrayList<Classe> classes) {

		ArrayList<Integer> classesMaisVotadas = new ArrayList<Integer>();
		int classeMaisVotada = 0;
		int[] classePontuacao = new int[classes.size()];
		double k = exemplo.classAttribute().numValues();

		for (int i = 0; i < classes.size(); i++) {
			ArrayList<Regra> regrasDaClasseQueVotamNoExemplo = new ArrayList<Regra>();
			obterRegrasVotamMultiClasse(regrasDaClasseQueVotamNoExemplo,
					regras, exemplo, classes.get(i).getNome());
			Collections.sort(regrasDaClasseQueVotamNoExemplo,
					comparatorOrdenacao);
			classePontuacao[i] += getResultadotVotacao(
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

	private double getResultadotVotacao(ArrayList<Regra> regra, double k) {
		double valor = 0;
		switch (votacaoM) {
		case ObterRegras.LP:
			valor = votarLaplaceMedia(regra, k);
			break;
		case ObterRegras.SENS:
			valor = votarSensitividadeMedia(regra, k);
			break;
		case ObterRegras.SPEC:
			valor = votarEspecifidadeMedia(regra, k);
			break;
		}
		return valor;
	}

}
