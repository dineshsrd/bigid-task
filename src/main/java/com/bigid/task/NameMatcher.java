package com.bigid.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameMatcher implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(NameMatcher.class.getName());

    private static final List<String> NAMES = Arrays.asList("James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth", "Steven", "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric", "Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick", "Peter", "Harold", "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger");

    private final String text;
    private final int lineOffset;
    private final ConcurrentHashMap<String, List<Location>> aggregatedResults;

    NameMatcher(String text, int lineOffset, ConcurrentHashMap<String, List<Location>> aggregatedResults) {
        this.text = text;
        this.lineOffset = lineOffset;
        this.aggregatedResults = aggregatedResults;
    }

    @Override
    public void run() {
        Map<String, List<Location>> results = new HashMap<>();
        String[] lines = text.split("\n");

        for (String name : NAMES) {
            List<Location> locations = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\b" + name + "\\b");

            for (int i = 0; i < lines.length; i++) {
                Matcher matcher = pattern.matcher(lines[i]);
                while (matcher.find()) {
                    locations.add(new Location(lineOffset + i, matcher.start()));
                }
            }
            if (!locations.isEmpty()) {
                results.put(name, locations);
            }
        }

        results.forEach((name, locations) -> aggregatedResults.merge(
                name, locations, (oldList, newList) -> { oldList.addAll(newList); return oldList; }
        ));
        LOGGER.info("Processed between " + (lineOffset-1000) + " and "+ lineOffset +" lines");
    }
}