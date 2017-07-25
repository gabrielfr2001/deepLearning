package br.com.roskowski.model;

import java.util.Random;

public class Learner {
	private static Display display;
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
		double[][] input = { { 0.01, 0.05 }};
		double[][] target = { { 0.99,0.01 } };
		// double predef[][][] = { { { 0 }, { .15, .25 }, { .20, .30 } }, { {
		// .35 }, { .40, .50 }, { .45, .55 } },
		// { { .60 } } };
		final Learner learner = new Learner(input, target);
		learner.hiddenLayersNumber = 2;
		learner.hiddenLayersSize = 2;
		learner.prepare();
		display = new Display(600, 600, "IA");
		display.setLearner(learner);

		Thread thread = new Thread(new ThreadHolder(learner, 1E-40));
		thread.run();

		display.setLearner(learner);
		System.out.println(9);
	}

	void trainInThread(double d, CallBack callBack) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				train(d);
				callBack.callback();
			}
		});
		t.run();
	}

	double[] test(double[] test) {
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
				hiddenLayers[i].prepare(f,hiddenLayersSize);
				hiddenLayers[i - 1].link(hiddenLayers[i]);
			} else if (i == hiddenLayersNumber - 1) {
				hiddenLayers[i] = new HiddenLayer(hiddenLayersSize);
				hiddenLayers[i].prepare(f,hiddenLayersSize);
			} else {
				hiddenLayers[i] = new HiddenLayer(hiddenLayersSize);
				hiddenLayers[i].prepare(f,hiddenLayersSize);
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
				error += outputLayer.calculateTotalError(i) / target.length;
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
			if (eval % 10000 == 0) {
				System.out.println(eval + " " + error + " " + outputLayer.neurons[0].out);
				display.learner = this;
			}
			//try {
			//	Thread.sleep(1);
			//} catch (InterruptedException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
	//		}
		}
	}

	private void resetLayers() {
		for (int i = 0; i < hiddenLayersNumber; i++) {
			hiddenLayers[i].reset();
		}
		outputLayer.reset();
	}
}
