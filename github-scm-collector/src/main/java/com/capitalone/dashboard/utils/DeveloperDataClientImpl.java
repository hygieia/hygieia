package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.collector.DeveloperDataClient;
import com.capitalone.dashboard.collector.DeveloperDataSettings;
import com.capitalone.dashboard.model.Developer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by ltz038 on 5/8/16.
 */
public class DeveloperDataClientImpl implements DeveloperDataClient {
    // This is Capital One Specific Implementation - including this to serve as an example
    // User Id of the Developer is passed in and what ever props you need to GET people data
    // can be passed in in a HashMap. Let's say your people data is in a database, you can pass
    // in DB urls/credentials etc. If there are REST end points, you need this - so save them in
    // YML file and have all your GIT controllers pick them from YML and pass here

    private static final Log LOG = LogFactory.getLog(DeveloperDataClientImpl.class);

    public Developer getDeveloper(String userId, DeveloperDataSettings keys) {
        try {
            if (!validUser(userId)) {
                //LOG.error("Not valid user : " + userId);
                return null;
            }
            //LOG.error("getDeveloper called for =[" + userId + "]");
            HashMap<String, String> peopleData = new HashMap<>();
            peopleData.put("URL",keys.getUrl());
            peopleData.put("password",keys.getPassword());
            peopleData.put("impersonateUserId",keys.getImpersonateUserId());

            Developer dev = getDeveloperData(userId, peopleData);
            setManagers(dev, peopleData);
            return dev;
        }
        catch (Exception e)
        {
            LOG.error(e);
        }
        return null;
    }

    private static void setManagers(Developer dev, Map keys) {
        //LOG.error("setDeveloper called for =[" + dev.getUserId() + "]");

        String currMgr = dev.getManager();
        //Job levels - don't go beyond 7 - you should be able to find by then
        //Again - this is Capital One Specific Implementation - Use as an example
        int counter = 0;
        try {
            while (dev.getLevelTwoMgr() == null && counter < 7) {
                Developer mgr = getDeveloperData(currMgr, keys);
                //if (mgr.getJobLevel().equals())
                int level = Integer.valueOf(mgr.getJobLevel());
                /*if (level < 40) {
                    level = 40;
                    dev.setJobLevel("40");
                }*/
                if (level == 65)
                    dev.setLevelOneMgr(mgr.getName());

                if (level > 65) {
                    dev.setLevelTwoMgr(mgr.getName());
                    dev.setDepartmentname(mgr.getDepartmentname());
                }
                currMgr = mgr.getManager();
                counter++;
            }
            if ((dev.getLevelOneMgr() == null) && (dev.getLevelTwoMgr() != null)) {
                dev.setLevelOneMgr(dev.getLevelTwoMgr());
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }
    private static Developer getDeveloperData(String userId, Map keys) throws Exception {
        // The idea is to have two levels of leadership identified or two Kinds of leadership etc
        // Don't go beyond Two b/c the views on Widgets will be bound to TWO.
        HttpURLConnection connection = null;
        Developer dev = new Developer();
        try {
            String peopleDataUrl =  (String )keys.get("URL");
            String userpassword = (String )keys.get("password");
            String impersonateUserId = (String )keys.get("impersonateUserId");

            URL url = new URL(peopleDataUrl  + userId);
            //BASE64Encoder enc = new BASE64Encoder();
            //String encodedCredentials = enc.encode(userpassword.getBytes());
            String encodedCredentials = Base64.getEncoder().encodeToString(userpassword.getBytes());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            // Set http header. In line with Basic Authorization.
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedCredentials);
            connection.setRequestProperty("X-Pulse-Run-As", impersonateUserId);
            if (!connection.getResponseMessage().equals("OK"))
                throw new Exception("Exception encountered accessing People REST apis ");

            // Get and process the response.
            StringBuilder sb = new StringBuilder();

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONParser parser = new JSONParser();
            String response = sb.substring(0);
            response = response.replace("throw \'allowIllegalResourceCall is false.\';", "");

            JSONObject jsonObject = (JSONObject) parser.parse(response);
            String devName = (String) jsonObject.get("displayName");
            dev.setName(devName);
            dev.setUserId(userId);

            JSONObject jiveData = (JSONObject) jsonObject.get("jive");
            JSONArray profile = (JSONArray) jiveData.get("profile");
            Iterator itr = profile.iterator();
            while (itr.hasNext()) {
                JSONObject featureJsonObj = (JSONObject) itr.next();
                String key = (String) featureJsonObj.get("jive_label");
                if (key.equals("Manager")) {
                    dev.setManager((String) featureJsonObj.get("value"));
                } else if (key.equals("Manager Level")) {
                    String curLevel = (String) featureJsonObj.get("value");
                    dev.setJobLevel(curLevel);
                    int level = Integer.valueOf(curLevel);
                    /*if (level < 40) {
                        level = 40;
                        dev.setJobLevel("40");
                    }*/
                    if (level >= 65 && level < 70)
                        dev.setLevelOneMgr(devName);
                    if (level >= 70)
                        dev.setLevelTwoMgr(devName);
                }
                else if (key.equals("Department")) {
                    dev.setDepartmentname((String) featureJsonObj.get("value"));
                } else if (key.equals("DepartmentID")) {
                    dev.setDepartmentId((String) featureJsonObj.get("value"));
                }
                if ((dev.getLevelOneMgr() == null) && (dev.getLevelTwoMgr() != null))
                    dev.setLevelOneMgr(dev.getLevelTwoMgr());
            }
        }
        catch (Exception e)
        {
            LOG.error(e);
            throw e;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return dev;
    }
    private boolean validUser(String id){
        String lowerCasePattern = "[a-z][a-z][a-z][0-9][0-9][0-9]";
        Pattern lowPattern = Pattern.compile(lowerCasePattern);
        java.util.regex.Matcher low = lowPattern.matcher(id.toLowerCase(Locale.ENGLISH));
        return low.find();
    }
}
