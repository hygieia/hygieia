package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Epic;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureBoard;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.FeatureEpicResult;
import com.capitalone.dashboard.model.JiraMode;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureBoardRepository;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.utils.Utilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Collects {@link FeatureCollector} data from feature content source system.
 *
 * @author KFK884
 */
@Component
public class FeatureCollectorTask extends CollectorTask<FeatureCollector> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureCollectorTask.class);
    private final FeatureRepository featureRepository;
    private final FeatureBoardRepository featureBoardRepository;
    private final TeamRepository teamRepository;
    private final ScopeRepository projectRepository;
    private final FeatureCollectorRepository featureCollectorRepository;
    private final FeatureSettings featureSettings;
    private final JiraClient jiraClient;

    /**
     * Default constructor for the collector task. This will construct this
     * collector task with all repository, scheduling, and settings
     * configurations custom to this collector.
     *
     * @param taskScheduler   A task scheduler artifact
     * @param teamRepository  The repository being use for feature collection
     * @param featureSettings The settings being used for feature collection from the source
     *                        system
     */
    @Autowired
    public FeatureCollectorTask(TaskScheduler taskScheduler, FeatureRepository featureRepository,
                                TeamRepository teamRepository, ScopeRepository projectRepository,
                                FeatureCollectorRepository featureCollectorRepository, FeatureSettings featureSettings,
                                JiraClient jiraClient, FeatureBoardRepository featureBoardRepository) {
        super(taskScheduler, FeatureCollectorConstants.JIRA);
        this.featureCollectorRepository = featureCollectorRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.featureRepository = featureRepository;
        this.featureSettings = featureSettings;
        this.jiraClient = jiraClient;
        this.featureBoardRepository = featureBoardRepository;
    }

    /**
     * Accessor method for the collector prototype object
     */
    @Override
    public FeatureCollector getCollector() {
        JiraMode mode = featureSettings.isJiraBoardAsTeam() ? JiraMode.Board : JiraMode.Team;
        FeatureCollector collector = FeatureCollector.prototype(mode);
        FeatureCollector existing = featureCollectorRepository.findByName(collector.getName());
        if (existing != null) {
            collector.setLastRefreshTime(existing.getLastRefreshTime());
        }
        Map<String, String> issueTypeIds = jiraClient.getJiraIssueTypeIds();
        if (!MapUtils.isEmpty(issueTypeIds)){
            collector.getProperties().put("issueTypesMap", issueTypeIds);
        }
        return collector;
    }

    /**
     * Accessor method for the collector repository
     */
    @Override
    public BaseCollectorRepository<FeatureCollector> getCollectorRepository() {
        return featureCollectorRepository;
    }

    /**
     * Accessor method for the current chronology setting, for the scheduler
     */
    @Override
    public String getCron() {
        return featureSettings.getCron();
    }

    /**
     * The collection action. This is the task which will run on a schedule to
     * gather data from the feature content source system and update the
     * repository with retrieved data.
     */
    @Override
    public void collect(FeatureCollector collector) {
        logBanner(featureSettings.getJiraBaseUrl());

        String proxyUrl = featureSettings.getJiraProxyUrl();
        String proxyPort = featureSettings.getJiraProxyPort();

        if (!StringUtils.isEmpty(proxyUrl) && !StringUtils.isEmpty(proxyPort)) {
            System.setProperty("http.proxyHost", proxyUrl);
            System.setProperty("https.proxyHost", proxyUrl);
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("https.proxyPort", proxyPort);
        }

        try {
            long startTime = System.currentTimeMillis();
            long diff = TimeUnit.MILLISECONDS.toHours(startTime - collector.getLastRefreshTime());
            LOGGER.info("JIRA Collector is set to work in " + collector.getMode() + " mode");
            if (diff > featureSettings.getRefreshTeamAndProjectHours()) {
                LOGGER.info("Hours since last run = " + diff + ". Collector is about to refresh Team/Board information");
                List<Team> teams = updateTeamInformation(collector);
                Set<Scope> scopes = updateProjectInformation(collector);
                if (collector.getLastExecuted() > 0) {
                    if (featureSettings.isCollectorItemOnlyUpdate()) {
                        refreshValidIssues(collector, getBoardList(collector.getId()), getScopeList(collector.getId()));
                    } else {
                        refreshValidIssues(collector, teams, scopes);
                    }
                }

                collector.setLastRefreshTime(System.currentTimeMillis());
                featureCollectorRepository.save(collector);
                LOGGER.info("Collected " + teams.size() + " teams and " + scopes.size() + " projects");
            } else {
                LOGGER.info("Hours since last run = " + diff + ". Collector is only collecting updated/new issues.");
            }
            updateStoryInformation(collector);
            log("Finished", startTime);
        } catch (Exception e) {
            // catch exception here so we don't blow up the collector completely
            LOGGER.error("Failed to collect jira information", e);
        }
    }


    /**
     * Update team information
     *
     * @param collector
     * @return list of teams collected
     */
    protected List<Team> updateTeamInformation(FeatureCollector collector) {
        long projectDataStart = System.currentTimeMillis();
        List<Team> teams = featureSettings.isJiraBoardAsTeam() ? jiraClient.getBoards() : jiraClient.getTeams();
        //Add or update teams that we got from api
        teams.forEach(newTeam -> {
            String teamId = newTeam.getTeamId();
            newTeam.setCollectorId(collector.getId());
            LOGGER.info(String.format("Adding %s:%s-%s", collector.getMode(), teamId, newTeam.getName()));
            Team existing = teamRepository.findByTeamId(teamId);
            if (existing == null) {
                teamRepository.save(newTeam);
            } else {
                newTeam.setId(existing.getId());
                teamRepository.save(newTeam);
            }
        });
        log(collector.getMode() + " Data Collected. Added ", projectDataStart, teams.size());
        projectDataStart = System.currentTimeMillis();

        // Delete the ones that are gone from JIRA
        List<Team> existingTeams = teamRepository.findByCollectorId(collector.getId());
        Set<String> newTeamIds = teams.stream().map(Team::getTeamId).collect(Collectors.toSet());
        Set<Team> toDelete = existingTeams.stream().filter(e -> !newTeamIds.contains(e.getTeamId())).collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(toDelete)) {
            toDelete.forEach(td -> {
                LOGGER.info(String.format("Deleting %s:%s-%s", collector.getMode(), td.getTeamId(), td.getName()));
            });
            teamRepository.delete(toDelete);
            log(collector.getMode() + " Data Collected. Deleted ", projectDataStart, toDelete.size());
        }
        return teams;
    }

    /**
     * Update project information
     *
     * @param collector
     * @return List of projects
     */
    protected Set<Scope> updateProjectInformation(Collector collector) {
        long projectDataStart = System.currentTimeMillis();
        //Add or update teams that we got from api
        Set<Scope> projects = jiraClient.getProjects();

        projects.forEach(jiraScope -> {
            LOGGER.info(String.format("Adding :%s-%s", jiraScope.getpId(), jiraScope.getName()));
            jiraScope.setCollectorId(collector.getId());
            Scope existing = projectRepository.findByCollectorIdAndPId(collector.getId(), jiraScope.getpId());
            if (existing == null) {
                projectRepository.save(jiraScope);
            } else {
                jiraScope.setId(existing.getId());
                projectRepository.save(jiraScope);
            }
        });
        log("Project Data Collected", projectDataStart, projects.size());

        // Delete the ones that are gone from JIRA
        List<Scope> existingProjects = projectRepository.findByCollectorId(collector.getId());
        Set<String> newProjectIds = projects.stream().map(Scope::getpId).collect(Collectors.toSet());
        Set<Scope> toDelete = existingProjects.stream().filter(e -> !newProjectIds.contains(e.getpId())).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(toDelete)) {
            toDelete.forEach(td -> {
                LOGGER.info(String.format("Deleting :%s-%s", td.getpId(), td.getName()));
            });
            projectRepository.delete(toDelete);
            log( "Project Data Collected. Deleted ", projectDataStart, toDelete.size());
        }
        return projects;
    }


    /**
     * Update story/feature information for all the projects one at a time
     *
     * @param collector
     */
    protected void updateStoryInformation(FeatureCollector collector) {
        long storyDataStart = System.currentTimeMillis();
        AtomicLong count = new AtomicLong();
        Map<String, String> issueTypesMap = (Map<String, String>)  collector.getProperties().get("issueTypesMap");
        if (Objects.equals(collector.getMode(), JiraMode.Team)) {
            List<Scope> projects = new ArrayList<>(getScopeList(collector.getId()));
            projects.forEach(project -> {
                LOGGER.info("Collecting " + count.incrementAndGet() + " of " + projects.size() + " projects.");

                long lastCollection = System.currentTimeMillis();
                FeatureEpicResult featureEpicResult = jiraClient.getIssues(project, issueTypesMap);
                List<Feature> features = featureEpicResult.getFeatureList();
                saveFeatures(features, collector);
                updateFeaturesWithLatestEpics(featureEpicResult.getEpicList(), collector);
                log("Story Data Collected since " + LocalDateTime.ofInstant(Instant.ofEpochMilli(project.getLastCollected()), ZoneId.systemDefault()), storyDataStart, features.size());

                project.setLastCollected(lastCollection); //set it after everything is successfully done
                projectRepository.save(project);

            });
        } else {
            List<Team> boards = getBoardList(collector.getId());
            boards.forEach(board -> {
                LOGGER.info("Collecting " + count.incrementAndGet() + " of " + boards.size() + " boards.");
                long lastCollection = System.currentTimeMillis();
                FeatureEpicResult featureEpicResult = jiraClient.getIssues(board, issueTypesMap);
                List<Feature> features = featureEpicResult.getFeatureList();
                saveFeatures(features, collector);
                updateFeaturesWithLatestEpics(featureEpicResult.getEpicList(), collector);
                log("Story Data Collected since " + LocalDateTime.ofInstant(Instant.ofEpochMilli(board.getLastCollected()), ZoneId.systemDefault()), storyDataStart, features.size());

                board.setLastCollected(lastCollection); //set it after everything is successfully done
                teamRepository.save(board);
                FeatureBoard featureBoard = featureBoardRepository.findFeatureBoard(collector.getId(), board.getTeamId());
                if(featureBoard != null){
                     featureBoard.setLastUpdated(System.currentTimeMillis());
                     featureBoardRepository.save(featureBoard);
                }
            });
        }

    }

    /**
     * Returns a full team list or partial based on whether or not isCollectorItemOnlyUpdate is true
     * @param collectorId
     * @return
     */
    private List<Team> getBoardList(ObjectId collectorId) {
        List<Team> boards;
        if(featureSettings.isCollectorItemOnlyUpdate()){
            Set<Team> uniqueTeams = new HashSet<>();
            for(FeatureBoard featureBoard: enabledFeatureBoards(collectorId)){
                Team team = teamRepository.findByTeamId(featureBoard.getTeamId());
                if(team != null){
                    uniqueTeams.add(team);
                }
            }

            boards = new ArrayList<>(uniqueTeams);
        }else {
            boards = teamRepository.findByCollectorId(collectorId);
        }
        return boards;
    }
    /**
     * Returns a full project list or partial based on whether or not isCollectorItemOnlyUpdate is true
     * @param collectorId
     * @return
     */
    private Set<Scope> getScopeList(ObjectId collectorId) {
        Set<Scope> projects = new HashSet<>();
        if(featureSettings.isCollectorItemOnlyUpdate()){
            for(FeatureBoard featureBoard: enabledFeatureBoards(collectorId)){
                Scope scope = projectRepository.findByCollectorIdAndPId(collectorId, featureBoard.getProjectId());
                if(scope != null){
                    projects.add(scope);
                }
            }
        }else {
            projects = new HashSet<>(projectRepository.findByCollectorId(collectorId));
        }
        return projects;
    }

    private List<FeatureBoard> enabledFeatureBoards(ObjectId collectorId) {
        return featureBoardRepository.findEnabledFeatureBoards(collectorId);
    }
    /**
     * Save features to repository
     *
     * @param features
     * @param collector
     */

    private void saveFeatures(List<Feature> features, FeatureCollector collector) {
        features.forEach(f -> {
            f.setCollectorId(collector.getId());
            Feature existing = featureRepository.findByCollectorIdAndSIdAndSTeamID(collector.getId(), f.getsId(), f.getsTeamID());
            if (existing != null) {
                f.setId(existing.getId());
            }
            featureRepository.save(f);
        });
    }


    /**
     * Update all features with the latest Epic Information, if any.
     *
     * @param epicList
     * @param collector
     */
    private void updateFeaturesWithLatestEpics(List<Epic> epicList, FeatureCollector collector) {
        epicList.stream().filter(Epic::isRecentUpdate).forEach(e -> {
            List<Feature> existing = featureRepository.findAllByCollectorIdAndSEpicID(collector.getId(), e.getId());
            existing.stream().filter(ex -> isEpicChanged(ex, e)).forEach(ex -> {
                ex.setsEpicAssetState(e.getStatus());
                ex.setsEpicName(e.getName());
                featureRepository.save(ex);
            });
        });
    }

    /**
     * Get a list of all issue ids for a given board or project and delete ones that are not in JIRA anymore
     *
     * @param collector
     * @param teams
     * @param scopes
     */
    private void refreshValidIssues(FeatureCollector collector, List<Team> teams, Set<Scope> scopes) {
        long refreshValidIssuesStart = System.currentTimeMillis();
        List<String> lookUpIds = Objects.equals(collector.getMode(), JiraMode.Board) ? teams.stream().map(Team::getTeamId).collect(Collectors.toList()) : scopes.stream().map(Scope::getpId).collect(Collectors.toList());
        lookUpIds.forEach(l -> {
            LOGGER.info("Refreshing issues for " + collector.getMode() + " ID:" + l);
            List<String> issueIds = jiraClient.getAllIssueIds(l, collector.getMode());
            List<Feature> existingFeatures = Objects.equals(collector.getMode(), JiraMode.Board) ? featureRepository.findAllByCollectorIdAndSTeamID(collector.getId(), l) : featureRepository.findAllByCollectorIdAndSProjectID(collector.getId(), l);
            List<Feature> deletedFeatures = existingFeatures.stream().filter(e -> !issueIds.contains(e.getsId())).collect(Collectors.toList());
            deletedFeatures.forEach(d -> {
                LOGGER.info("Deleting Feature " + d.getsId() + ':' + d.getsName());
                featureRepository.delete(d);
            });
        });
        log(collector.getMode() + " Issues Refreshed ", refreshValidIssuesStart);
    }


    // Checks if epic information on a feature needs update
    private static boolean isEpicChanged(Feature feature, Epic epic) {
        if (!feature.getsEpicAssetState().equalsIgnoreCase(epic.getStatus())) {
            return true;
        }
        if (!feature.getsEpicName().equalsIgnoreCase(epic.getName()) || !feature.getsEpicNumber().equalsIgnoreCase(epic.getNumber())) {
            return true;
        }

        if (!StringUtils.isEmpty(feature.getChangeDate()) && !StringUtils.isEmpty(epic.getChangeDate()) &&
                !Objects.equals(Utilities.parseDateWithoutFraction(feature.getChangeDate()), Utilities.parseDateWithoutFraction(epic.getChangeDate()))) {
            return true;
        }
        if (!StringUtils.isEmpty(feature.getsEpicBeginDate()) && !StringUtils.isEmpty(epic.getBeginDate()) &&
                !Objects.equals(Utilities.parseDateWithoutFraction(feature.getsEpicBeginDate()), Utilities.parseDateWithoutFraction(epic.getBeginDate()))) {
            return true;
        }
        return !StringUtils.isEmpty(feature.getsEpicEndDate()) && !StringUtils.isEmpty(epic.getEndDate()) &&
                !Objects.equals(Utilities.parseDateWithoutFraction(feature.getsEpicEndDate()), Utilities.parseDateWithoutFraction(epic.getEndDate()));
    }
}
