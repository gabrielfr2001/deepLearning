package br.com.roskowski.model;

import br.com.roskowski.util.Functions;

public class Neuron {
	public int function;
	public int x;
	public int y;
	public double net;
	public double out;
	public Sinapsis[] sinapsis;
	public double expected;
	public double[] eAOut;
	public double[] oANet;
	public String id;
	public double lastOut;
	public double lastIn;
	public String functionString;
	private static int counter;

	public Neuron(int function) {
		counter++;
		this.id = "" + counter;
		this.function = function;
	}

	public void prepare(int size) {
		sinapsis = new Sinapsis[size];
		for (int i = 0; i < size; i++) {
			sinapsis[i] = new Sinapsis();
		}
	}

	public void activate(double bia) {
		for (int i = 0; i < sinapsis.length; i++) {
			out = activate(i);
		}
		// System.out.println("net: " + net);
		// System.out.println("out: " + out);
	}

	private double activate(int i) {
		switch (function) {
		case 3:
			this.functionString = "f(x)=x*x"; 
			return sinapsis[i].activate(Functions.SELU(net));
		case 2:
			this.functionString = "f(x)=x>0?2*x:x"; 
			return sinapsis[i].activate(Functions.leckyPreLU(net));
		case 1:
			this.functionString = "f(x)=x>0?1:0"; 
			return sinapsis[i].activate(Functions.binaryStep(net));
		case 0:
			this.functionString = "f(x)=1/(1+e^-x)"; 
			return sinapsis[i].activate(Functions.logistic(net));
		case -1:
			this.functionString = "f(x)=x"; 
			return sinapsis[i].activate(net);
		}
		return 0;
	}

	public void assignNewWeights() {
		for (int i = 0; i < sinapsis.length; i++) {
			sinapsis[i].weight = sinapsis[i].newWeight;
		}
	}
}
