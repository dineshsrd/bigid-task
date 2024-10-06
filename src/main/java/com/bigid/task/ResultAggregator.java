package com.bigid.task;

import java.util.List;
import java.util.Map;

public class ResultAggregator {
    private final Map<String, List<Location>> results;

    ResultAggregator(Map<String, List<Location>> results) {
        this.results = results;
    }

    void printResults() {
        results.forEach((name, locations) -> {
            System.out.print(name + " --> ");
            System.out.println(locations);
        });
    }
}