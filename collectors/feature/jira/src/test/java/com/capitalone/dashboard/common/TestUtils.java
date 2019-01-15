package com.capitalone.dashboard.common;

import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class TestUtils {
    public static void loadCollectorFeature(FeatureCollectorRepository featureCollectorRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./collectors/featureCollector.json"));
        FeatureCollector feature = gson.fromJson(json, new TypeToken<FeatureCollector>(){}.getType());
        featureCollectorRepository.save(feature);
    }
    public static void loadFeature(FeatureRepository featureRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./feature/feature.json"));
        List<Feature> feature = gson.fromJson(json, new TypeToken<List<Feature>>(){}.getType());
        featureRepository.save(feature);
    }
    public static void loadTeams(TeamRepository teamRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./team/team.json"));
        List<Team> team = gson.fromJson(json, new TypeToken<List<Team>>(){}.getType());
        teamRepository.save(team);
    }
    public static void loadScope(ScopeRepository projectRepository) throws IOException {
        Gson gson = GsonUtil.getGson();
        String json = IOUtils.toString(Resources.getResource("./scope/scope.json"));
        List<Scope> team = gson.fromJson(json, new TypeToken<List<Scope>>() {
        }.getType());
        projectRepository.save(team);
    }
}
