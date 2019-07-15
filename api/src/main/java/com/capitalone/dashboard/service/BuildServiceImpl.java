package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStage;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CodeReposBuilds;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QBuild;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CodeReposBuildsRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.CollectorRequest;
import com.capitalone.dashboard.response.BuildDataCreateResponse;
import com.capitalone.dashboard.settings.ApiSettings;
import com.google.common.collect.Sets;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BuildServiceImpl implements BuildService {

    private final BuildRepository buildRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;
    private final DashboardService dashboardService;
    private final CollectorItemRepository collectorItemRepository;
    private final CodeReposBuildsRepository codeReposBuildsRepository;

    @Autowired
    private ApiSettings settings;

    @Autowired
    public BuildServiceImpl(BuildRepository buildRepository,
                            ComponentRepository componentRepository,
                            CollectorRepository collectorRepository,
                            CollectorService collectorService,
                            DashboardService dashboardService,
                            CollectorItemRepository collectorItemRepository,
                            ApiSettings settings,
                            CodeReposBuildsRepository codeReposBuildsRepository) {
        this.buildRepository = buildRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = collectorService;
        this.dashboardService = dashboardService;
        this.collectorItemRepository = collectorItemRepository;
        this.settings = settings;
        this.codeReposBuildsRepository = codeReposBuildsRepository;
    }

    @Override
    public DataResponse<Iterable<Build>> search(BuildSearchRequest request) {
        CollectorItem item = null;
        Component component = componentRepository.findOne(request.getComponentId());
        if ( (component == null)
                || ((item = component.getLastUpdatedCollectorItemForType(CollectorType.Build)) == null) ) {
            Iterable<Build> results = new ArrayList<>();
            return new DataResponse<>(results, new Date().getTime());
        }

        QBuild build = new QBuild("build");
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(build.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(build.endTime.goe(endTimeTarget));
        } else {
            if (request.validStartDateRange()) {
                builder.and(build.startTime.between(request.getStartDateBegins(), request.getStartDateEnds()));
            }
            if (request.validEndDateRange()) {
                builder.and(build.endTime.between(request.getEndDateBegins(), request.getEndDateEnds()));
            }
        }
        if (request.validDurationRange()) {
            builder.and(build.duration.between(request.getDurationGreaterThan(), request.getDurationLessThan()));
        }

        if (!request.getBuildStatuses().isEmpty()) {
            builder.and(build.buildStatus.in(request.getBuildStatuses()));
        }

        Collector collector = collectorRepository.findOne(item.getCollectorId());

        Iterable<Build> result;
        if (request.getMax() == null) {
            result = buildRepository.findAll(builder.getValue());
        } else {
            PageRequest pageRequest = new PageRequest(0, request.getMax(), Sort.Direction.DESC, "timestamp");
            result = buildRepository.findAll(builder.getValue(), pageRequest).getContent();
        }

        return new DataResponse<>(result, collector.getLastExecuted());
    }

    protected Build createBuild(BuildDataCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert build data if new. If existing, update it.
         */
        Collector collector = createCollector();

        if (collector == null) {
            throw new HygieiaException("Failed creating Build collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            throw new HygieiaException("Failed creating Build collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }

        Build build = createBuild(collectorItem, request);

        if (build == null) {
            throw new HygieiaException("Failed inserting/updating build information.", HygieiaException.ERROR_INSERTING_DATA);
        }

        return build;

    }

    @Override
    public String create(BuildDataCreateRequest request) throws HygieiaException {
        Build build = createBuild(request);
        return build.getId().toString();
    }

    @Override
    public String createV2(BuildDataCreateRequest request) throws HygieiaException {
        Build build = createBuild(request);
        return String.format("%s,%s", build.getId().toString(), build.getCollectorItemId().toString());
    }

    @Override
    public BuildDataCreateResponse createV3(BuildDataCreateRequest request) throws HygieiaException {
        BuildDataCreateResponse response = new BuildDataCreateResponse();
        Build build = createBuild(request);
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(response, build);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new HygieiaException(e);
        }
        finally {
            if(settings.isLookupDashboardForBuildDataCreate()) {
                populateDashboardId(response);
            }
        }
        return response;
    }

    private void populateDashboardId(BuildDataCreateResponse response) {
        if (response == null) return;

        CollectorItem collectorItem = collectorItemRepository.findOne(response.getCollectorItemId());
        if (collectorItem == null) return;

        List<Dashboard> dashboards = dashboardService.getDashboardsByCollectorItems
                (Collections.singleton(collectorItem), CollectorType.Build);
        /*
         * retrieve the dashboardId only if 1 dashboard is associated for this collectorItem
         * */
        if (CollectionUtils.isNotEmpty(dashboards) && dashboards.size() == 1) {
            response.setDashboardId(dashboards.iterator().next().getId());
        }

    }

    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("Hudson");  //for now hardcode it.
        collectorReq.setCollectorType(CollectorType.Build);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("jobUrl", "");
        allOptions.put("instanceUrl", "");
        allOptions.put("jobName","");

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put("jobUrl", "");
        uniqueOptions.put("jobName","");

        col.setAllFields(allOptions);
        col.setUniqueFields(uniqueOptions);
        col.setSearchFields(Arrays.asList("options.jobName","niceName"));
        return collectorService.createCollector(col);
    }

    private CollectorItem createCollectorItem(Collector collector, BuildDataCreateRequest request) throws HygieiaException {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getJobName());
        tempCi.setPushed(true);
        tempCi.setLastUpdated(System.currentTimeMillis());
        Map<String, Object> option = new HashMap<>();
        option.put("jobName", request.getJobName());
        option.put("jobUrl", request.getJobUrl());
        option.put("instanceUrl", request.getInstanceUrl());
        tempCi.setNiceName(request.getNiceName());
        tempCi.getOptions().putAll(option);
        if (StringUtils.isEmpty(tempCi.getNiceName())) {
            return collectorService.createCollectorItem(tempCi);
        }
        return collectorService.createCollectorItemByNiceNameAndJobName(tempCi, request.getJobName());
    }

    private Build createBuild(CollectorItem collectorItem, BuildDataCreateRequest request) {
        Build build = buildRepository.findByCollectorItemIdAndNumber(collectorItem.getId(),
                request.getNumber());
        if (build == null) {
            build = new Build();
        }
        build.setNumber(request.getNumber());
        build.setBuildUrl(request.getBuildUrl());
        build.setStartTime(request.getStartTime());
        build.setEndTime(request.getEndTime());
        build.setDuration(request.getDuration());
        build.setStartedBy(request.getStartedBy());
        build.setBuildStatus(BuildStatus.fromString(request.getBuildStatus()));
        build.setCollectorItemId(collectorItem.getId());
        build.setSourceChangeSet(request.getSourceChangeSet());
        build.setTimestamp(System.currentTimeMillis());
        if (CollectionUtils.isNotEmpty(request.getStages())) {
            build.setStages(populateStages(request.getStages()));
        }
        Set<RepoBranch> repoBranches = Sets.newHashSet();
        repoBranches.addAll(build.getCodeRepos());
        repoBranches.addAll(request.getCodeRepos());
        repoBranches.stream().forEach(repoBranch -> createSCMCollectorItem(repoBranch));
        /*
         * This is a Quick fix until feature toggle via ff4j is implemented which is coming up soon
         * */
        boolean  filterLibraryRepos = settings.getWebHook() != null && settings.getWebHook().getJenkinsBuild() != null
                && settings.getWebHook().getJenkinsBuild().isEnableFilterLibraryRepos();
        if(filterLibraryRepos && CollectionUtils.isNotEmpty(repoBranches)) {
            Set<RepoBranch> copyRepoBranches = Sets.newHashSet(repoBranches);
            for (RepoBranch repoBranch : copyRepoBranches) {
                final String codeRepo = StringUtils.lowerCase(repoBranch.getUrl());
                CodeReposBuilds entity = codeReposBuildsRepository.findByCodeRepo(codeRepo);
                if(entity == null) {
                    entity = new CodeReposBuilds();
                }
                int threshold = settings.getWebHook().getJenkinsBuild().getExcludeLibraryRepoThreshold();
                if (CollectionUtils.size(entity.getBuildCollectorItems()) > threshold) {
                    // remove the repoBranch from Build
                    repoBranches.remove(repoBranch);
                }
                entity.setCodeRepo(codeRepo);
                entity.getBuildCollectorItems().add(collectorItem.getId());
                entity.setTimestamp(System.currentTimeMillis());
                codeReposBuildsRepository.save(entity);
            }
        }
        build.getCodeRepos().clear();
        build.getCodeRepos().addAll(repoBranches);
        return buildRepository.save(build); // Save = Update (if ID present) or Insert (if ID not there)
    }

    private List<BuildStage> populateStages(List<BuildStage> buildStages) {
        buildStages.stream().forEach(buildStage -> {
            buildStage.setId(ObjectId.get());
        });
        return buildStages;
    }

    private void createSCMCollectorItem(@NotNull RepoBranch repoBranch) {
        //Check if collector item exists else create one.
        if (RepoBranch.RepoType.GIT.equals(repoBranch.getType())) {
            Collector collector = collectorRepository.findByName(settings.getGitToolName());
            if (collector == null) return;
            // check if collector item exists and is disabled.
            CollectorItem item = collectorItemRepository.findRepoByUrlAndBranch(collector.getId(),
                    repoBranch.getBranch(), repoBranch.getUrl());
            if (item == null) {
                item = new CollectorItem();
                item.setCollectorId(collector.getId());
                item.getOptions().put("branch", repoBranch.getBranch());
                item.getOptions().put("url", repoBranch.getUrl());
                item.setEnabled(true);
                item.setLastUpdated(0);
                collectorItemRepository.save(item);
            } else if (!item.isEnabled()) {
                item.setEnabled(true);
                item.setLastUpdated(0);
                collectorItemRepository.save(item);
            }
        }
    }
}
