package net.alainpham.helpers;

import java.util.Random;

public class Tester {

    public static void main(String[] args) {
        // Parameters for the Pareto distribution
        int scale = 1; // Scale parameter (also known as the minimum value)
        double shape = 1; // Shape parameter (also known as the Pareto index)

        // Number of samples to generate
        int numberOfSamples = 100;

        // Create a random number generator
        Random random = new Random();

        // Generate random integers from the Pareto distribution
        for (int i = 0; i < numberOfSamples; i++) {
            long randomValue = generateParetoRandom(scale, shape, random);
            System.out.println("Random value " + (i + 1) + ": " + randomValue);
        }
    }


        // Function to generate random integers from Pareto distribution
    public static long generateParetoRandom(int scale, double shape, Random random) {

        // Generate a random value from a Pareto distribution
        double paretoValue = scale / Math.pow(random.nextDouble(), 1.0 / shape);
        return Math.round(paretoValue);
    }
}
