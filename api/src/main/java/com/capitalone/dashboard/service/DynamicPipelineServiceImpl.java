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
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.ArtifactIdentifier;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentStage;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.model.PipelineResponseCommit;
import com.capitalone.dashboard.model.PipelineStageType;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.RepoBranch.RepoType;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.BinaryArtifactSearchRequest;
import com.capitalone.dashboard.request.BuildSearchRequest;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.capitalone.dashboard.util.HygieiaUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * An implementation of PipelineService that computes pipelines dynamically.
 * <p>
 * For more details see {@link #buildPipeline(Pipeline, Long, Long)}.
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
@Service("dynamic-pipeline")
public class DynamicPipelineServiceImpl implements PipelineService {
	private static final Logger logger = Logger.getLogger(DynamicPipelineServiceImpl.class);

    private static final int PROD_COMMIT_DATE_RANGE_DEFAULT = -90;
    
    private static final Comparator<Build> BUILD_NUMBER_COMPATATOR = new Comparator<Build>() {

		@Override
		public int compare(Build o1, Build o2) {
			int b1Int = o1.getNumber() != null? Integer.valueOf(o1.getNumber()) : 0;
			int b2Int = o2.getNumber() != null? Integer.valueOf(o2.getNumber()) : 0;
			
			return b2Int - b1Int;
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
        //sets the lower and upper bound for the prod bucket's commits.  uses constant for lower bound limit and today as default for upper bound
        Long lowerBound = searchRequest.getBeginDate();
        //if(lowerBound == null){
        	// TODO get incremental updates working
            lowerBound = getMinStart();
        //}
        Long upperBound = searchRequest.getEndDate() != null ? searchRequest.getEndDate() : new Date().getTime();
		
        List<PipelineResponse> pipelineResponses = new ArrayList<>();
        for(ObjectId collectorItemId : searchRequest.getCollectorItemId()){
            Pipeline pipeline = getOrCreatePipeline(collectorItemId);
            pipeline = buildPipeline(pipeline, lowerBound, upperBound);

            // This will make debugging much easier
            pipelineRepository.save(pipeline);
            
            pipelineResponses.add(buildPipelineResponse(pipeline, lowerBound, upperBound));
            
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
    
    // Creates the response that is returned to the client
    private PipelineResponse buildPipelineResponse(Pipeline pipeline, Long lowerBound, Long upperBound){
        /**
         * get the collector item and dashboard
         */
        CollectorItem dashboardCollectorItem = collectorItemRepository.findOne(pipeline.getCollectorItemId());
        Dashboard dashboard = dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")));
        
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
        
        pipelineResponse.setUnmappedStages(findUnmappedEnvironments(dashboard));
        return pipelineResponse;
    }
    
    /**
     * Dynamically calculates what should be in a Pipeline.
     * <p>
     * A pipeline contains 3 section types: commits, builds, and deployments. The deployment
     * section is further subdivided into environments. This method gathers information
     * from collectors for the team dashboard that the pipeline corresponds to and makes a reasonable
     * attempt to correlate it.
     * <p>
     * Data is correlated in the following ways:
     * <ul>
     * <li><b>Build -&gt; Commit</b>: Builds keep track of the SCM revision numbers as well as the repository
     * and branch information (though builds with multiple repositories are not 100% accurate). Given a list
     * of commits for the dashboard we can correlate builds to them using the scm revision number.</li>
     * <li><b>EnvironmentComponent -&gt; BinaryArtifact</b>: Given a list of {@link Environment}s we can gather
     * DeploymentUnits and associate them to {@link BinaryArtifact}s by the component name and version number.
     * In the future this information may be stored in metadata that is retrieved by the deployment collector.
     * <li><b>BinaryArtifact -&gt; Commit</b>: An artifact will contain information about the HEAD svn revision
     * number that was used to produce it. Given the scm revision number we can find it in our list of commits
     * that are tracked for the dashboard and determine it along with all previous commits. For GIT this is done
     * using a graph buitl from {@link Commit#getScmParentRevisionNumbers()}. For SVN we simply grab all revisions
     * with a number less than ours.</li>
     * </ul>
     * 
     * @param pipeline		the pipeline to calculate
     * @param lowerBound	the lower window bound for gathering statistics
     * @param upperBound	the upper window bound for gathering statistics
     * @return				the <b>pipeline</b> passed in
     */
    protected Pipeline buildPipeline(Pipeline pipeline, Long lowerBound, Long upperBound) {
        CollectorItem dashboardCollectorItem = collectorItemRepository.findOne(pipeline.getCollectorItemId());
        Dashboard dashboard = dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")));

        // First gather information about our dashboard
        
        // TODO how should we handle multiple components?
        Component component = dashboard.getApplication().getComponents().iterator().next();

        // Note - since other items link to commits we always need to pull all of our commit data
        List<Commit> commits = getCommits(component, getMinStart(), upperBound);
        List<Build> builds = getBuilds(component, lowerBound, upperBound);
        List<Environment> environments = getEnvironments(component);
        Map<Environment, Collection<ArtifactIdentifier>> environmentArtifactIdentifiers = getArtifactIdentifiers(environments);
        Map<ArtifactIdentifier, Collection<BinaryArtifact>> artifacts = getBinaryArtifacts(
        		environmentArtifactIdentifiers.values().stream().flatMap( coll -> coll.stream()).collect(Collectors.toList()));
    	
    	// We only want builds that belong to our repo
    	RepoBranch repo = getComponentRepoBranch(component);
    	builds = filterBuilds(builds, repo.getUrl(), repo.getBranch());
    	artifacts = filterBinaryArtifacts(artifacts, repo.getUrl(), repo.getBranch());
    	
    	// we assume all the builds belong to the same job
    	Collections.sort(builds, BUILD_NUMBER_COMPATATOR);
        
        // recompute pipeline
        pipeline.setFailedBuilds(new HashSet<>());
        pipeline.setStages(new HashMap<>());
        
        processCommits(pipeline, commits);
        processBuilds(pipeline, builds, commits);
        processDeployments(pipeline, environments, artifacts, commits);
        
        return pipeline;
    }

    /**
     * Computes the commit stage of the pipeline.
     * 
     * @param pipeline	
     * @param commits
     */
	protected void processCommits(Pipeline pipeline, List<Commit> commits) {
		// TODO when processing commits should we only add the commits that are within the time boundaries?
    	Set<String> seenRevisionNumbers = new HashSet<>();
    	
    	if (logger.isDebugEnabled()) {
    		StringBuilder sb = new StringBuilder();
    		sb.append("\n===== Commit List =====\n");
    		for (Commit commit : commits) {
    			sb.append("    - " + commit.getId() + " (" + commit.getScmRevisionNumber() + ") - " + commit.getScmCommitLog() + "\n");
    		}
    		
    		logger.debug(sb.toString());
    	}
    	
    	for (Commit commit : commits) {
    		boolean commitNotSeen = seenRevisionNumbers.add(commit.getScmRevisionNumber());
    		
    		if (commitNotSeen) {
    			pipeline.addCommit(PipelineStageType.Commit.name(), new PipelineCommit(commit, commit.getScmCommitTimestamp()));
    		}
		}
    }
    
	/**
	 * Computes the build stage of the pipeline.
	 * <p>
	 * Given a list of builds and commits, this method will associate builds to commits and then
	 * add commits to the build stage of the pipeline. Only commits that are tracked by our dashboard
	 * are added meaning that if a build builds some other branch the commit information for that branch
	 * will not be put into the pipeline.
	 * 
	 * Note: At present some extraneous builds may be processed due to limitations in the jenkins api
	 * when there are multiple branches being built by the same job.
	 * 
	 * @param pipeline
	 * @param builds	a list of builds sorted descending by build number
	 * @param commits
	 */
    protected void processBuilds(Pipeline pipeline, List<Build> builds, List<Commit> commits) {
    	// sort again in case code changes in future to be safe
    	List<Build> sortedBuilds = new ArrayList<>(builds);
    	Collections.sort(sortedBuilds, BUILD_NUMBER_COMPATATOR);
    	Multimap<ObjectId, Commit> buildCommits = buildBuildToCommitsMap(sortedBuilds, commits);
    	
    	if (logger.isDebugEnabled()) {
    		StringBuilder sb = new StringBuilder();
    		sb.append("\n===== Build Commit Mapping =====\n");
    		for (Build build : sortedBuilds) {
    			sb.append("    - " + build.getBuildUrl() + " -> ");
    			
    			Collection<Commit> commitsForBuild = buildCommits.get(build.getId());
    			
    			if (commitsForBuild != null && !commitsForBuild.isEmpty()) {
    				boolean hasPrinted = false;
    				for (Commit commit : commitsForBuild) {
    					if (hasPrinted) {
    						sb.append(", ");
    					}
    					
    					sb.append(commit.getId());
    					
    					hasPrinted = true;
    				}
    			} else {
    				sb.append("(NONE) - No commits for build exists/found.");
    			}
    			
    			sb.append("\n");
    		}
    		
    		logger.debug(sb.toString());
    	}
    	
    	Set<String> seenRevisionNumbers = new HashSet<>();
		
    	Build latestSuccessfulBuild = null;
    	Build lastSuccessfulBuild = null;
		for (Build build : sortedBuilds) {
			boolean isSuccessful = BuildStatus.Success.equals(build.getBuildStatus());
			
			if (isSuccessful) {
				lastSuccessfulBuild = build;
				
				if (latestSuccessfulBuild == null) {
					latestSuccessfulBuild = build;
				}
			}
			
			if (isSuccessful || (lastSuccessfulBuild != null)) {
				Collection<Commit> commitsForBuild = buildCommits.get(build.getId());
				
				/*
				 * If the build belongs to a branch that has commits we are not tracking or if 
				 * the commit is greater than 90 days old this will be null as we will not have
				 * a corresponding commit from our commits collection. This is desired as we don't
				 * want to track commits outside of our window or commits that belong to different
				 * branches.
				 */
				if (commitsForBuild != null) {
					for (Commit commit : commitsForBuild) {
						boolean commitNotSeen = seenRevisionNumbers.add(commit.getScmRevisionNumber());
						
						/*
						 * Multiple builds may reference the same commit. For example, a failed build followed by a 
						 * successful build may reference the same commit. We will use the first time we come across
						 * the commit as the build it belongs to.
						 */
						if (commitNotSeen) {
							long timestamp = isSuccessful? build.getStartTime() : lastSuccessfulBuild.getStartTime();
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
					
					pipeline.addCommit(PipelineStageType.Build.name(), new PipelineCommit(commit, commit.getScmCommitTimestamp()));
				}
			}
		}
    }
    
    /**
     * Computes the build stage of the pipeline.
     * <p>
     * Iterates over each environment to determine what commits currently exist in the current deployment.
     * Given an {@link Environment} this method will iterate over its {@link DeploymentUnit}s until
     * a unit is found that corresponds to a {@link BinaryArtifact} that exists in the artifacts
     * collection. DeploymentUnits are artifacts are currently correlated by name and version.
     * If the artifact is found an attempt is made to find the last {@link Commit} that was used
     * when producing the artifact. With this information all previous commits can be deduced and
     * thus added to the pipeline at each environment stage that is processed.
     * 
     * @param pipeline
     * @param environments
     * @param artifacts
     * @param commits
     * @see #buildPipeline(Pipeline, Long, Long)
     */
    protected void processDeployments(Pipeline pipeline, List<Environment> environments,
			Map<ArtifactIdentifier, Collection<BinaryArtifact>> artifacts, List<Commit> commits) {
    	
    	if (logger.isDebugEnabled()) {
    		StringBuilder sb = new StringBuilder();
    		
    		sb.append("\n===== Environment Artifact Mapping =====\n");
    		for (Environment env : environments) {
    			sb.append("    - " + env.getName() + "\n");
    			
    			if (env.getUnits() != null && !env.getUnits().isEmpty()) {
    				for (DeployableUnit du : env.getUnits()) {
    					/*
    					 * Note: At present we do not have a way to determine artifact gruop information
    					 * from deployments. Thus if multiple distinct artifacts have the same name and
    					 * version information the wrong artifact may be picked. A future enhancement will
    					 * have to improve artifact correlation by storing deployment artifact information
    					 * in deployment tools and then using this to find the correct BinaryArtifact in
    					 * the artifacts collection.
    					 */
    					ArtifactIdentifier id = new ArtifactIdentifier(null, du.getName(), du.getVersion(), null, null);
    					sb.append("        - " + id.getGroup() + ":" + id.getName() + ":" + id.getVersion() + " -> ");
    					
    					Collection<BinaryArtifact> tmp = artifacts.get(id);
        				if (tmp != null && !tmp.isEmpty()) {
        					boolean hasPrinted = false;
        					for (BinaryArtifact ba : tmp) {
        						if (hasPrinted) {
        							sb.append(", ");
        						}
        						
        						sb.append(ba.getId());
        						
        						hasPrinted = true;
        					}
        				} else {
        					sb.append("(NONE) - No BinaryArtifacts found!");
        				}
        				
        				sb.append("\n");
    				}
    			} else {
        			sb.append("        - (NONE) - No DeployableUnits found!\n");
        		}
    		}
    		
    		logger.debug(sb.toString());
    	}
    	
    	// Build commit graph - child : parents
    	Map<String, Commit> commitsByRevisionNumber = buildRevisionNumberToCommitMap(commits);
    	Map<String, Collection<String>> commitGraph = buildCommitGraph(commits);

    	// iterate through this in case other maps ignore missing items
    	for (Environment env : environments) {
    		EnvironmentStage stage = new EnvironmentStage();
    		
    		BinaryArtifact artifact = null;
    		DeployableUnit deployableUnit = null;
    		if (env.getUnits() != null) {
    			for (DeployableUnit du : env.getUnits()) {
    				ArtifactIdentifier id = new ArtifactIdentifier(null, du.getName(), du.getVersion(), null, null);
    				
    				Collection<BinaryArtifact> tmp = artifacts.get(id);
    				if (tmp != null && !tmp.isEmpty()) {
    					artifact = tmp.iterator().next();
    					deployableUnit = du;
    					break;
    				}
    			}
    		}
    		
    		if (artifact != null) {
    			// we already filtered out bas that don't correspond to our repo
    			String revsionNumber = artifact.getScmRevisionNumber();
    			
    			List<String> commitRevisionNumbers = getCommitHistory(commitGraph, revsionNumber);
    			
    			for (String rev : commitRevisionNumbers) {
    				Commit commit = commitsByRevisionNumber.get(rev);
    				
    				if (commit == null) {
    					logger.warn("Error encountered building pipeline: commit information missing for revision " + rev);
    				} else {
    					stage.addCommit(new PipelineCommit(commit, deployableUnit.getLastUpdated()));
    				}
    			}
    		}
    		
    		pipeline.getStages().put(env.getName(), stage);
    	}
    }
    
    /**
     * Filters out builds from the dashboard's job that used a different repository.
     * <p>
     * Builds picked up by a jenkins job might refer to different repositories if users
     * changed the job around at one point. We are only interested in the repository
     * that all of our commits come from. This fill filter out builds that do not 
     * correspond to our repository.
     * <p>
     * Note that this method may not work 100% due to limitations gathering data from
     * the jenkins api. See note in code for more information.
     * 
     * @param builds	a list of builds
     * @param url		the url of the repository we are interested in
     * @param branch	the branch of the repository we are interested in
     * @return 			the filtered list
     */
    protected List<Build> filterBuilds(List<Build> builds, String url, String branch) {
    	List<Build> rt = new ArrayList<Build>();
    	String urlNoNull = url != null? url : "";
    	//String branchNoNull = branch != null? branch : "";
    	
    	for (Build build : builds) {
    		boolean added = false;
    		
    		// TODO this is not reliable
    		for (RepoBranch repo : build.getCodeRepos()) {
    			String rurl = repo.getUrl() != null? repo.getUrl() : "";
    			//String rbranch = repo.getBranch() != null? repo.getBranch() : "";
    			
    			/*
    			 * Note:
    			 * Based on https://github.com/capitalone/Hygieia/pull/857 and experimentation it seems
    			 * that branch information on the repo's is not 100% reliable when there are multiple 
    			 * repositories that participate in the build (at least for jenkins). It appears that jenkins 
    			 * will spawn of multiple builds but each build will have all of the repositories listed
    			 * that participated in the first build. This means that we cannot distinguish which particular
    			 * branch the build used in this case.
    			 * 
    			 * As a result the timestamping of commits may be a little off in the build portion of the pipeline.
    			 * We shouldn't however pick up commits that exist in other branches but not the branch we are tracking
    			 * because when processBuilds runs those extraneous commits will be dropped since they will not link
    			 * to a commit that we are tracking.
    			 */
    			// do not check type since it might not be known
    			if (HygieiaUtils.smartUrlEquals(urlNoNull, rurl) /*&& ObjectUtils.equals(branchNoNull, rbranch)*/) {
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
    
    /**
     * Filters out {@link BinaryArtifact}s that did not come from a specified repository
     * 
     * @param artifactsMap	a map of binary artifacts
     * @param url			the repository url
     * @param branch		the repository branch
     * @return				the filtered list
     */
    protected Map<ArtifactIdentifier, Collection<BinaryArtifact>> filterBinaryArtifacts(Map<ArtifactIdentifier, Collection<BinaryArtifact>> artifactsMap, String url, String branch) {
    	Map<ArtifactIdentifier, Collection<BinaryArtifact>> rt = new HashMap<>();
    	String urlNoNull = url != null? url : "";
    	String branchNoNull = branch != null? branch : "";
    	
    	for (Map.Entry<ArtifactIdentifier, Collection<BinaryArtifact>> e : artifactsMap.entrySet()) {
    		ArtifactIdentifier id = e.getKey();
    		List<BinaryArtifact> artifacts = new ArrayList<>();
    		
    		boolean added = false;
    		for (BinaryArtifact ba : e.getValue()) {
    			String baUrl = ba.getScmUrl();
    			String baBranch = ba.getScmBranch();
    			
    			if (HygieiaUtils.smartUrlEquals(urlNoNull, baUrl) && ObjectUtils.equals(branchNoNull, baBranch)) {
    				artifacts.add(ba);
    				added = true;
    				break;
    			}
    		}
    		
    		if (logger.isDebugEnabled() && !added) {
    			StringBuilder sb = new StringBuilder();
    			sb.append("Ignoring artifact identifier " + id.getGroup() + ":" + id.getName() + ":" + id.getVersion()
    			+ " since it does not correspond to any artifacts that use the component's repository\n");
    			sb.append("Component repo: (url: " + url + " branch: " + branch + ")\n");
    			sb.append("Artifacts:\n");
    			
    			boolean hasPrinted = false;
    			for (BinaryArtifact ba : e.getValue()) {
    				sb.append("    " + ba.getArtifactGroupId() + ":" + ba.getArtifactName() + ":" + ba.getArtifactVersion()
    					+ " " + "(url: " + ba.getScmUrl() + " branch: " + ba.getScmBranch() + ")\n");
    				hasPrinted = true;
    			}
    			
    			if (!hasPrinted) {
    				sb.append("(None)\n");
    			}
    			
    			logger.debug(sb.toString());
    		}
    		
    		if (!artifacts.isEmpty()) {
        		rt.put(e.getKey(), artifacts);
    		}
    	}
    	
    	return rt;
    }
	
    /**
     * Determine the SCM url and branch that is set for the component. Information
     * is gathered with the assumption that the data is stored in options.url and 
     * options.branch.
     * 
     * @param component
     * @return			the {@link RepoBranch} that the component uses
     */
	protected RepoBranch getComponentRepoBranch(Component component) {
        CollectorItem item = component.getFirstCollectorItemForType(CollectorType.SCM);
        if (item == null) {
        	logger.warn("Error encountered building pipeline: could not find scm collector item for dashboard.");
        	return new RepoBranch("", "", RepoType.Unknown);
        }
        
        // TODO find a better way?
        String url = (String)item.getOptions().get("url");
        String branch = (String)item.getOptions().get("branch");
        
        return new RepoBranch(url, branch, RepoType.Unknown);
	}
	
	private List<Commit> getCommits(Component component, Long startDate, Long endDate) {
		List<Commit> rt;
		
		CommitRequest request = new CommitRequest();
		request.setComponentId(component.getId());
		request.setCommitDateBegins(startDate);
		request.setCommitDateEnds(endDate);
		
		DataResponse<Iterable<Commit>> response = commitService.search(request);
		
		rt = response.getResult() != null? Lists.newArrayList(response.getResult()) : Collections.emptyList();
		
		return rt;
	}

	private List<Build> getBuilds(Component component, Long startDate, Long endDate) {
		List<Build> rt;
		
		BuildSearchRequest request = new BuildSearchRequest();
		request.setComponentId(component.getId());
		request.setStartDateBegins(startDate);
		request.setStartDateEnds(endDate);
		
		DataResponse<Iterable<Build>> response = buildService.search(request);
		
		rt = response.getResult() != null? Lists.newArrayList(response.getResult()) : Collections.emptyList();
		
		return rt;
	}
	
	private List<Environment> getEnvironments(Component component) {
		DataResponse<List<Environment>> response = deployService.getDeployStatus(component.getId());
		
		return response.getResult() != null? response.getResult() : Collections.emptyList();
	}
	
	// this is here for future expansion
	private Map<Environment, Collection<ArtifactIdentifier>> getArtifactIdentifiers(List<Environment> environments) {
		Map<Environment, Collection<ArtifactIdentifier>> rt = new HashMap<>();
		
		for (Environment env : environments) {
			Set<ArtifactIdentifier> ids = new HashSet<>();
			
			if (env.getUnits() != null) {
				for (DeployableUnit du : env.getUnits()) {
					ArtifactIdentifier id = new ArtifactIdentifier(null, du.getName(), du.getVersion(), null, null);
					
					ids.add(id);
				}
			}
			
			rt.put(env, new ArrayList<>(ids));
		}
		
		return rt;
	}
	
	private Map<ArtifactIdentifier, Collection<BinaryArtifact>> getBinaryArtifacts(List<ArtifactIdentifier> ids) {
		Map<ArtifactIdentifier, Collection<BinaryArtifact>> rt = new HashMap<>();
		Set<ArtifactIdentifier> idsDedup = new HashSet<>(ids);
		
		for (ArtifactIdentifier id : idsDedup) {
			List<BinaryArtifact> artifacts = getBinaryArtifacts(id.getGroup(), id.getName(), id.getVersion());
			
			rt.put(id, artifacts);
		}
		
		return rt;
	}
	
	private List<BinaryArtifact> getBinaryArtifacts(String group, String name, String version) {
		List<BinaryArtifact> rt;
		
		BinaryArtifactSearchRequest request = new BinaryArtifactSearchRequest();
		request.setArtifactGroup(group != null && group.length() > 0? group : null);
		request.setArtifactName(name != null && name.length() > 0? name : null);
		request.setArtifactVersion(version != null && version.length() > 0? version : null);
		
		DataResponse<Iterable<BinaryArtifact>> response = binaryArtifactService.search(request);
		
		rt = response.getResult() != null? Lists.newArrayList(response.getResult()) : Collections.emptyList();
		
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
					assert build.getId() != null;
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
	
	/**
	 * Calculates the commit graph.
	 * <p>
	 * Builds a commit graph using information within the list of commits. For GIT this is
	 * build using {@link Commit#getScmParentRevisionNumbers()}. For SCM this is a sequential
	 * key:value map of all previous commits.
	 * 
	 * @param commits
	 * @return			a map of revision number : parent revision number(s). This is plural in the
	 * 					case of merge commits.
	 */
	// We assume each commit belongs to the same repo + branch
	private Map<String, Collection<String>> buildCommitGraph(List<Commit> commits) {
		// multimap api doesn't quite fit what we want to do here
		Map<String, Collection<String>> rt = new HashMap<>();
		
		// TODO build graph for svn
		
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
	
	/**
	 * Given a commit graph determines all predecessor commits that came before the specified revision number.
	 * 
	 * @param commitGraph
	 * @param headRevisionNumber
	 * @return						the commit history starting at <b>headRevisionNumber</b>
	 */
	// TODO need to handle SVN
	protected List<String> getCommitHistory(Map<String, Collection<String>> commitGraph, String headRevisionNumber) {
		List<String> rt = new ArrayList<>();
		Set<String> seenRevisions = new HashSet<>();
		
		seenRevisions.add(headRevisionNumber);
		rt.add(headRevisionNumber);
		getCommitHistory(rt, seenRevisions, commitGraph, headRevisionNumber);
		
		return rt;
	}
	
	private void getCommitHistory(List<String> rt, Set<String> seenRevisions, Map<String, Collection<String>> commitGraph, String revisionNumber) {
		if (revisionNumber == null) {
			return;
		}
		
		if (commitGraph.get(revisionNumber) == null || commitGraph.get(revisionNumber).isEmpty()) {
			return;
		}
		
		for (String rn : commitGraph.get(revisionNumber)) {
			if (seenRevisions.add(rn)) {
				rt.add(rn);
				getCommitHistory(rt, seenRevisions, commitGraph, rn);
			}
		}
	}
	
	private Long getMinStart() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, PROD_COMMIT_DATE_RANGE_DEFAULT);
        return cal.getTime().getTime();
	}
	
    /**
     * finds any environments for a dashboard that aren't mapped.
     * @param dashboard
     * @return
     */
	@SuppressWarnings("unchecked")
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
