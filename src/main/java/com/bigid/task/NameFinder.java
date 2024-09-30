package com.bigid.task;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class NameFinder {

    private static final Logger LOGGER = Logger.getLogger(NameFinder.class.getName());

    private static final ConcurrentHashMap<String, List<Location>> aggregatedResults = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        try {
            ClassLoader classLoader = NameFinder.class.getClassLoader();
            Path filePath = Paths.get(Objects.requireNonNull(classLoader.getResource("big.txt")).toURI());
            int batchSize = 1000;

            ExecutorService executor = Executors.newFixedThreadPool(10);

            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                List<String> batch = new ArrayList<>();
                String line;
                int currentLineOffset = 0;

                while ((line = reader.readLine()) != null) {
                    batch.add(line);
                    if (batch.size() == batchSize) {
                        String batchText = String.join("\n", batch);
                        executor.submit(new NameMatcher(batchText, currentLineOffset, aggregatedResults));
                        currentLineOffset += batch.size();
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    String batchText = String.join("\n", batch);
                    executor.submit(new NameMatcher(batchText, currentLineOffset, aggregatedResults));
                }
            } catch (Exception e) {
                LOGGER.severe("Error occurred while reading and processing the file");
                throw new RuntimeException(e);
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);

            ResultAggregator aggregator = new ResultAggregator(aggregatedResults);
            aggregator.printResults();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Error occurred while processing the file");
        }
    }
}
