package br.com.roskowski.model;

public class Layer {
	public Neuron[] neurons;
	protected double rate = 10;
	public double bias;
	public String id;
	public static int counter;

	public Layer() {
		counter++;
		this.id = "" + counter;
	}

	public void reset() {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].lastIn = neurons[i].net;
			neurons[i].net = 0;
			neurons[i].lastOut = neurons[i].out;
			neurons[i].out = 0;
		}
	}

	public void activate() {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].activate(bias);
		}
	}

	public void link(Layer layer) {
		for (int i = 0; i < layer.neurons.length; i++) {
			for (int o = 0; o < neurons.length; o++) {
				try {
					neurons[o].sinapsis[i].neuron = layer.neurons[i];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void applyBias() {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].net += bias;
		}
	}
}
