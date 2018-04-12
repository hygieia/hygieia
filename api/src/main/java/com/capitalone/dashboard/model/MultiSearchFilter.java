package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiSearchFilter {
    private String combinedSearchField;
    private String searchKey;
    private String advancedSearchKey;

    public MultiSearchFilter(String combinedSearchField) {
        this.combinedSearchField = combinedSearchField;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public String getAdvancedSearchKey() {
        return advancedSearchKey;
    }

    public MultiSearchFilter invoke() {
        searchKey = "";
        advancedSearchKey = "";
        List<String> l = getSearchKeyAndAdvancedSearchKey(combinedSearchField);
        if (!l.isEmpty()) {
            advancedSearchKey = l.get(0).trim();
            if (l.size() > 1) {
                searchKey = findIndex(combinedSearchField);
            }
        }
        return this;
    }

    private List<String> getSearchKeyAndAdvancedSearchKey(String combinedSearchField) {
        if (combinedSearchField.contains(":"))
            return Stream.of(combinedSearchField.split(":")).map(String::trim)
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }


    private static String findIndex(String combinedSearchField) {
        return combinedSearchField.substring(combinedSearchField.indexOf(":") + 1, combinedSearchField.length()).trim();

    }


}