package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductCollectorTask extends CollectorTask<ProductDashboardCollector> {

    private final ProductDashboardRepository productDashboardRepository;
    private final TeamDashboardRepository teamDashboardRepository;
    private final PipelineRepository pipelineRepository;
    private final BuildRepository    buildRepository;
    private final CommitRepository commitRepository;
    private final ProductClient productClient;
    private final ProductSettings productSettings;

    @Autowired
    public ProductCollectorTask(TaskScheduler taskScheduler,
                                ProductDashboardRepository productDashboardRepository,
                                TeamDashboardRepository teamDashboardRepository,
                                PipelineRepository pipelineRepository,
                                BuildRepository buildRepository,
                                CommitRepository commitRepository,
                                ProductClient productClient,
                                ProductSettings productSettings) {
        super(taskScheduler, "Product");
        this.productDashboardRepository = productDashboardRepository;
        this.teamDashboardRepository = teamDashboardRepository;
        this.productClient = productClient;
        this.productSettings = productSettings;
        this.pipelineRepository = pipelineRepository;
        this.commitRepository = commitRepository;
        this.buildRepository = buildRepository;
    }

    @Override
    public String getCron() {
        return productSettings.getCron();
    }

    @Override
    public ProductDashboardCollector getCollector() {
        return ProductDashboardCollector.prototype();
    }

    @Override
    public BaseCollectorRepository<ProductDashboardCollector> getCollectorRepository() {
        return productDashboardRepository;
    }

    private void doCleanUp(List<TeamDashboardCollectorItem> collectorItems, List<Dashboard> dashboards){
        //get all collectorItems where there aren't dashboards'
        if(dashboards.isEmpty()){
            return;
        }

        List<String> dashboardIdsForCollectorItemsToRemove = new ArrayList<>();

        for(TeamDashboardCollectorItem collectorItem : collectorItems){
            dashboardIdsForCollectorItemsToRemove.add(collectorItem.getDashboardId());
        }
        for(Dashboard d :dashboards){
            dashboardIdsForCollectorItemsToRemove.remove(d.getId().toString());
        }

        List<TeamDashboardCollectorItem> collectorItemsToDelete = (List) teamDashboardRepository.findByDashboardIdIn(dashboardIdsForCollectorItemsToRemove);
        teamDashboardRepository.delete(collectorItemsToDelete);
    }

    @Override
    public void collect(ProductDashboardCollector collector) {
        List<TeamDashboardCollectorItem> existingTeamDashboardCollectorItems = teamDashboardRepository.findTeamDashboards(collector.getId());
        List<Dashboard> allTeamDashboards = productClient.getAllTeamDashboards();

        doCleanUp(existingTeamDashboardCollectorItems, allTeamDashboards);
        addNewCollectorItemsForNewDashboards(existingTeamDashboardCollectorItems, allTeamDashboards, collector);

        Map<TeamDashboardCollectorItem, Dashboard> enabledTeamDashboardsMap = findAllEnabledTeamDashboards(collector);

        for(Map.Entry<TeamDashboardCollectorItem, Dashboard> entryItem : enabledTeamDashboardsMap.entrySet()){
            Dashboard dashboard = entryItem.getValue();
            TeamDashboardCollectorItem dashboardCollectorItem = entryItem.getKey();

            List<CollectorItem> scmCollectorItems = dashboard.getApplication().getComponents().get(0).getCollectorItems(CollectorType.SCM);
            if(scmCollectorItems != null && !scmCollectorItems.isEmpty()){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_MONTH, -productSettings.getCommitDateThreshold());
                Long dateThreshold = calendar.getTimeInMillis();
                Set<Commit> commitsForDashboard = new HashSet<>();
                for(CollectorItem scmCollectorItem : scmCollectorItems){
                    commitsForDashboard.addAll(commitRepository.findByCollectorItemIdAndScmCommitTimestamp(scmCollectorItem.getId(), dateThreshold));
                }

                Pipeline teamDashboardPipeline = pipelineRepository.findByCollectorItemId(dashboardCollectorItem.getId());
                if(teamDashboardPipeline == null){
                    teamDashboardPipeline = new Pipeline();
                    teamDashboardPipeline.setName(dashboard.getTitle());
                    teamDashboardPipeline.setCollectorItemId(dashboardCollectorItem.getId());
                }

                //Build pipelines for the commits
                teamDashboardPipeline.getStages().put(PipelineStageType.Commit, buildCommitPipelineStage(commitsForDashboard, dashboard));
                pipelineRepository.save(teamDashboardPipeline);

                //set the last processed timestamp
                dashboardCollectorItem.setLastProcessedDate(new Date().getTime());
                teamDashboardRepository.save(dashboardCollectorItem);
            }


        }
    }


    private Map<TeamDashboardCollectorItem, Dashboard> findAllEnabledTeamDashboards(ProductDashboardCollector collector) {
        Map<TeamDashboardCollectorItem, Dashboard> enabledTeamDashboardsMap = new HashMap<>();
        List<TeamDashboardCollectorItem> enabledTeamDashboards = teamDashboardRepository.findByEnabled(collector.getId());
        if(enabledTeamDashboards.isEmpty()){
            return enabledTeamDashboardsMap;
        }

        List<Dashboard> allTeamDashboards = productClient.getAllTeamDashboards();
        //this will hold a map of enabled team dashboards and their collectoritems
        for(TeamDashboardCollectorItem dashboardCollectorItem : enabledTeamDashboards){
            for(Dashboard dashboard : allTeamDashboards){
                if(dashboard.getId().toString().equals(dashboardCollectorItem.getDashboardId())){ //// TODO: 1/20/16 might need conversion to string
                    enabledTeamDashboardsMap.put(dashboardCollectorItem, dashboard);
                }
            }
        }
        return enabledTeamDashboardsMap;
    }

    /**
     * Creates a {@link PipelineStage} for the commit phase of a {@link Pipeline} based on a
     * list of commits that occurred after a dashboard was created
     * @param commitsAfterDashboardCreated
     * @return {@link PipelineStage}
     */
    private PipelineStage buildCommitPipelineStage(Set<Commit> commitsAfterDashboardCreated, Dashboard dashboard){
        List<String> revisionNumbers = new ArrayList<>();

        /**
         * get all revision numbers for the commits after the dashboard was created
         */
        for(Commit commit : commitsAfterDashboardCreated){
            revisionNumbers.add(commit.getScmRevisionNumber());
        }

        PipelineStage commitPipelineStage = new PipelineStage();
        Map<Commit, List<Build>> commitBuildMap = new HashMap<>();

        //Get the build collector items for this particular dashboard
        List<CollectorItem> collectorItems = dashboard.getApplication().getComponents().get(0).getCollectorItems(CollectorType.Build);
        List<ObjectId> collectorItemIds = new ArrayList<>();
        for(CollectorItem collectorItem : collectorItems){
            collectorItemIds.add(collectorItem.getId());
        }
        //get all the builds for the revision numbers of the commits above and the
        List<Build> buildsForCommits = buildRepository.findBuildsForRevisionNumbersAndBuildCollectorItemIds(revisionNumbers, collectorItemIds);
        List<Build> successfulBuildsForCommits;

        /**
         * Or all of the commits, build a list of builds that included the commit
         * (pivoting the relationship that currently exists for Build to Commits)
         */
        for(Commit c : commitsAfterDashboardCreated){
            successfulBuildsForCommits = new ArrayList<>();
            for(Build build : buildsForCommits){
                //if build was successful and its changeset includes the commit, add to the list
                if(build.getBuildStatus().equals(BuildStatus.Success) && build.getSourceChangeSet().contains(c)){
                    successfulBuildsForCommits.add(build);
                }
            }

            /**
             * If this commit didn't exist in any successful builds it is considered to be in the commit phase
             */
            if(successfulBuildsForCommits.isEmpty()){
                PipelineCommit pipelineCommit = new PipelineCommit();
                pipelineCommit.setCommit(c);
                pipelineCommit.addNewPipelineProcessedTimestamp(PipelineStageType.Commit, c.getScmCommitTimestamp());
                commitPipelineStage.getCommits().add(pipelineCommit);
            }
            //this commit did exist in a successful build, so it is in at least the build stage...

            commitBuildMap.put(c, successfulBuildsForCommits);
        }
        return commitPipelineStage;
    }

    private void addNewCollectorItemsForNewDashboards(List<TeamDashboardCollectorItem> existingTeamDashboardCollectorItems, List<Dashboard> allTeamDashboards, ProductDashboardCollector collector) {

        if(allTeamDashboards.isEmpty()){
            return;
        }

        Map<TeamDashboardCollectorItem, Dashboard> createdTeamDashboards = new HashMap<>();
        for(Dashboard dashboard : allTeamDashboards){
            TeamDashboardCollectorItem teamDashboardCollectorItem = new TeamDashboardCollectorItem();
            teamDashboardCollectorItem.setDashboardId(dashboard.getId().toString());
            createdTeamDashboards.put(teamDashboardCollectorItem, dashboard);
        }

        /**
         * find all collector items that are new
         */
        List<TeamDashboardCollectorItem> toUpdate = new ArrayList<>();
        for ( Map.Entry entry : createdTeamDashboards.entrySet()) {
            /**
             * if collectoritem for the dashboard in the repository doesnt exist
             * as a collector item add it to the list of items to save
             */
            if(!existingTeamDashboardCollectorItems.contains(entry.getKey())) {
                TeamDashboardCollectorItem collectorItem = ((TeamDashboardCollectorItem)entry.getKey());
                collectorItem.setCollectorId(collector.getId());
                collectorItem.setDescription(((Dashboard)entry.getValue()).getTitle());

                //// TODO: 1/20/16 where should this be done??
                //collectorItem.setDateEnabled(new Date().getTime());
                toUpdate.add(collectorItem);
            }
        }
        teamDashboardRepository.save(toUpdate);
    }
}
