package com.capitalone.dashboard.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

public class ScopeOwnerRepositoryTest extends FongoBaseRepositoryTest {
	private static ScopeOwnerCollectorItem mockV1ScopeOwner;
	private static ScopeOwnerCollectorItem mockJiraScopeOwner;
	private static ScopeOwnerCollectorItem mockJiraScopeOwner2;
	private static CollectorItem mockBadItem;
	private static final String generalUseDate = "2015-11-01T00:00:00Z";
	private static final String olderThanGeneralUseDate = "2015-10-30T00:00:00Z";
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static Calendar cal = Calendar.getInstance();
	private static final String maxDateWinner = df.format(new Date());
	private static String maxDateLoser = new String();
	private static final ObjectId jiraCollectorId = new ObjectId();
	private static final ObjectId v1CollectorId = new ObjectId();

	@Autowired
	private ScopeOwnerRepository scopeOwnerRepo;
	@Autowired
	private CollectorItemRepository badItemRepo;

	@Before
	public void setUp() {
		// Date-time modifications
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -1);
		maxDateLoser = df.format(cal.getTime());

		// VersionOne Mock Scope
		mockV1ScopeOwner = new ScopeOwnerCollectorItem();
		mockV1ScopeOwner.setCollectorId(v1CollectorId);
		mockV1ScopeOwner.setIsDeleted("False");
		mockV1ScopeOwner.setChangeDate(maxDateLoser);
		mockV1ScopeOwner.setAssetState("Active");
		mockV1ScopeOwner.setId(ObjectId.get());
		mockV1ScopeOwner.setTeamId("Team:129825");
		mockV1ScopeOwner.setName("Resistance");
		mockV1ScopeOwner.setDescription(mockV1ScopeOwner.getName());
		mockV1ScopeOwner.setEnabled(true);

		// Jira Mock Scope
		// Mock Scope 1
		mockJiraScopeOwner = new ScopeOwnerCollectorItem();
		mockJiraScopeOwner.setCollectorId(jiraCollectorId);
		mockJiraScopeOwner.setIsDeleted("False");
		mockJiraScopeOwner.setChangeDate(maxDateWinner);
		mockJiraScopeOwner.setAssetState("Active");
		mockJiraScopeOwner.setId(ObjectId.get());
		mockJiraScopeOwner.setTeamId("871589423");
		mockJiraScopeOwner.setName("Sith Lords");
		mockJiraScopeOwner.setDescription(mockJiraScopeOwner.getName());
		mockJiraScopeOwner.setEnabled(true);

		// Mock Scope 2
		mockJiraScopeOwner2 = new ScopeOwnerCollectorItem();
		mockJiraScopeOwner2.setCollectorId(jiraCollectorId);
		mockJiraScopeOwner2.setIsDeleted("False");
		mockJiraScopeOwner2.setChangeDate(generalUseDate);
		mockJiraScopeOwner2.setAssetState("Active");
		mockJiraScopeOwner2.setId(ObjectId.get());
		mockJiraScopeOwner2.setTeamId("078123416");
		mockJiraScopeOwner2.setName("Jedi Knights");
		mockJiraScopeOwner2.setDescription(mockJiraScopeOwner2.getName());
		mockJiraScopeOwner2.setEnabled(false);

		// Mock Alternative Collector Item
		mockBadItem = new CollectorItem();
		mockBadItem.setCollector(new Collector());
		mockBadItem.setCollectorId(jiraCollectorId);
		mockBadItem.setDescription("THIS SHOULD NOT SHOW UP");
		mockBadItem.setEnabled(true);
		mockBadItem.setId(ObjectId.get());
	}

	@After
	public void tearDown() {
		mockV1ScopeOwner = null;
		mockJiraScopeOwner = null;
		mockJiraScopeOwner2 = null;
		mockBadItem = null;
		badItemRepo.deleteAll();
		scopeOwnerRepo.deleteAll();
	}

	@Test
	public void validateConnectivity_HappyPath() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner2);

		assertTrue("Happy-path MongoDB connectivity validation for the ScopeRepository has failed",
				scopeOwnerRepo.findAll().iterator().hasNext());
	}

	@Test
	public void testFindTeamCollector_NoCollectorForGivenFilter() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner2);

		assertNull("Expected null response did not match actual null response",
				scopeOwnerRepo.findTeamCollector(mockJiraScopeOwner.getCollectorId(),
						mockJiraScopeOwner.getTeamId(), mockJiraScopeOwner.getName()));
	}

	@Test
	public void testFindEnabledTeamCollectors_HappyPath() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner2);

		assertEquals(
				"Expected number of enabled team collectors did not match actual number of enabled team collectors",
				1,
				scopeOwnerRepo.findEnabledTeamCollectors(mockJiraScopeOwner.getCollectorId(),
						mockJiraScopeOwner.getTeamId()).size());
	}

	@Test
	public void testGetTeamMaxChangeDate_HappyPath() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner2);

		assertEquals(
				"Expected number of enabled team collectors did not match actual number of enabled team collectors",
				mockJiraScopeOwner.getChangeDate(),
				scopeOwnerRepo
						.findTopByChangeDateDesc(mockJiraScopeOwner.getCollectorId(), maxDateLoser)
						.get(0).getChangeDate().toString());
		assertEquals(
				"Expected number of enabled team collectors did not match actual number of enabled team collectors",
				maxDateWinner,
				scopeOwnerRepo
						.findTopByChangeDateDesc(mockJiraScopeOwner.getCollectorId(),
								olderThanGeneralUseDate).get(0).getChangeDate().toString());
	}

	@Test
	public void testGetTeamMaxChangeDate_WithOtherCollectorItemClasses() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner2);
		badItemRepo.save(mockBadItem);

		assertTrue(
				"A wild CollectorItem class appeared!",
				scopeOwnerRepo
						.findTopByChangeDateDesc(mockJiraScopeOwner.getCollectorId(),
								olderThanGeneralUseDate)
						.get(0)
						.getClass()
						.toString()
						.equalsIgnoreCase(
								"class com.capitalone.dashboard.model.ScopeOwnerCollectorItem"));
	}

	@Test
	public void testGetTeamIdById_HappyPath() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner);
		scopeOwnerRepo.save(mockJiraScopeOwner2);

		assertEquals(
				"Expected number of enabled team collectors did not match actual number of enabled team collectors",
				mockJiraScopeOwner2.getTeamId(),
				scopeOwnerRepo.getTeamIdById(mockJiraScopeOwner2.getTeamId()).get(0).getTeamId()
						.toString());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetTeamIdById_IndexOutOfBoundsException() {
		String testValue = "This does not exist";
		assertEquals("Something returned that was not an IndexOutOfBoundsException", testValue,
				scopeOwnerRepo.getTeamIdById(testValue).get(0).getTeamId().toString());
	}

	@Test
	public void testGetTeamIdById_InActiveValidTeamId_OneResponse() {
		scopeOwnerRepo.save(mockV1ScopeOwner);
		assertEquals("An unexpected inactive team was included with the response", 1,
				scopeOwnerRepo.getTeamIdById(mockV1ScopeOwner.getTeamId()).size());
		scopeOwnerRepo.deleteAll();
		mockV1ScopeOwner.setAssetState("InActive");
		scopeOwnerRepo.save(mockV1ScopeOwner);
		assertEquals("Teams which are inactive should also return to be updated", 1,
				scopeOwnerRepo.getTeamIdById(mockV1ScopeOwner.getTeamId()).size());
	}
}