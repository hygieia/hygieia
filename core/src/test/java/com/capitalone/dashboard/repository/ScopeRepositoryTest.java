package com.capitalone.dashboard.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalone.dashboard.model.Scope;

public class ScopeRepositoryTest extends FongoBaseRepositoryTest {
	private static Scope mockV1Scope;
	private static Scope mockJiraScope;
	private static Scope mockJiraScope2;
	private static final String generalUseDate = "2015-11-01T00:00:00Z";
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static Calendar cal = Calendar.getInstance();
	private static final String maxDateWinner = df.format(new Date());
	private static String maxDateLoser = new String();
	private static String currentScopeEndDate = new String();
	private static final ObjectId jiraCollectorId = new ObjectId();
	private static final ObjectId v1CollectorId = new ObjectId();

	@Autowired
	private ScopeRepository scopeRepo;

	@Before
	public void setUp() {
		// Date-time modifications
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -1);
		maxDateLoser = df.format(cal.getTime());
		cal.add(Calendar.DAY_OF_YEAR, +13);
		currentScopeEndDate = df.format(cal.getTime());

		// VersionOne Mock Scope
		mockV1Scope = new Scope();
		mockV1Scope.setCollectorId(v1CollectorId);
		mockV1Scope.setIsDeleted("False");
		mockV1Scope.setChangeDate(maxDateLoser);
		mockV1Scope.setAssetState("Active");
		mockV1Scope.setBeginDate(maxDateLoser);
		mockV1Scope.setEndDate(currentScopeEndDate);
		mockV1Scope.setId(ObjectId.get());
		mockV1Scope.setIsDeleted("False");
		mockV1Scope.setName("Massive Project");
		mockV1Scope.setpId("Scope:14327");
		mockV1Scope
				.setProjectPath("This -> Is -> C -> Project -> Path -> " + mockV1Scope.getName());

		// Jira Mock Scope
		// Mock Scope 1
		mockJiraScope = new Scope();
		mockJiraScope.setCollectorId(jiraCollectorId);
		mockJiraScope.setIsDeleted("False");
		mockJiraScope.setChangeDate(maxDateWinner);
		mockJiraScope.setAssetState("Active");
		mockJiraScope.setBeginDate(maxDateLoser);
		mockJiraScope.setEndDate(currentScopeEndDate);
		mockJiraScope.setId(ObjectId.get());
		mockJiraScope.setIsDeleted("False");
		mockJiraScope.setName("Yet Another Agile Scope");
		mockJiraScope.setpId("110213780");
		mockJiraScope.setProjectPath("This -> Is -> B -> Project -> Path -> "
				+ mockJiraScope.getName());

		// Mock Scope 2
		mockJiraScope2 = new Scope();
		mockJiraScope2.setCollectorId(jiraCollectorId);
		mockJiraScope2.setIsDeleted("False");
		mockJiraScope2.setChangeDate(generalUseDate);
		mockJiraScope2.setAssetState("Inactive");
		mockJiraScope2.setBeginDate(maxDateLoser);
		mockJiraScope2.setEndDate(currentScopeEndDate);
		mockJiraScope2.setId(ObjectId.get());
		mockJiraScope2.setIsDeleted("False");
		mockJiraScope2.setName("This One Is Serious");
		mockJiraScope2.setpId("11978790");
		mockJiraScope2.setProjectPath("This -> Is -> A -> Project -> Path -> "
				+ mockJiraScope2.getName());
	}

	@After
	public void tearDown() {
		mockV1Scope = null;
		mockJiraScope = null;
		mockJiraScope2 = null;
		scopeRepo.deleteAll();
	}

	@Test
	public void validateConnectivity_HappyPath() {
		scopeRepo.save(mockV1Scope);
		scopeRepo.save(mockJiraScope);
		scopeRepo.save(mockJiraScope2);

		assertTrue("Happy-path MongoDB connectivity validation for the ScopeRepository has failed",
				scopeRepo.findAll().iterator().hasNext());
	}

	@Test
	public void testGetScopeIdById_HappyPath() {
		scopeRepo.save(mockV1Scope);
		scopeRepo.save(mockJiraScope);
		scopeRepo.save(mockJiraScope2);
		String testScopeId = mockJiraScope.getpId();

		assertEquals("Expected scope ID did not match actual scope ID", testScopeId, scopeRepo
				.getScopeIdById(testScopeId).get(0).getpId().toString());
	}

	@Test
	public void testGetScopeById_HappyPath() {
		scopeRepo.save(mockV1Scope);
		scopeRepo.save(mockJiraScope);
		scopeRepo.save(mockJiraScope2);
		String testScopeId = mockJiraScope.getpId();

		assertEquals("Expected scope Name did not match actual scope Name",
				mockJiraScope.getName(), scopeRepo.getScopeById(testScopeId).get(0).getName()
						.toString());
	}

	@Test
	public void testGetAllScopes_HappyPath() {
		scopeRepo.save(mockV1Scope);
		scopeRepo.save(mockJiraScope2);
		scopeRepo.save(mockJiraScope);

		assertEquals("Expected scope ID did not match actual scope ID", mockV1Scope.getpId(),
				scopeRepo.findByOrderByProjectPathDesc().get(0).getpId().toString());
		assertEquals("Expected scope ID did not match actual scope ID", mockJiraScope.getpId(),
				scopeRepo.findByOrderByProjectPathDesc().get(1).getpId().toString());
		assertEquals("Expected scope ID did not match actual scope ID", mockJiraScope2.getpId(),
				scopeRepo.findByOrderByProjectPathDesc().get(2).getpId().toString());
	}

	@Test
	public void testGetScopeMaxChangeDate_HappyPath() {
		scopeRepo.save(mockJiraScope);
		scopeRepo.save(mockJiraScope2);
		
		assertEquals(
				"Expected max change dated scope ID did not match actual max change dated scope ID",
				mockJiraScope.getChangeDate(),
				scopeRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(jiraCollectorId, maxDateLoser).get(0)
						.getChangeDate().toString());
	}
}