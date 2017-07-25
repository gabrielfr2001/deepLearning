package br.com.roskowski.model;

import java.util.Random;

public class Learner {
	private double[][] target;
	private double[][] input;
	private int hiddenLayersSize;
	private int hiddenLayersNumber;
	HiddenLayer[] hiddenLayers;
	InputLayer inputLayer;
	OutputLayer outputLayer;
	private double[][][] predef;

	public Learner(double[][] input, double[][] target) {
		this.input = input;
		this.target = target;
	}

	public Learner(double[][] input, double[][] target, double[][][] predef2) {
		this.input = input;
		this.target = target;
		this.predef = predef2;
	}

	public static void main(String[] args) {
		double[][] input = { { 0.01 }, { 0.02 }, { 0.03 }, { 0.04 } };
		double[][] target = { { 0.02 }, { 0.04 }, { 0.06 }, { 0.08 } };
		//double predef[][][] = { { { 0 }, { .15, .25 }, { .20, .30 } }, { { .35 }, { .40, .50 }, { .45, .55 } },
		//		{ { .60 } } };
		Learner learner = new Learner(input, target);
		learner.hiddenLayersNumber = 2;
		learner.hiddenLayersSize = 2;
		learner.prepare();
		learner.trainInThread(0.00001, new CallBack() {
			public void callback() {
				for (int i = 0; i < 100; i++) {
					double[] test = { i/100f };
					double result[] = learner.test(test);
					for (double d : result) {
						System.out.println(d);
					}
				}
			}
		});
		System.out.println(9);
	}

	private void trainInThread(double d, CallBack callBack) {
		new Runnable() {
			public void run() {
				train(d);
				callBack.callback();
			}
		}.run();
	}

	private double[] test(double[] test) {
		inputLayer.setInputs(test);
		inputLayer.activate();

		for (int o = 0; o < hiddenLayersNumber; o++) {
			HiddenLayer hidden = hiddenLayers[o];
			hidden.applyBias();
			hidden.activate();
		}
		outputLayer.applyBias();
		outputLayer.activate();
		double[] result = outputLayer.getResult();
		resetLayers();
		return result;
	}

	private void prepare() {
		inputLayer = new InputLayer();
		hiddenLayers = new HiddenLayer[hiddenLayersNumber];
		outputLayer = new OutputLayer(target[0].length);
		int f = 0;
		outputLayer.prepare(f, target);

		for (int i = 0; i < hiddenLayersNumber; i++) {
			if (i > 0) {
				hiddenLayers[i] = new HiddenLayer(hiddenLayersSize, target[0].length);
				hiddenLayers[i].prepare(f);
				hiddenLayers[i - 1].link(hiddenLayers[i]);
			} else if (i == hiddenLayersNumber - 1) {
				hiddenLayers[i] = new HiddenLayer(hiddenLayersSize);
				hiddenLayers[i].prepare(f);
			} else {
				hiddenLayers[i] = new HiddenLayer(hiddenLayersSize);
				hiddenLayers[i].prepare(f);
			}
		}

		hiddenLayers[hiddenLayers.length - 1].link(outputLayer);
		inputLayer.prepare(hiddenLayers[0], input[0].length);

		if (this.predef != null) {
			evaluatePredef();
		}
	}

	private void evaluatePredef() {
		for (int i = 0; i < predef.length; i++) {
			if (i == 0) {
				inputLayer.bias = predef[i][0][0];
			} else {
				if (hiddenLayers.length >= i)
					hiddenLayers[i - 1].bias = predef[i][0][0];
			}
			for (int o = 1; o < predef[i].length; o++) {
				for (int e = 0; e < predef[i][o].length; e++) {
					if (i == 0) {
						inputLayer.neurons[o - 1].sinapsis[e].weight = predef[i][o][e];
					} else {
						hiddenLayers[i - 1].neurons[o - 1].sinapsis[e].weight = predef[i][o][e];
					}
				}
			}
		}
		outputLayer.bias = predef[predef.length - 1][predef[predef.length - 1].length
				- 1][predef[predef.length - 1][predef[predef.length - 1].length - 1].length - 1];
	}

	private void train(double d) {
		double eval = 0;
		double error = 99999999;
		double maxerror = 99999999;
		while (Math.abs(maxerror) > d) {
			error = 0;
			maxerror = -99999;
			eval++;
			for (int i = 0; i < input.length; i++) {
				inputLayer.setInputs(input[i]);
				inputLayer.activate();

				for (int o = 0; o < hiddenLayersNumber; o++) {
					HiddenLayer hidden = hiddenLayers[o];
					hidden.applyBias();
					hidden.activate();
				}
				outputLayer.applyBias();
				outputLayer.activate();
				error = outputLayer.calculateTotalError(i);
				if (error > maxerror) {
					maxerror = error;
				}
				for (int o = hiddenLayersNumber - 1; o >= 0; o--) {
					HiddenLayer hidden = hiddenLayers[o];
					if (o == hiddenLayersNumber - 1) {
						hidden.calculateNewWeights(error, outputLayer);
					} else {
						hidden.calculateNewWeights(error, hiddenLayers[o + 1]);
					}
				}
				inputLayer.calculateNewWeights(error, hiddenLayers[0]);
				inputLayer.assignNewWeights();
				for (int o = hiddenLayersNumber - 1; o >= 0; o--) {
					HiddenLayer hidden = hiddenLayers[o];
					hidden.assignNewWeights();
				}

				resetLayers();
			}
			if (eval % 100 == 0) {
				System.out.println(eval + " " + maxerror + " " + outputLayer.neurons[0].out);
			}
		}
	}

	private void resetLayers() {
		for (int i = 0; i < hiddenLayersNumber; i++) {
			hiddenLayers[i].reset();
		}
		outputLayer.reset();
	}
}
