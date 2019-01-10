package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Sprint;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class SprintFormatter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraClient.class);
    static Sprint parseSprint(JSONArray customArray) {
        if (CollectionUtils.isEmpty(customArray)) return null;
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        List<Sprint> sprints = new ArrayList<>();

        for (Object c : customArray) {
            Matcher matcher = pattern.matcher((String) c);
            while (matcher.find()) {
                String text = matcher.group(1);
                Map<String, String> sprintMap = Arrays.stream(text.split(","))
                        .map(s -> s.split("="))
                        .filter(s -> s.length == 2)
                        .collect(toMap(s -> s[0], s -> s[1]));

                if (MapUtils.isEmpty(sprintMap)) {
                    return null;
                }

                Sprint sprint = new Sprint();
                sprint.setId(MapUtils.getString(sprintMap, "id", ""));
                sprint.setName(MapUtils.getString(sprintMap, "name", ""));
                sprint.setRapidViewId(MapUtils.getString(sprintMap, "rapidView", ""));
                sprint.setStartDateStr(MapUtils.getString(sprintMap, "startDate", ""));
                if (StringUtils.isEmpty(sprint.getStartDateStr()) || sprint.getStartDateStr().equalsIgnoreCase("<null>")) {
                    LOGGER.info("ERROR: Sprint start date is bad. Sprint ID=" + sprint.getRapidViewId());
                    continue;
                }
                sprint.setState(MapUtils.getString(sprintMap, "state", ""));
                sprint.setSequence(MapUtils.getString(sprintMap, "sequence", ""));
                sprint.setEndDateStr(MapUtils.getString(sprintMap, "endDate", ""));
                sprints.add(sprint);
            }
        }
        if (CollectionUtils.isEmpty(sprints)) return null;

        //Sort by date and take the latest sprint.
        sprints = sprints.stream().sorted(Comparator.comparing(s -> LocalDateTime.parse(s.getStartDateStr(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))).collect(Collectors.toList());

        return sprints.get(sprints.size() - 1);
    }


}
