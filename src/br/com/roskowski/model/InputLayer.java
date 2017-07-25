package br.com.roskowski.model;

public class InputLayer extends Layer {

	public void prepare(HiddenLayer layer, int length) {
		neurons = new Neuron[length];
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(-1);
			neurons[i].sinapsis = new Sinapsis[layer.neurons.length];
			for (int o = 0; o < layer.neurons.length; o++) {
				neurons[i].sinapsis[o] = new Sinapsis();
				neurons[i].sinapsis[o].neuron = layer.neurons[o];
			}
		}
	}

	public void setInputs(double[] i) {
		for (int o = 0; o < i.length; o++) {
			try {
				neurons[o].lastIn = i[o];
				neurons[o].net = i[o];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void calculateNewWeights(double error, Layer layer) {
		if (layer instanceof HiddenLayer) {
			for (int i = 0; i < neurons.length; i++) {
				for (int o = 0; o < neurons[i].sinapsis.length; o++) {
					double eAOut = 0;
					for (int k = 0; k < neurons[i].sinapsis[o].neuron.sinapsis.length; k++) {
						try {
							eAOut += neurons[i].sinapsis[o].neuron.eAOut[k] * neurons[i].sinapsis[o].neuron.oANet[k]
									* neurons[i].sinapsis[o].neuron.sinapsis[k].weight;
						} catch (Exception e) {
						}
					}
					double d = neurons[i].sinapsis[o].neuron.out * (1 - neurons[i].sinapsis[o].neuron.out);
					double deltaRule = d * neurons[i].net * eAOut;
					neurons[i].sinapsis[o].newWeight = neurons[i].sinapsis[o].weight - rate * deltaRule;
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
