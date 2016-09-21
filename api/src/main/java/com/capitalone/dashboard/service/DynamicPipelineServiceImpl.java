package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.model.PipelineResponseCommit;
import com.capitalone.dashboard.model.PipelineStageType;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.RepoBranch.RepoType;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.capitalone.dashboard.util.HygieiaUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Service("dynamic-pipeline")
public class DynamicPipelineServiceImpl implements PipelineService {
	private static final Logger logger = Logger.getLogger(DynamicPipelineServiceImpl.class);

    private static final int PROD_COMMIT_DATE_RANGE_DEFAULT = -90;
    
    private static final Comparator<Build> BUILD_NUMBER_COMPATATOR = new Comparator<Build>() {

		@Override
		public int compare(Build o1, Build o2) {
			int b1Int = o1.getNumber() != null? Integer.valueOf(o1.getNumber()) : 0;
			int b2Int = o1.getNumber() != null? Integer.valueOf(o1.getNumber()) : 0;
			
			return b1Int - b2Int;
		}
    	
    };
    
    private final PipelineRepository pipelineRepository;
    private final DashboardRepository dashboardRepository;
    private final CollectorItemRepository collectorItemRepository;
    
    private final BinaryArtifactService binaryArtifactService;
    private final BuildService buildService;
    private final CommitService commitService;
	private final DeployService deployService;
    
    @Autowired
    public DynamicPipelineServiceImpl(PipelineRepository pipelineRepository, DashboardRepository dashboardRepository,
			CollectorItemRepository collectorItemRepository, BinaryArtifactService binaryArtifactService,
			BuildService buildService, CommitService commitService, DeployService deployService) {
		super();
		this.pipelineRepository = pipelineRepository;
		this.dashboardRepository = dashboardRepository;
		this.collectorItemRepository = collectorItemRepository;
		this.binaryArtifactService = binaryArtifactService;
		this.buildService = buildService;
		this.commitService = commitService;
		this.deployService = deployService;
	}
	
	@Override
	public Iterable<PipelineResponse> search(PipelineSearchRequest searchRequest) {
        List<PipelineResponse> pipelineResponses = new ArrayList<>();
        for(ObjectId collectorItemId : searchRequest.getCollectorItemId()){
            Pipeline pipeline = getOrCreatePipeline(collectorItemId);
            pipelineResponses.add(buildPipelineResponse(pipeline, searchRequest.getBeginDate(), searchRequest.getEndDate()));
            
            // This will make debugging much easier
            pipelineRepository.save(pipeline);
        }
        return pipelineResponses;
	}
	
    protected Pipeline getOrCreatePipeline(ObjectId collectorItemId) {
        Pipeline pipeline = pipelineRepository.findByCollectorItemId(collectorItemId);
        if(pipeline == null){
            pipeline = new Pipeline();
            pipeline.setCollectorItemId(collectorItemId);
            pipelineRepository.save(pipeline);
        }
        return pipeline;
    }
    
    private PipelineResponse buildPipelineResponse(Pipeline pipeline, Long beginDate, Long endDate){
    	// TODO filter builds that do not belong
    	
        //sets the lower and upper bound for the prod bucket's commits.  uses constant for lower bound limit and today as default for upper bound
        Long lowerBound = beginDate;
        //if(beginDate == null){ // TODO
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, PROD_COMMIT_DATE_RANGE_DEFAULT);
            lowerBound = cal.getTime().getTime();
        //}
        Long upperBound = endDate != null ? endDate : new Date().getTime();

        /**
         * get the collector item and dashboard
         */
        CollectorItem dashboardCollectorItem = collectorItemRepository.findOne(pipeline.getCollectorItemId());
        Dashboard dashboard = dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")));

        // First gather information about our dashboard
        
        // TODO how should we handle multiple components?
        Component component = dashboard.getApplication().getComponents().iterator().next();
        List<BinaryArtifact> artifacts = new ArrayList<>();

        List<Commit> commits = getCommits(component, lowerBound, upperBound);
        List<Build> builds = getBuilds(component, lowerBound, upperBound);
    	
    	// We only want builds that belong to our repo
    	RepoBranch repo = getComponentRepoBranch(component);
    	builds = filterBuilds(builds, repo.getUrl(), repo.getBranch());
    	
    	// we assume all the builds belong to the same job
    	Collections.sort(builds, BUILD_NUMBER_COMPATATOR);
        
        // Fill in pipeline
        pipeline.setFailedBuilds(new HashSet<>());
        pipeline.setStages(new HashMap<>());
        
        for(PipelineStageType stage : PipelineStageType.values()){
        	if (PipelineStageType.Commit.equals(stage)) {
        		processCommits(pipeline, commits);
        	} else if (PipelineStageType.Build.equals(stage)) {
        		processBuilds(pipeline, builds, commits);
        	} else {
        		// TODO
        	}
        }
        
        
        PipelineResponse pipelineResponse = new PipelineResponse();
        pipelineResponse.setCollectorItemId(dashboardCollectorItem.getId());
        
        /**
         * iterate over the pipeline stages (which are ordered as defined in the enum)
         * **/
        for(PipelineStageType stage : PipelineStageType.values()){
        	
            List<PipelineResponseCommit> commitsForStage = findNotPropagatedCommits(dashboard, pipeline, stage);
            pipelineResponse.getStages().put(stage, commitsForStage);
            /**
             * remove prod commits outside of filter date range
             */
            Iterator<PipelineResponseCommit> commitIterator = commitsForStage.iterator();
            if(stage.equals(PipelineStageType.Prod)){
                while(commitIterator.hasNext()){
                    PipelineResponseCommit commit = commitIterator.next();
                    if(!isBetween(commit.getProcessedTimestamps().get(stage.name()), lowerBound, upperBound)){
                        commitIterator.remove();
                    }
                }
            }
        }
        
        return pipelineResponse;
    }
    
    private void processCommits(Pipeline pipeline, List<Commit> commits) {
    	Set<String> seenRevisionNumbers = new HashSet<>();
    	
    	for (Commit commit : commits) {
    		boolean commitNotSeen = seenRevisionNumbers.add(commit.getScmRevisionNumber());
    		
    		if (commitNotSeen) {
    			pipeline.addCommit(PipelineStageType.Commit.name(), new PipelineCommit(commit, commit.getScmCommitTimestamp()));
    		}
		}
    }
    
    private void processBuilds(Pipeline pipeline, List<Build> builds, List<Commit> commits) {
    	Multimap<ObjectId, Commit> buildCommits = buildBuildToCommitsMap(builds, commits);
    	
    	/*
    	 * once there is a success add all commits prior?
    	 */
    	
    	// if failed build has use that timestamp
    	// if no build has use timestamp of commit?
    	
    	Set<String> seenRevisionNumbers = new HashSet<>();
		
    	Build latestSuccessfulBuild = null;
    	Build lastSuccessfulBuild = null;
		for (Build build : builds) {
			boolean isSuccessful = BuildStatus.Success.equals(build.getBuildStatus());
			
			if (isSuccessful) {
				lastSuccessfulBuild = build;
				
				if (latestSuccessfulBuild == null) {
					latestSuccessfulBuild = build;
				}
			}
			
			if (isSuccessful || (lastSuccessfulBuild != null)) {
				Collection<Commit> commitsForBuild = buildCommits.get(build.getId());
				
				if (commitsForBuild != null) {
					for (Commit commit : commitsForBuild) {
						boolean commitNotSeen = seenRevisionNumbers.add(commit.getScmRevisionNumber());
						
						/*
						 * Multiple builds may reference the same commit. For example, a failed build followed by a 
						 * successful build may reference the same commit. We will use the first time we come across
						 * the commit as the build it belongs to.
						 */
						if (commitNotSeen) {
							long timestamp = isSuccessful? build.getTimestamp() : lastSuccessfulBuild.getTimestamp();
							pipeline.addCommit(PipelineStageType.Build.name(), new PipelineCommit(commit, timestamp));
						}
            		}
				}
			}
		}
		
		// Gather commits that didn't participate in a build for one reason or another but have been processed
		// For now use what is in BuildEventListener... this may need to be improved upon in the future
		if (latestSuccessfulBuild != null) {
			for (Commit commit : commits) {
				if (seenRevisionNumbers.contains(commit.getScmRevisionNumber())) {
					continue;
				}
				
				if (commit.getScmCommitTimestamp() < latestSuccessfulBuild.getStartTime()) {
					if (logger.isDebugEnabled()) {
						logger.debug("processBuilds adding orphaned build commit " + commit.getScmRevisionNumber());
					}
					
					pipeline.addCommit(PipelineStageType.Build.name(), new PipelineCommit(commit, commit.getTimestamp()));
				}
			}
		}
    }
    
    /**
     * Builds picked up by a jenkins job might refer to different repositories if users
     * changed the job around at one point. We are only interested in the repository
     * that all of our commits come from. This fill filter out builds that do not 
     * correspond to our repository.
     * 
     * @param builds	a list of builds
     * @param url		the url of the repository we are interested in
     * @param branch	the branch of the repository we are interested in
     */
    private List<Build> filterBuilds(List<Build> builds, String url, String branch) {
    	List<Build> rt = new ArrayList<Build>();
    	String urlNoNull = url != null? url : "";
    	String branchNoNull = branch != null? branch : "";
    	
    	for (Build build : builds) {
    		boolean added = false;
    		for (RepoBranch repo : build.getCodeRepos()) {
    			String rurl = repo.getUrl() != null? repo.getUrl() : "";
    			String rbranch = repo.getBranch() != null? repo.getBranch() : "";
    			
    			// do not check type since it might not be known
    			if (HygieiaUtils.smartUrlEquals(urlNoNull, rurl) && ObjectUtils.equals(branchNoNull, rbranch)) {
    				rt.add(build);
    				added = true;
    				break;
    			}
    		}
    		
    		if (logger.isDebugEnabled() && !added) {
    			StringBuilder sb = new StringBuilder();
    			sb.append("Ignoring build " + build.getBuildUrl() + " since it does not use the component's repository\n");
    			sb.append("Component repo: (url: " + url + " branch: " + branch + ")\n");
    			sb.append("Build repos:    ");
    			
    			boolean hasPrinted = false;
    			for (RepoBranch repo : build.getCodeRepos()) {
    				if (hasPrinted) {
    					sb.append("                ");
    				}
    				
    				sb.append("(url: " + repo.getUrl() + " branch: " + repo.getBranch() + ")\n");
    				
    				hasPrinted = true;
    			}
    			
    			if (!hasPrinted) {
    				sb.append("(None)\n");
    			}
    			
    			logger.debug(sb.toString());
    		}
    	}
    	
    	return rt;
    }
	
	private List<BinaryArtifact> getArtifacts(String group, String name, String version) {
		return null; // TODO
	}
	
	private RepoBranch getComponentRepoBranch(Component component) {
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        
        // TODO find a better way?
        String url = (String)item.getOptions().get("url");
        String branch = (String)item.getOptions().get("branch");
        
        return new RepoBranch(url, branch, RepoType.Unknown);
	}
	
	private List<Commit> getCommits(Component component, Long startDate, Long endDate) {
		ArrayList<Commit> rt;
		
		CommitRequest request = new CommitRequest();
		request.setComponentId(component.getId());
		request.setCommitDateBegins(startDate);
		request.setCommitDateEnds(endDate);
		
		DataResponse<Iterable<Commit>> response = commitService.search(request);
		
		rt = Lists.newArrayList(response.getResult());
		
		return rt;
	}

	private List<Build> getBuilds(Component component, Long startDate, Long endDate) {
		ArrayList<Build> rt;
		
		BuildSearchRequest request = new BuildSearchRequest();
		request.setComponentId(component.getId());
		request.setStartDateBegins(startDate);
		request.setStartDateEnds(endDate);
		
		DataResponse<Iterable<Build>> response = buildService.search(request);
		
		rt = Lists.newArrayList(response.getResult());
		
		return rt;
	}
	
	private Multimap<ObjectId, Commit> buildBuildToCommitsMap(List<Build> builds, List<Commit> commits) {
		Multimap<ObjectId, Commit> rt = HashMultimap.create();
		
		Map<String, Commit> revisionNumberToCommitMap = buildRevisionNumberToCommitMap(commits);
		
		for (Build build : builds) {
			for (SCM scm : build.getSourceChangeSet()) {
				String revisionNumber = scm.getScmRevisionNumber();
				
				Commit correspondingCommit = revisionNumberToCommitMap.get(revisionNumber);
				if (correspondingCommit != null) {
					rt.put(build.getId(), correspondingCommit);
				}
			}
		}
		
		return rt;
	}
	
	// TODO needs to account for scm url and branch
	private Map<String, Commit> buildRevisionNumberToCommitMap(List<Commit> commits) {
		Map<String, Commit> rt = new HashMap<>();
		
		for (Commit commit : commits) {
			String revisionNumber = commit.getScmRevisionNumber();
			
			boolean alreadyExists = rt.put(revisionNumber, commit) != null;
			
			if (alreadyExists) {
				logger.warn("Error encountered building pipeline: multiple commits exist for revision number " + revisionNumber);
			}
		}
		
		return rt;
	}
	
	// We assume each commit belongs to the same repo + branch
	private Map<String, Collection<String>> buildCommitTree(List<Commit> commits) {
		// multimap api doesn't quite fit what we want to do here
		Map<String, Collection<String>> rt = new HashMap<>();
		
		for (Commit commit : commits) {
			String revisionNumber = commit.getScmRevisionNumber();
			boolean alreadyExists = false;
			
			List<String> parentRevisionNumbers = commit.getScmParentRevisionNumbers();
			if (parentRevisionNumbers == null) {
				alreadyExists = rt.put(revisionNumber, new ArrayList<>()) != null;
			} else {
				alreadyExists = rt.put(revisionNumber, parentRevisionNumbers) != null;
			}
			
			if (alreadyExists) {
				logger.warn("Error encountered building pipeline: multiple commits exist for revision number " + revisionNumber);
			}
		}
		
		return rt;
	}
	
	private List<String> condense(Map<String, Collection<String>> commitTree, String headRevisionNumber) {
		List<String> rt = new ArrayList<>();
		Set<String> seenRevisions = new HashSet<>();
		
		condense(rt, seenRevisions, commitTree, headRevisionNumber);
		
		return rt;
	}
	
	private void condense(List<String> rt, Set<String> seenRevisions, Map<String, Collection<String>> commitTree, String revisionNumber) {
		if (revisionNumber == null) {
			return;
		}
		
		if (commitTree.get(revisionNumber) == null || commitTree.get(revisionNumber).isEmpty()) {
			return;
		}
		
		for (String rn : commitTree.get(revisionNumber)) {
			if (seenRevisions.add(rn)) {
				rt.add(rn);
				condense(rt, seenRevisions, commitTree, rn);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    /**
     * finds any environments for a dashboard that aren't mapped.
     * @param dashboard
     * @return
     */
    private List<PipelineStageType> findUnmappedEnvironments(Dashboard dashboard){


        Map<String, String> environmentMappings= new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(Widget widget : dashboard.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                environmentMappings.putAll((Map<String,String>)widget.getOptions().get("mappings"));
            }
        }

        List<PipelineStageType> unmappedNames = new ArrayList<>();
        for(PipelineStageType stage : PipelineStageType.values()){
            if(!stage.equals(PipelineStageType.Build) && !stage.equals(PipelineStageType.Commit)){
                String mappedName = environmentMappings.get(stage.name());
                if(mappedName == null || mappedName.isEmpty()){
                    unmappedNames.add(stage);
                }
            }

        }

        return unmappedNames;
    }

    /**
     * Finds a map of commits for all stages after the current stage
     * @param stage
     * @param pipeline
     * @param dashboard
     * @return
     */
    private Map<String, PipelineCommit> getCommitsAfterStage(PipelineStageType stage, Pipeline pipeline, Dashboard dashboard){
        Map<String, PipelineCommit> unionOfAllSets = new HashMap<>();
        for(PipelineStageType stageType : PipelineStageType.values()){
            if(stageType.ordinal() > stage.ordinal()){
                Map<String, PipelineCommit> commits = findCommitsForPipelineStageType(dashboard, pipeline, stageType);
                unionOfAllSets.putAll(commits);
            }
        }
        return unionOfAllSets;
    }


    private boolean isBetween(Long commitTimestamp, Long lowerBound, Long upperBound){
        return (lowerBound <= commitTimestamp && commitTimestamp <= upperBound);
    }

    /**
     * For a given commit, will traverse the pipeline and find the time it entered in each stage of the pipeline
     * @param commit
     * @param dashboard
     * @param pipeline
     * @return
     */
    private PipelineResponseCommit applyStageTimestamps(PipelineResponseCommit commit, Dashboard dashboard, Pipeline pipeline){
        PipelineResponseCommit returnCommit = new PipelineResponseCommit(commit);


        for(PipelineStageType stageType : PipelineStageType.values()){
            //get commits for a given stage
            Map<String, PipelineCommit> commitMap = findCommitsForPipelineStageType(dashboard, pipeline, stageType);

            //if this commit doesnt have a processed timestamp for this stage, add one
            PipelineCommit pipelineCommit = commitMap.get(commit.getScmRevisionNumber());
            if(pipelineCommit != null && !returnCommit.getProcessedTimestamps().containsKey(stageType.name())){
                Long timestamp = pipelineCommit.getTimestamp();
                returnCommit.addNewPipelineProcessedTimestamp(stageType.name(), timestamp);
            }
        }
        return returnCommit;
    }

    /**
     * Gets all commits for a given pipeline stage, taking into account the mappings for environment stages
     * @param dashboard
     * @param pipeline
     * @param stageType
     * @return
     */
    private Map<String, PipelineCommit> findCommitsForPipelineStageType(Dashboard dashboard, Pipeline pipeline, PipelineStageType stageType) {
        String mappedName = (stageType.equals(PipelineStageType.Build) || stageType.equals(PipelineStageType.Commit)) ? stageType.name() : dashboard.findEnvironmentMappings().get(stageType);
        Map<String, PipelineCommit> commitMap = new HashMap<>();
        if(mappedName != null){
            commitMap = pipeline.getCommitsByStage(mappedName);
        }
        return commitMap;
    }

    /**
     * get the commits for a given stage by finding which commits havent passed to a later stage
     * @param dashboard dashboard
     * @param pipeline pipeline for that dashboard
     * @param stage current stage
     * @return a list of all commits as pipeline response commits that havent moved past the current stage
     */
    public List<PipelineResponseCommit> findNotPropagatedCommits(Dashboard dashboard, Pipeline pipeline, PipelineStageType stage){

        Map<String, PipelineCommit> startingStage = findCommitsForPipelineStageType(dashboard, pipeline, stage);
        Map<String, PipelineCommit> commitsInLaterStages = getCommitsAfterStage(stage, pipeline, dashboard);

        List<PipelineResponseCommit> notPropagatedCommits = new ArrayList<>();
        for(Map.Entry<String,PipelineCommit> entry : startingStage.entrySet()){
            if(!commitsInLaterStages.containsKey(entry.getKey())){
                PipelineResponseCommit commit = applyStageTimestamps(new PipelineResponseCommit((PipelineCommit)entry.getValue()), dashboard, pipeline);
                notPropagatedCommits.add(commit);
            }
        }
        return notPropagatedCommits;
    }

}
