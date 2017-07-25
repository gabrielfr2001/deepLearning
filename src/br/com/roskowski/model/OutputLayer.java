package br.com.roskowski.model;

public class OutputLayer extends Layer {
	private double[][] target;

	public OutputLayer(int size) {
		neurons = new Neuron[size];
	}

	public void prepare(int e, double[][] target) {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(e);
			neurons[i].prepare(neurons.length);
		}
		this.target = target;
	}

	public double calculateTotalError(int o) {
		double d = 0;
		for (int i = 0; i < target[o].length; i++) {
			double di = target[o][i] - neurons[i].out;
			neurons[i].expected = target[o][i];
			d += (1 / 2f) * (di * di);
		}
		return d;
	}

	public void printOuts() {
		for (Neuron n : neurons) {
			System.out.println("O" + n.getClass().getSimpleName() + ": " + n.out);
		}
	}

	public double[] getResult() {
		double d[] = new double[neurons.length];
		for (int i = 0; i < neurons.length; i++) {
			d[i] = neurons[i].out;
		}
		return d;
	}

}
