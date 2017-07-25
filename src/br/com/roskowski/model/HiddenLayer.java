package br.com.roskowski.model;

public class HiddenLayer extends Layer {

	private int outSize;

	public HiddenLayer(int size) {
		super();
		neurons = new Neuron[size];
		outSize = size;
	}

	public HiddenLayer(int size, int out) {
		neurons = new Neuron[size];
		if (out != 0)
			outSize = out;
		else {
			outSize = size;
		}
	}

	public void prepare(int e) {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(e);
			neurons[i].prepare(outSize);
		}
	}

	public void calculateNewWeights(double error, Layer layer) {
		if (layer instanceof OutputLayer) {
			for (int i = 0; i < neurons.length; i++) {
				neurons[i].eAOut = new double[neurons[i].sinapsis.length];
				neurons[i].oANet = new double[neurons[i].sinapsis.length];
				for (int o = 0; o < neurons[i].sinapsis.length; o++) {
					try {
						if (neurons[i].sinapsis[o].neuron != null) {
							double eAOut = neurons[i].sinapsis[o].neuron.out - neurons[i].sinapsis[o].neuron.expected;
							double oANet = neurons[i].sinapsis[o].neuron.out * (1 - neurons[i].sinapsis[o].neuron.out);
							neurons[i].eAOut[o] = eAOut;
							neurons[i].oANet[o] = oANet;
							double nAWeight = neurons[i].out;
							double deltaRule = eAOut * oANet * nAWeight;
							neurons[i].sinapsis[o].newWeight = neurons[i].sinapsis[o].weight - deltaRule * rate;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void assignNewWeights() {
		for (int o = 0; o < neurons.length; o++) {
			neurons[o].assignNewWeights();
		}
	}

}
