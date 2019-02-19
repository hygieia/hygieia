package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Sprint;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SprintFormatterTest {

    private static final Gson gson = GsonUtil.getGson();
    private static final JSONParser parser = new JSONParser();

    private static JSONArray getJsonArray(List<String> list) throws ParseException {
        String toString = gson.toJson(list);
        return (JSONArray) parser.parse(toString);
    }

    @Test
    public void parseSprintNullInput() {
        Sprint sprint = SprintFormatter.parseSprint(null);
        assertNull(sprint);
    }

    @Test
    public void parseSprintNullString() throws ParseException {
        Sprint sprint = SprintFormatter.parseSprint(getJsonArray(Collections.EMPTY_LIST));
        assertNull(sprint);
    }

    @Test
    public void parseSprintEmptyString() {
        Sprint sprint = SprintFormatter.parseSprint(null);
        assertNull(sprint);
    }

    @Test
    public void parseSprintRandomString() throws ParseException {
        Sprint sprint = SprintFormatter.parseSprint(getJsonArray(Arrays.asList("test")));
        assertNull(sprint);
    }

    @Test
    public void parseSprintRandomStringMatchingRegEx() throws ParseException {
        Sprint sprint = SprintFormatter.parseSprint(getJsonArray(Arrays.asList("test[test]")));
        assertNull(sprint);
    }

    @Test
    public void parseSprintAnotherRandomStringMatchingRegEx() throws ParseException {
        Sprint sprint = SprintFormatter.parseSprint(getJsonArray(Arrays.asList("test[this=that")));
        assertNull(sprint);
    }


    @Test
    public void parseSprintGoodData() throws ParseException {
        String string = "com.atlassian.greenhopper.service.sprint.Sprint@57910277[id=29200,rapidViewId=8070,state=CLOSED,name=SPrUCE - Sprint 15,startDate=2018-04-19T10:45:31.067-04:00,endDate=2018-05-02T14:04:00.000-04:00,completeDate=2018-05-03T11:08:13.295-04:00,sequence=29200,goal=Spruce 100%!]";

//        "com.atlassian.greenhopper.service.sprint.Sprint@57910277[id=29200,rapidViewId=8070,state=CLOSED,name=SPrUCE - Sprint 15,startDate=2018-04-19T10:45:31.067-04:00,endDate=2018-05-02T14:04:00.000-04:00,completeDate=2018-05-03T11:08:13.295-04:00,sequence=29200,goal=Spruce 100%!]",
//                "com.atlassian.greenhopper.service.sprint.Sprint@589f7520[id=29201,rapidViewId=8070,state=CLOSED,name=SPrUCE - Sprint 16,startDate=2018-05-03T11:46:29.710-04:00,endDate=2018-05-16T15:05:00.000-04:00,completeDate=2018-05-17T10:36:19.487-04:00,sequence=29201,goal=]"
//                ],
        Sprint sprint = SprintFormatter.parseSprint(getJsonArray(Arrays.asList(string)));
        assertNotNull(sprint);
        assertEquals(sprint.getId(), "29200");

    }


}
