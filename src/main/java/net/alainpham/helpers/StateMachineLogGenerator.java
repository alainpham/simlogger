package net.alainpham.helpers;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.alainpham.model.BusinessProcessSimConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class StateMachineLogGenerator {
    
    private static Random random = new Random();
    
    private static List<BusinessProcessSimConfig> BusinessProcessSimConfig = new ArrayList<BusinessProcessSimConfig>();

    static {
        BusinessProcessSimConfig config;

        config = new BusinessProcessSimConfig();
        config.setName("Customer");
        config.setDurationSecondsAverage(10);
        config.setDurationSecondsStdDeviation(2);
        BusinessProcessSimConfig.add(config);

        config = new BusinessProcessSimConfig();
        config.setName("SalesOrders");
        config.setDurationSecondsAverage(10);
        config.setDurationSecondsStdDeviation(2);
        BusinessProcessSimConfig.add(config);

        config = new BusinessProcessSimConfig();
        config.setName("Inventory");
        config.setDurationSecondsAverage(10);
        config.setDurationSecondsStdDeviation(2);
        BusinessProcessSimConfig.add(config);

        config = new BusinessProcessSimConfig();
        config.setName("Payments");
        config.setDurationSecondsAverage(10);
        config.setDurationSecondsStdDeviation(2);
        BusinessProcessSimConfig.add(config);

        config = new BusinessProcessSimConfig();
        config.setName("Reports");
        config.setDurationSecondsAverage(10);
        config.setDurationSecondsStdDeviation(2);
        BusinessProcessSimConfig.add(config);

        config = new BusinessProcessSimConfig();
        config.setName("Reviews");
        config.setDurationSecondsAverage(10);
        config.setDurationSecondsStdDeviation(2);
        BusinessProcessSimConfig.add(config);
    }

 

    public static void main(String[] args) {
        List<String> lines = readFileLines("timestamps.txt");
        for (String line : lines) {
            /* simulate  */
            // ...
            LocalDateTime dateTime = LocalDateTime.parse(line, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
            // continue with the rest of your code
            System.out.println(dateTime);
        }
    }

    public static void simulate(int maxIndex) {
        /* simulate */
        
    }

    public static List<String> readFileLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    // Function to generate random integers from Pareto distribution
    public static double generateParetoRandom(int scale, double shape, Random random) {

        // Generate a random value from a Pareto distribution
        double paretoValue = scale / Math.pow(random.nextDouble(), 1.0 / shape);
        return paretoValue;
    }
}
