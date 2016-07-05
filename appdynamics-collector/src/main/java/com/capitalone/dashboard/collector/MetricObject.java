package com.capitalone;

import org.apache.commons.lang3.StringUtils;
import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.data.Application;
import org.appdynamics.appdrestapi.data.MetricData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by on 6/29/2016.
 */
public class MetricObject {

    private final int NUM_MINUTES = 60;
    private final double DEFAULT_VALUE = -1.0;
    private final String METRIC_FILEPATH = "src\\main\\java\\com\\capitalone\\metrics.txt";
    Map<String, Double> metricDataMap;

    public Map<String, Double> getMetricDataMap() {
        return metricDataMap;
    }

    private RESTAccess getAccess() {
        final String controller = "appdyn-hqa-c01";
        final String port = "80";
        final String user = "";
        final String passwd = "";
        final String account = "customer1";
        final boolean useSSL = false;

        return new RESTAccess(controller, port, useSSL, user, passwd, account);
    }

    private void buildMap() throws IOException {
        FileReader reader = new FileReader(METRIC_FILEPATH);
        BufferedReader bufferedReader = new BufferedReader(reader);

        metricDataMap = new HashMap<String, Double>();

        String line;
        while ((line = bufferedReader.readLine()) != null)
            metricDataMap.put(line, DEFAULT_VALUE);

        reader.close();
    }


    public MetricObject initialize(String appIdentifier) {

        try {
            buildMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RESTAccess access = getAccess();

        String appName = StringUtils.isNumeric(appIdentifier) ?
                getAppName(access.getApplications().getApplications(), appIdentifier) :
                appIdentifier;


        try {
            populateFields(appName, access);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return this;
    }


    private String getAppName(ArrayList<Application> apps, String appIdentifier) {


        int appID = Integer.valueOf(appIdentifier);

        if (apps == null) {
            System.out.println("Something went wrong because getting applications should be easy!");
            System.exit(1);
        }

        for (Application app : apps) {
            if (app.getId() == appID) {
                return app.getName();
            }
        }

        System.out.println("Could not find application with ID: " + appID + ".");
        System.exit(1);
        return null;
    }


    private void populateFields(String applName, RESTAccess access) throws IllegalAccessException {

        ArrayList<String> unknownMetrics = new ArrayList<>();

        for (Map.Entry<String, Double> entry : metricDataMap.entrySet()) {
            String metricName = entry.getKey();

            if (metricName.contains("Total")) {
                unknownMetrics.add(metricName);
                continue;
            }

            entry.setValue(getMetricValue(applName, createPath(metricName), access));
        }

        for (String metricName : unknownMetrics)
            metricDataMap.replace(metricName, generateMetricValue(metricName));

        testInit();
    }


    private double getMetricValue(String applName, String metricPath, RESTAccess access) throws IllegalAccessException {

        Calendar cal = Calendar.getInstance();
        long end = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -NUM_MINUTES);
        long start = cal.getTimeInMillis();

        ArrayList<MetricData> metricDataArr = access.getRESTGenericMetricQuery(applName, metricPath, start, end, true).getMetric_data();

        if (metricDataArr.size() > 0)
            return metricDataArr.get(0).getSingleValue().getValue();
        else
            return -1;
    }

    private double generateMetricValue(String metricName) throws IllegalAccessException {
        //only implemented total
        return NUM_MINUTES * metricDataMap.get(totalToPerMinute(metricName));
    }

    private String totalToPerMinute(String currField) {
        return currField.replace("Total ", "") + " per Minute";
    }


    private String createPath(String currMember) {
        return "Overall Application Performance|" + currMember.replace("OPEN", "(").replace("CLOSE", ")");
    }

    private void testInit() throws IllegalAccessException {
        metricDataMap.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
