package com.capitalone.dashboard.util;
import com.capitalone.dashboard.mapper.CustomObjectMapper;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.springframework.http.MediaType.*;

public class TestUtil {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(APPLICATION_JSON.getType(), APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static void loadDashBoard(DashboardRepository dashboardRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./dashboard/dashboard.json"));
        Dashboard dashboard = gson.fromJson(json, Dashboard.class);
        dashboardRepository.save(dashboard);
    }
    public static void loadUserInfo(UserInfoRepository userInfoRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./userInfo/user.json"));
        UserInfo[] usersInfo = gson.fromJson(json, UserInfo[].class);
        for (UserInfo user : usersInfo) {
            userInfoRepository.save(user);
        }
    }
    public static void loadCollector(CollectorRepository collectorRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./collectors/coll.json"));
        List<Collector> collector = gson.fromJson(json, new TypeToken<List<Collector>>() {
        }.getType());
        collectorRepository.save(collector);
    }
    public static void loadComponent(ComponentRepository componentRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./component/component.json"));
        Component component = gson.fromJson(json, Component.class);
        componentRepository.save(component);
    }
    public static void loadCollectorItems(CollectorItemRepository collectorItemRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./collector_items/items.json"));
        List<CollectorItem> collectorItem = gson.fromJson(json, new TypeToken<List<CollectorItem>>(){}.getType());
        collectorItemRepository.save(collectorItem);
    }
}