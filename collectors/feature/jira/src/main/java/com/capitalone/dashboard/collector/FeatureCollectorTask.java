package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Epic;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.FeatureEpicResult;
import com.capitalone.dashboard.model.JiraMode;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects {@link FeatureCollector} data from feature content source system.
 *
 * @author KFK884
 */
@Component
public class FeatureCollectorTask extends CollectorTask<FeatureCollector> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureCollectorTask.class);
    private final FeatureRepository featureRepository;
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
                                JiraClient jiraClient) {
        super(taskScheduler, FeatureCollectorConstants.JIRA);
        this.featureCollectorRepository = featureCollectorRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.featureRepository = featureRepository;
        this.featureSettings = featureSettings;
        this.jiraClient = jiraClient;
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
        teams.forEach(newTeam -> {
            String teamId = newTeam.getTeamId();
            newTeam.setCollectorId(collector.getId());
            Team existing = teamRepository.findByTeamId(teamId);
            if (existing == null) {
                teamRepository.save(newTeam);
            } else {
                if (!Objects.equals(existing.getName(), newTeam.getName()) ||
                        !Objects.equals(existing.getTeamType(), newTeam.getTeamType()) ||
                        !Objects.equals(existing.getIsDeleted(), newTeam.getIsDeleted())) {
                    newTeam.setId(existing.getId());
                    teamRepository.save(newTeam);
                }
            }
        });
        log(collector.getMode() + " Data Collected", projectDataStart, teams.size());
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
        Set<Scope> projects = jiraClient.getProjects();

        projects.forEach(jiraScope -> {
            jiraScope.setCollectorId(collector.getId());
            Scope existing = projectRepository.findByCollectorIdAndPId(collector.getId(), jiraScope.getpId());
            if (existing == null) {
                projectRepository.save(jiraScope);
            } else {
                if (!Objects.equals(existing, jiraScope)) {
                    jiraScope.setId(existing.getId());
                    projectRepository.save(jiraScope);
                }
            }
        });
        log("Project Data Collected", projectDataStart, projects.size());
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

        if (Objects.equals(collector.getMode(), JiraMode.Team)) {
            List<Scope> projects = projectRepository.findByCollectorId(collector.getId());
            projects.forEach(project -> {
                LOGGER.info("Collecting " + count.incrementAndGet() + " of " + projects.size() + " projects.");

                long lastCollection = System.currentTimeMillis();
                FeatureEpicResult featureEpicResult = jiraClient.getIssues(project);
                List<Feature> features = featureEpicResult.getFeatureList();
                saveFeatures(features, collector);
                updateFeaturesWithLatestEpics(featureEpicResult.getEpicList(), collector);
                log("Story Data Collected since " + LocalDateTime.ofInstant(Instant.ofEpochMilli(project.getLastCollected()), ZoneId.systemDefault()), storyDataStart, features.size());

                project.setLastCollected(lastCollection); //set it after everything is successfully done
                projectRepository.save(project);
            });
        } else {
            List<Team> boards = teamRepository.findByCollectorId(collector.getId());
            boards.forEach(board -> {
                LOGGER.info("Collecting " + count.incrementAndGet() + " of " + boards.size() + " boards.");
                long lastCollection = System.currentTimeMillis();
                FeatureEpicResult featureEpicResult = jiraClient.getIssues(board);
                List<Feature> features = featureEpicResult.getFeatureList();
                saveFeatures(features, collector);
                updateFeaturesWithLatestEpics(featureEpicResult.getEpicList(), collector);
                log("Story Data Collected since " + LocalDateTime.ofInstant(Instant.ofEpochMilli(board.getLastCollected()), ZoneId.systemDefault()), storyDataStart, features.size());

                board.setLastCollected(lastCollection); //set it after everything is successfully done
                teamRepository.save(board);
            });
        }

    }

    private void saveFeatures(List<Feature> features, FeatureCollector collector) {
        features.forEach(f -> {
            f.setCollectorId(collector.getId());
            Feature existing = featureRepository.findByCollectorIdAndSId(collector.getId(), f.getsId());
            if (existing != null) {
                f.setId(existing.getId());
            }
            featureRepository.save(f);
        });
    }


    private void updateFeaturesWithLatestEpics(List<Epic> epicList, FeatureCollector collector) {
        epicList.stream().filter(Epic::isRecentUpdate).forEach(e -> {
            List<Feature> existing = featureRepository.findAllByCollectorIdAndSEpicID(collector.getId(), e.getId());
            existing.forEach(ex -> {
                if (!ex.getsEpicChangeDate().equalsIgnoreCase(e.getChangeDate()) ||
                        !ex.getsEpicAssetState().equalsIgnoreCase(e.getStatus()) ||
                        !ex.getsEpicName().equalsIgnoreCase(e.getName()) ||
                        !ex.getsEpicBeginDate().equalsIgnoreCase(e.getBeginDate())
                ) {
                    ex.setsEpicAssetState(e.getStatus());
                    ex.setsEpicBeginDate(e.getBeginDate());
                    ex.setsEpicChangeDate(e.getChangeDate());
                    ex.setsEpicName(e.getName());
                    featureRepository.save(ex);
                }
            });
        });
    }

}
