package br.com.roskowski.util;

import java.util.Random;

public class Functions {
	final static int SIZE = 2;

	public static double binaryStep(double d) {
		return d > 0 ? 1 : 0;
	}

	public static double logistic(double d) {
		return 1 / (1 + Math.exp(-d));
	}

	public static int rendomFunction() {
		return new Random().nextInt(SIZE);
	}

	public static double randomWeight(double min, double max) {
		return new Random().nextInt((int) ((max - min) * 10000)) / 10000f + min;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static double leckyPreLU(double d) {
		return d > 0 ? d : d * 2;
	}

	public static double SELU(double d) {
		return d * d;
	}
}
