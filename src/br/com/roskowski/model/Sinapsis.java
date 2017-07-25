package br.com.roskowski.model;

import br.com.roskowski.util.Functions;

public class Sinapsis {
	public double weight;
	public double newWeight;
	public Neuron neuron;
	public static final int ROUND = 5;

	public Sinapsis() {
		weight = Functions.randomWeight(-0.5, 0.5);
	}

	public void applyWeight(double out) {
		neuron.net += out * weight;
	}

	public double activate(double net) {
		if (neuron != null) {
			neuron.net += net * weight;
			// System.out.println("weight: " + Functions.round(weight, ROUND));
			// System.out.println(" mod: " + Functions.round(neuron.net,
			// ROUND));
		}
		return net;
	}
}
