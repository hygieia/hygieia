package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildSearch {
    private String descriptionFilter;
    private String niceName;
    private String jobName;

    public BuildSearch(String descriptionFilter) {
        this.descriptionFilter = descriptionFilter;
    }

    public String getNiceName() {
        return niceName;
    }

    public String getJobName() {
        return jobName;
    }

    public BuildSearch invoke() {
        niceName = "";
        jobName = "";
        List<String> l = findJobNameAndNiceName(descriptionFilter);
        if (!l.isEmpty()) {
            niceName = l.get(0).trim();
            if (l.size() > 1) {
                jobName = findIndex(descriptionFilter);
            }
        }
        return this;
    }

    private List<String> findJobNameAndNiceName(String descriptionFilter) {
        if (descriptionFilter.contains(":"))
            return Stream.of(descriptionFilter.split(":")).map(String::trim)
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }


    private static String findIndex(String descriptionFilter) {
        return descriptionFilter.substring(descriptionFilter.indexOf(":") + 1, descriptionFilter.length()).trim();

    }


}