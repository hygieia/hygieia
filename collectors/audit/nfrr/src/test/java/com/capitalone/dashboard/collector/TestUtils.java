package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class TestUtils {

    public static void loadDashBoard(DashboardRepository dashboardRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./dashboard/dashboard.json"));
        Dashboard dashboard = gson.fromJson(json, Dashboard.class);
        dashboardRepository.save(dashboard);
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
        List<CollectorItem> collectorItem = gson.fromJson(json, new TypeToken<List<CollectorItem>>() {
        }.getType());
        collectorItemRepository.save(collectorItem);
    }

}

