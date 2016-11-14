package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.ArtifactIdentifier;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentComponent;
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
import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Basic tests to test the functionality of the DynamicPipelineServiceImpl class. See
 * setup tasks for data configuration.
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class DynamicPipelineServiceImplTest {
	
	private static final ObjectId COMPONENT_ID = new ObjectId();
	private static final ObjectId DASHBOARD_ID = new ObjectId();
	private static final ObjectId COLLECTOR_ITEM_SCM_ID = new ObjectId();
	private static final ObjectId COLLECTOR_ITEM_BUILD_ID = new ObjectId();
	private static final ObjectId COLLECTOR_ITEM_PIPELINE_ID = new ObjectId();
	
	private static final String SCM_URL1 = "http://foo.bar.com";
	private static final String SCM_BRANCH1 = "master";
	private static final String SCM_AUTHOR1 = "MarkRx";
	
	private static final String BUILD_JOB_URL1 = "http://my.build.com/asdf/";
	private static final String BUILD_AUTHOR1 = "MarkRx";
	
	private static final String ENV_NAME3 = "Environment 3";
	private static final String ENV_NAME2 = "Environment 2";
	private static final String ENV_NAME1 = "Environment 1";
	private static final String ENV_URL3 = "http://env3.foo.com";
	private static final String ENV_URL2 = "http://env2.foo.com";
	private static final String ENV_URL1 = "http://env1.foo.com";
	
	// note we havn't been setting the branch for builds
	private static final RepoBranch BUILD_REPO_BRANCH1 = new RepoBranch(SCM_URL1, "", RepoType.GIT);
	
	private static final Comparator<PipelineCommit> PIPELINE_COMMIT_COMPARATOR = new Comparator<PipelineCommit>() {

		@Override
		public int compare(PipelineCommit o1, PipelineCommit o2) {
			return o1.getScmRevisionNumber().compareTo(o2.getScmRevisionNumber());
		}
		
	};
	
	@Mock
    private PipelineRepository pipelineRepository;
	
	@Mock
    private DashboardRepository dashboardRepository;
	
	@Mock
    private CollectorItemRepository collectorItemRepository;
    
	@Mock
    private BinaryArtifactService binaryArtifactService;
	
	@Mock
    private BuildService buildService;
	
	@Mock
    private CommitService commitService;
	
	@Mock
	private DeployService deployService;
	
	@InjectMocks
	private DynamicPipelineServiceImpl service;
	
	@Test
	public void testSearch() {
		CollectorItem pipelineCI = setupPipelineCollectorItem();
		CollectorItem scmCI = setupScmCollectorItem();
		Component component = setupComponent(scmCI);
		Dashboard dashboard = setupDashboard(component);
		
		List<Commit> commits = setupCommits();
		List<Build> builds = setupBuilds();
		List<Environment> environments = setupEnvironments();
		List<BinaryArtifact> binaryArtifacts = setupBinaryArtifacts();
		
		Mockito.when(collectorItemRepository.findOne(Mockito.eq(COLLECTOR_ITEM_PIPELINE_ID))).thenReturn(pipelineCI);
		Mockito.when(dashboardRepository.findOne(Mockito.eq(DASHBOARD_ID))).thenReturn(dashboard);
		Mockito.when(commitService.search(Mockito.any(CommitRequest.class))).thenReturn(new DataResponse<>(commits, 0));
		Mockito.when(buildService.search(Mockito.any(BuildSearchRequest.class))).thenReturn(new DataResponse<>(builds, 0));
		Mockito.when(deployService.getDeployStatus(Mockito.eq(COMPONENT_ID))).thenReturn(new DataResponse<>(environments, 0));
		Mockito.when(binaryArtifactService.search(Mockito.any(BinaryArtifactSearchRequest.class))).then(new Answer<DataResponse<Iterable<BinaryArtifact>>>() {
			@Override
			public DataResponse<Iterable<BinaryArtifact>> answer(InvocationOnMock invocation) throws Throwable {
				BinaryArtifactSearchRequest request = (BinaryArtifactSearchRequest)invocation.getArguments()[0];
				
				for (BinaryArtifact ba : binaryArtifacts) {
					if (ba.getArtifactName().equals(request.getArtifactName()) && ba.getArtifactVersion().equals(request.getArtifactVersion())) {
						return new DataResponse<>(Collections.singleton(ba), 0);
					}
				}
				
				return new DataResponse<>(Collections.emptyList(), 0);
			}
		});
		
		PipelineSearchRequest request = new PipelineSearchRequest();
		request.setBeginDate(0L);
		request.setEndDate(100L);
		
		request.setCollectorItemId(Collections.singletonList(COLLECTOR_ITEM_PIPELINE_ID));
		
		Iterable<PipelineResponse> responses = service.search(request);
		PipelineResponse response = responses.iterator().next();
		
		List<PipelineStageType> unmappedStages = response.getUnmappedStages();
		Map<PipelineStageType, List<PipelineResponseCommit>> stages = response.getStages();
		
		assertEquals(2, unmappedStages.size());
		
		List<PipelineResponseCommit> prcs = stages.get(PipelineStageType.Commit);
		Map<String, PipelineResponseCommit> prcMap = prcRevisionMap(prcs);
		assertEquals(1, prcs.size());
		assertNotNull(prcMap.get("H"));
		
		prcs = stages.get(PipelineStageType.Build);
		prcMap = prcRevisionMap(prcs);
		assertEquals(1, prcs.size());
		assertNotNull(prcMap.get("G"));
		
		prcs = stages.get(PipelineStageType.Dev);
		prcMap = prcRevisionMap(prcs);
		assertEquals(3, prcs.size());
		assertNotNull(prcMap.get("C1"));
		assertNotNull(prcMap.get("E"));
		assertNotNull(prcMap.get("F"));
		
		prcs = stages.get(PipelineStageType.QA);
		prcMap = prcRevisionMap(prcs);
		assertEquals(3, prcs.size());
		assertNotNull(prcMap.get("B"));
		assertNotNull(prcMap.get("C2"));
		assertNotNull(prcMap.get("D"));
		
		prcs = stages.get(PipelineStageType.Int);
		prcMap = prcRevisionMap(prcs);
		assertEquals(1, prcs.size());
		assertNotNull(prcMap.get("A"));
	}
	
	@Test
	public void testPipeline() {
		Pipeline pipeline = createEmptyPipeline();
		CollectorItem pipelineCI = setupPipelineCollectorItem();
		CollectorItem scmCI = setupScmCollectorItem();
		Component component = setupComponent(scmCI);
		Dashboard dashboard = setupDashboard(component);
		
		List<Commit> commits = setupCommits();
		List<Build> builds = setupBuilds();
		List<Environment> environments = setupEnvironments();
		List<BinaryArtifact> binaryArtifacts = setupBinaryArtifacts();
		
		Mockito.when(collectorItemRepository.findOne(Mockito.eq(COLLECTOR_ITEM_PIPELINE_ID))).thenReturn(pipelineCI);
		Mockito.when(dashboardRepository.findOne(Mockito.eq(DASHBOARD_ID))).thenReturn(dashboard);
		Mockito.when(commitService.search(Mockito.any(CommitRequest.class))).thenReturn(new DataResponse<>(commits, 0));
		Mockito.when(buildService.search(Mockito.any(BuildSearchRequest.class))).thenReturn(new DataResponse<>(builds, 0));
		Mockito.when(deployService.getDeployStatus(Mockito.eq(COMPONENT_ID))).thenReturn(new DataResponse<>(environments, 0));
		Mockito.when(binaryArtifactService.search(Mockito.any(BinaryArtifactSearchRequest.class))).then(new Answer<DataResponse<Iterable<BinaryArtifact>>>() {
			@Override
			public DataResponse<Iterable<BinaryArtifact>> answer(InvocationOnMock invocation) throws Throwable {
				BinaryArtifactSearchRequest request = (BinaryArtifactSearchRequest)invocation.getArguments()[0];
				
				for (BinaryArtifact ba : binaryArtifacts) {
					if (ba.getArtifactName().equals(request.getArtifactName()) && ba.getArtifactVersion().equals(request.getArtifactVersion())) {
						return new DataResponse<>(Collections.singleton(ba), 0);
					}
				}
				
				return new DataResponse<>(Collections.emptyList(), 0);
			}
		});
		
		pipeline = service.buildPipeline(pipeline, 0L, 100L);
		
		Map<String, PipelineCommit> pcs = pipeline.getCommitsByStage(PipelineStageType.Commit.name());
		assertEquals(9, pcs.size());
		assertNotNull(pcs.get("A"));
		assertNotNull(pcs.get("B"));
		assertNotNull(pcs.get("C1"));
		assertNotNull(pcs.get("C2"));
		assertNotNull(pcs.get("D"));
		assertNotNull(pcs.get("E"));
		assertNotNull(pcs.get("F"));
		assertNotNull(pcs.get("G"));
		assertNotNull(pcs.get("H"));
		
		pcs = pipeline.getCommitsByStage(PipelineStageType.Build.name());
		assertEquals(8, pcs.size());
		assertNotNull(pcs.get("A"));
		assertNotNull(pcs.get("B"));
		assertNotNull(pcs.get("C1"));
		assertNotNull(pcs.get("C2"));
		assertNotNull(pcs.get("D"));
		assertNotNull(pcs.get("E"));
		assertNotNull(pcs.get("F"));
		assertNotNull(pcs.get("G"));
		
		pcs = pipeline.getCommitsByStage(ENV_NAME1);
		assertEquals(7, pcs.size());
		assertNotNull(pcs.get("A"));
		assertNotNull(pcs.get("B"));
		assertNotNull(pcs.get("C1"));
		assertNotNull(pcs.get("C2"));
		assertNotNull(pcs.get("D"));
		assertNotNull(pcs.get("E"));
		assertNotNull(pcs.get("F"));
		
		pcs = pipeline.getCommitsByStage(ENV_NAME2);
		assertEquals(4, pcs.size());
		assertNotNull(pcs.get("A"));
		assertNotNull(pcs.get("B"));
		assertNotNull(pcs.get("C2"));
		assertNotNull(pcs.get("D"));
		
		pcs = pipeline.getCommitsByStage(ENV_NAME3);
		assertEquals(1, pcs.size());
		assertNotNull(pcs.get("A"));
	}

	// Basic test for processing the commit portion of the pipeline
	@Test
	public void testProcessCommitsForGit() {
		Pipeline pipeline = createEmptyPipeline();
		List<Commit> commits = setupCommits();
		
		service.processCommits(pipeline, commits);
		
		List<PipelineCommit> pipelineCommits = getPipelineCommits(pipeline, PipelineStageType.Commit);
		
		assertEquals(9, pipelineCommits.size());
		
		PipelineCommit pc = pipelineCommits.get(0);
		assertEquals("A", pc.getScmRevisionNumber());
		assertEquals(100000000, pc.getTimestamp());
		assertEquals(SCM_URL1, pc.getScmUrl());
		assertEquals(SCM_BRANCH1, pc.getScmBranch());
		assertEquals("Commit A", pc.getScmCommitLog());
		assertEquals(SCM_AUTHOR1, pc.getScmAuthor());
		assertEquals(100000000, pc.getScmCommitTimestamp());
		assertEquals(1, pc.getNumberOfChanges());
		
		pc = pipelineCommits.get(1);
		assertEquals("B", pc.getScmRevisionNumber());
		assertEquals(100000001, pc.getTimestamp());
		assertEquals(Arrays.asList("A"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(2);
		assertEquals("C1", pc.getScmRevisionNumber());
		assertEquals(100000002, pc.getTimestamp());
		assertEquals(Arrays.asList("B"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(3);
		assertEquals("C2", pc.getScmRevisionNumber());
		assertEquals(100000003, pc.getTimestamp());
		assertEquals(Arrays.asList("B"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(4);
		assertEquals("D", pc.getScmRevisionNumber());
		assertEquals(100000004, pc.getTimestamp());
		assertEquals(Arrays.asList("C2"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(5);
		assertEquals("E", pc.getScmRevisionNumber());
		assertEquals(100000005, pc.getTimestamp());
		assertNotNull(pc.getScmParentRevisionNumbers());
		assertEquals("D", pc.getScmParentRevisionNumbers().get(0));
		assertEquals("C1", pc.getScmParentRevisionNumbers().get(1));
		
		pc = pipelineCommits.get(6);
		assertEquals("F", pc.getScmRevisionNumber());
		assertEquals(100000006, pc.getTimestamp());
		assertEquals(Arrays.asList("E"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(7);
		assertEquals("G", pc.getScmRevisionNumber());
		assertEquals(100000007, pc.getTimestamp());
		assertEquals(Arrays.asList("F"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(8);
		assertEquals("H", pc.getScmRevisionNumber());
		assertEquals(110000000, pc.getTimestamp());
		assertEquals(Arrays.asList("G"), pc.getScmParentRevisionNumbers());
	}
	
	// Basic test for processing the build portion of the pipeline
	@Test
	public void testProcessBuilds() {
		Pipeline pipeline = createEmptyPipeline();
		List<Commit> commits = setupCommits();
		List<Build> builds = setupBuilds();
		
		service.processBuilds(pipeline, builds, commits);
		
		List<PipelineCommit> pipelineCommits = getPipelineCommits(pipeline, PipelineStageType.Build);
		
		assertEquals(8, pipelineCommits.size());
		
		PipelineCommit pc = pipelineCommits.get(0);
		assertEquals("A", pc.getScmRevisionNumber());
		assertEquals(100001000, pc.getTimestamp());
		assertEquals(SCM_URL1, pc.getScmUrl());
		assertEquals(SCM_BRANCH1, pc.getScmBranch());
		assertEquals("Commit A", pc.getScmCommitLog());
		assertEquals(SCM_AUTHOR1, pc.getScmAuthor());
		assertEquals(100000000, pc.getScmCommitTimestamp());
		assertEquals(1, pc.getNumberOfChanges());
		
		pc = pipelineCommits.get(1);
		assertEquals("B", pc.getScmRevisionNumber());
		assertEquals(100003000, pc.getTimestamp());
		assertEquals(Arrays.asList("A"), pc.getScmParentRevisionNumbers());
		
		// C1 uses the last sucessful build which would have been F
		pc = pipelineCommits.get(2);
		assertEquals("C1", pc.getScmRevisionNumber());
		assertEquals(100007000, pc.getTimestamp());
		assertEquals(Arrays.asList("B"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(3);
		assertEquals("C2", pc.getScmRevisionNumber());
		assertEquals(100004000, pc.getTimestamp());
		assertEquals(Arrays.asList("B"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(4);
		assertEquals("D", pc.getScmRevisionNumber());
		assertEquals(100004000, pc.getTimestamp());
		assertEquals(Arrays.asList("C2"), pc.getScmParentRevisionNumbers());
		
		// E got lost so it uses the timestamp that was on the commit
		pc = pipelineCommits.get(5);
		assertEquals("E", pc.getScmRevisionNumber());
		assertEquals(100000005, pc.getTimestamp());
		assertNotNull(pc.getScmParentRevisionNumbers());
		assertEquals("D", pc.getScmParentRevisionNumbers().get(0));
		assertEquals("C1", pc.getScmParentRevisionNumbers().get(1));
		
		pc = pipelineCommits.get(6);
		assertEquals("F", pc.getScmRevisionNumber());
		assertEquals(100007000, pc.getTimestamp());
		assertEquals(Arrays.asList("E"), pc.getScmParentRevisionNumbers());
		
		pc = pipelineCommits.get(7);
		assertEquals("G", pc.getScmRevisionNumber());
		assertEquals(100009000, pc.getTimestamp());
		assertEquals(Arrays.asList("F"), pc.getScmParentRevisionNumbers());
	}
	
	@Test
	public void testProcessDeployments() {
		Pipeline pipeline = createEmptyPipeline();
		List<Environment> environments = setupEnvironments();
		Map<ArtifactIdentifier, Collection<BinaryArtifact>> artifacts = setupBinaryArtifactsMap();
		List<Commit> commits = setupCommits();
		
		service.processDeployments(pipeline, environments, artifacts, commits);
		
		Map<String, PipelineCommit> pcs = pipeline.getCommitsByStage(ENV_NAME3);
		assertEquals(1, pcs.size());
		assertNotNull(pcs.get("A"));
		assertEquals("A", pcs.get("A").getScmRevisionNumber());
		assertEquals(100030000, pcs.get("A").getTimestamp());
		assertEquals(SCM_URL1, pcs.get("A").getScmUrl());
		assertEquals(SCM_BRANCH1, pcs.get("A").getScmBranch());
		assertEquals("Commit A", pcs.get("A").getScmCommitLog());
		assertEquals(SCM_AUTHOR1, pcs.get("A").getScmAuthor());
		assertEquals(100000000, pcs.get("A").getScmCommitTimestamp());
		assertEquals(1, pcs.get("A").getNumberOfChanges());
		
		pcs = pipeline.getCommitsByStage(ENV_NAME2);
		assertEquals(4, pcs.size());
		assertNotNull(pcs.get("D"));
		assertEquals(100031000, pcs.get("D").getTimestamp());
		assertNotNull(pcs.get("C2"));
		assertEquals(100031000, pcs.get("C2").getTimestamp());
		assertNotNull(pcs.get("B"));
		assertEquals(100031000, pcs.get("B").getTimestamp());
		assertNotNull(pcs.get("A"));
		assertEquals(100031000, pcs.get("A").getTimestamp());
		
		pcs = pipeline.getCommitsByStage(ENV_NAME1);
		assertEquals(7, pcs.size());
		assertNotNull(pcs.get("F"));
		assertEquals(100032000, pcs.get("F").getTimestamp());
		assertNotNull(pcs.get("E"));
		assertEquals(100032000, pcs.get("E").getTimestamp());
		assertNotNull(pcs.get("D"));
		assertEquals(100032000, pcs.get("D").getTimestamp());
		assertNotNull(pcs.get("C2"));
		assertEquals(100032000, pcs.get("C2").getTimestamp());
		assertNotNull(pcs.get("C1"));
		assertEquals(100032000, pcs.get("C1").getTimestamp());
		assertNotNull(pcs.get("B"));
		assertEquals(100032000, pcs.get("B").getTimestamp());
		assertNotNull(pcs.get("A"));
		assertEquals(100032000, pcs.get("A").getTimestamp());
	}
	
	@Test
	public void testGetCommitHistory() {
		Map<String, Collection<String>> commitHistory = setupCommitHistory();
		
		List<String> hist = service.getCommitHistory(commitHistory, "A");
		assertEquals(1, hist.size());
		assertTrue(hist.contains("A"));
		
		hist = service.getCommitHistory(commitHistory, "E");
		assertEquals(6, hist.size());
		
		assertTrue(hist.contains("A"));
		assertTrue(hist.contains("B"));
		assertTrue(hist.contains("C1"));
		assertTrue(hist.contains("C2"));
		assertTrue(hist.contains("D"));
		assertTrue(hist.contains("E"));
		
		hist = service.getCommitHistory(commitHistory, "D");
		assertEquals(4, hist.size());
		
		assertTrue(hist.contains("A"));
		assertTrue(hist.contains("B"));
		assertTrue(hist.contains("C2"));
		assertTrue(hist.contains("D"));
	}
	
	private List<PipelineCommit> getPipelineCommits(Pipeline pipeline, PipelineStageType type) {
		EnvironmentStage stage = pipeline.getStages().get(type.name());
		
		assertNotNull(stage);
		
		Set<PipelineCommit> processedCommits = stage.getCommits();
		List<PipelineCommit> rt = new ArrayList<>(processedCommits);
		
		Collections.sort(rt, PIPELINE_COMMIT_COMPARATOR);
		
		return rt;
	}
	
	private List<Commit> setupCommits() {
		List<Commit> rt = new ArrayList<>();
		
		long commitTime = 100000000 - 1;
		
		rt.add(createCommit("A", ++commitTime));
		rt.add(createCommit("B", ++commitTime, "A"));
		rt.add(createCommit("C1", ++commitTime, "B"));
		rt.add(createCommit("C2", ++commitTime, "B"));
		rt.add(createCommit("D", ++commitTime, "C2"));
		rt.add(createCommit("E", ++commitTime, "D", "C1"));
		rt.add(createCommit("F", ++commitTime, "E"));
		rt.add(createCommit("G", ++commitTime, "F"));
		rt.add(createCommit("H", 110000000, "G")); // needs to be higher than last build so not picked up in build portion
		
		return rt;
	}
	
	private Map<String, Collection<String>> setupCommitHistory() {
		Map<String, Collection<String>> rt = new HashMap<>();
		
		rt.put("A", new ArrayList<>());
		rt.put("B", Arrays.asList("A"));
		rt.put("C1", Arrays.asList("B"));
		rt.put("C2", Arrays.asList("B"));
		rt.put("D", Arrays.asList("C2"));
		rt.put("E", Arrays.asList("D", "C1"));
		rt.put("F", Arrays.asList("E"));
		rt.put("G", Arrays.asList("F"));
		rt.put("H", Arrays.asList("G"));
		
		return rt;
	}
	
	private List<Build> setupBuilds() {
		List<Build> rt = new ArrayList<Build>();
		
		long startTime = 100001000 - 1000;
		
		rt.add(createBuild("1", startTime += 1000, startTime + 100, BuildStatus.Success, "A"));
		rt.add(createBuild("2", startTime += 1000, startTime + 100, BuildStatus.Success));
		rt.add(createBuild("3", startTime += 1000, startTime + 100, BuildStatus.Success, "B"));
		rt.add(createBuild("4", startTime += 1000, startTime + 100, BuildStatus.Success, "C2", "D"));
		rt.add(createBuild("5", startTime += 1000, startTime + 100, BuildStatus.Failure, "C1"));
		rt.add(createBuild("7", startTime += 1000, startTime + 100, BuildStatus.Aborted));
		// pretend for some reason E is missing in our build history
		rt.add(createBuild("8", startTime += 1000, startTime + 100, BuildStatus.Success, "F"));
		rt.add(createBuild("9", startTime += 1000, startTime + 100, BuildStatus.Success));
		rt.add(createBuild("10", startTime += 1000, startTime + 100, BuildStatus.Success, "G"));
		
		// needs to be in descending order
		Collections.reverse(rt);
		
		return rt;
	}
	
	private List<BinaryArtifact> setupBinaryArtifacts() {
		List<BinaryArtifact> rt = new ArrayList<>();
		
		rt.add(createBinaryArtifact("helloworld", "1.0.0", "1", "A"));
		rt.add(createBinaryArtifact("helloworld", "1.0.1", "4", "D"));
		rt.add(createBinaryArtifact("helloworld", "1.0.2", "9", "F"));
		
		return rt;
	}
	
	private Map<ArtifactIdentifier, Collection<BinaryArtifact>> setupBinaryArtifactsMap() {
		Map<ArtifactIdentifier, Collection<BinaryArtifact>> rt = new HashMap<>();
		
		rt.put(new ArtifactIdentifier(null, "helloworld", "1.0.0", null, null), Collections.singleton(createBinaryArtifact("helloworld", "1.0.0", "1", "A")));
		rt.put(new ArtifactIdentifier(null, "helloworld", "1.0.1", null, null), Collections.singleton(createBinaryArtifact("helloworld", "1.0.1", "4", "D")));
		rt.put(new ArtifactIdentifier(null, "helloworld", "1.0.2", null, null), Collections.singleton(createBinaryArtifact("helloworld", "1.0.2", "9", "F")));
		
		return rt;
	}
	
	private List<Environment> setupEnvironments() {
		List<Environment> rt = new ArrayList<>();
		
		rt.add(createEnvironment(ENV_NAME3, ENV_URL3, "helloworld", "1.0.0", 100030000));
		rt.add(createEnvironment(ENV_NAME2, ENV_URL2, "helloworld", "1.0.1", 100031000));
		rt.add(createEnvironment(ENV_NAME1, ENV_URL1, "helloworld", "1.0.2", 100032000));
		
		return rt;
	}
	
	private Component setupComponent(CollectorItem scmCI) {
		Component rt = new Component();
		rt.setId(COMPONENT_ID);
		rt.addCollectorItem(CollectorType.SCM, scmCI);
		
		return rt;
	}
	
	private CollectorItem setupPipelineCollectorItem() {
		CollectorItem ci = new CollectorItem();
		ci.setId(COLLECTOR_ITEM_PIPELINE_ID);
		ci.getOptions().put("dashboardId", DASHBOARD_ID.toHexString());
		
		return ci;
	}
	
	private CollectorItem setupScmCollectorItem() {
		CollectorItem ci = new CollectorItem();
		ci.setId(COLLECTOR_ITEM_SCM_ID);
		ci.getOptions().put("url", SCM_URL1);
		ci.getOptions().put("branch", SCM_BRANCH1);
		
		return ci;
	}
	
	private Dashboard setupDashboard(Component component) {
		Dashboard rt = new Dashboard("foo", "bar", new Application("helloworld", component), "MarkRx", DashboardType.Product);
		
		Widget pipelineWidget = new Widget();
		pipelineWidget.setName("pipeline");
		Map<String, String> mappings = new HashMap<>();
		mappings.put(PipelineStageType.Dev.name(), ENV_NAME1);
		mappings.put(PipelineStageType.QA.name(), ENV_NAME2);
		mappings.put(PipelineStageType.Int.name(), ENV_NAME3);
		pipelineWidget.getOptions().put("mappings", mappings);
		
		rt.getWidgets().add(pipelineWidget);
		
		rt.setId(DASHBOARD_ID);
		
		return rt;
	}
	
	private Pipeline createEmptyPipeline() {
		Pipeline rt = new Pipeline();
		
		rt.setCollectorItemId(COLLECTOR_ITEM_PIPELINE_ID);
		return rt;
	}
	
	private Commit createCommit(String scmRevisionNumber, long scmCommitTimestamp, String... scmParentRevisionNumbers) {
		Commit rt = new Commit();
		
		rt.setId(new ObjectId());
		rt.setCollectorItemId(COLLECTOR_ITEM_SCM_ID);
		rt.setScmUrl(SCM_URL1);
		rt.setScmBranch(SCM_BRANCH1);
		rt.setScmRevisionNumber(scmRevisionNumber);
		rt.setScmCommitLog("Commit " + scmRevisionNumber);
		rt.setScmAuthor(SCM_AUTHOR1);
		rt.setScmCommitTimestamp(scmCommitTimestamp);
		rt.setNumberOfChanges(1);
		rt.setTimestamp(System.currentTimeMillis());
		
		if (scmParentRevisionNumbers != null) {
			rt.setScmParentRevisionNumbers(Lists.newArrayList(scmParentRevisionNumbers));
		}
		
		return rt;
	}
	
	private Build createBuild(String number, long startTime, long endTime, BuildStatus buildStatus, String... scmRevisionNumbers) {
		Build rt = new Build();
		
		rt.setId(new ObjectId());
		rt.setCollectorItemId(COLLECTOR_ITEM_BUILD_ID);
		rt.setTimestamp(System.currentTimeMillis());
		rt.setNumber(number);
		rt.setBuildUrl(BUILD_JOB_URL1 + number);
		rt.setStartTime(startTime);
		rt.setEndTime(endTime);
		rt.setDuration(endTime - startTime);
		rt.setBuildStatus(buildStatus);
		rt.setStartedBy(BUILD_AUTHOR1);
		rt.setLog("Log " + number);
		rt.getCodeRepos().add(BUILD_REPO_BRANCH1);
		
		if (scmRevisionNumbers != null) {
			for (String rn : scmRevisionNumbers) {
				SCM scm = new SCM();
				
				// note that these SCMs are not always fully filled in
				scm.setScmRevisionNumber(rn);
				scm.setScmCommitLog("Commit " + rn);
				scm.setScmUrl(SCM_URL1);
				
				rt.addSourceChangeSet(scm);
			}
		}
		
		return rt;
	}
	
	private BinaryArtifact createBinaryArtifact(String artifactName, String artifactVersion, String buildNumber, String headRevisionNumber) {
		BinaryArtifact rt = new BinaryArtifact();
		
		rt.setArtifactGroupId("my.group");
		rt.setArtifactName(artifactName);
		rt.setCanonicalName(artifactName);
		rt.setArtifactVersion(artifactVersion);
		
		rt.setBuildUrl(BUILD_JOB_URL1 + buildNumber);
		rt.setBuildNumber(buildNumber);
		rt.setJobUrl(BUILD_JOB_URL1);
		rt.setJobName("my job");
		rt.setInstanceUrl("http://my.build.com");
		
		rt.setScmUrl(SCM_URL1);
		rt.setScmBranch(SCM_BRANCH1);
		rt.setScmRevisionNumber(headRevisionNumber);
		
		return rt;
	}
	
	private Environment createEnvironment(String envName, String envUrl, String duName, String duVersion, long deployDate) {
		Environment rt = new Environment(envName, envUrl);
		
		EnvironmentComponent ec = new EnvironmentComponent();
		ec.setComponentName(duName);
		ec.setComponentVersion(duVersion);
		ec.setDeployed(true);
		ec.setAsOfDate(deployDate);
		
		DeployableUnit du = new DeployableUnit(ec, Collections.emptyList());
		
		rt.getUnits().add(du);
		
		return rt;
	}
	
	private Map<String, PipelineResponseCommit> prcRevisionMap(List<PipelineResponseCommit> prcs) {
		Map<String, PipelineResponseCommit> rt = new HashMap<>();
		prcs.forEach(prc -> rt.put(prc.getScmRevisionNumber(), prc));
		
		return rt;
	}
}
