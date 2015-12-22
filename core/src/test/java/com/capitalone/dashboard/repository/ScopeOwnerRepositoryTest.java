package com.capitalone.dashboard.repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.capitalone.dashboard.config.MongoConfig;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = { MongoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class ScopeOwnerRepositoryTest {
	private static ScopeOwnerCollectorItem mockV1ScopeOwner;
	private static ScopeOwnerCollectorItem mockJiraScopeOwner;
	private static ScopeOwnerCollectorItem mockJiraScopeOwner2;
	private static final String generalUseDate = "2015-11-01T00:00:00Z";
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static Calendar cal = Calendar.getInstance();
	private static final String maxDateWinner = df.format(new Date());
	private static String maxDateLoser = new String();
	private static final ObjectId jiraCollectorId = new ObjectId();
	private static final ObjectId jiraCollectorId2 = new ObjectId();
	private static final ObjectId v1CollectorId = new ObjectId();

	@ClassRule
	public static final EmbeddedMongoDBRule RULE = new EmbeddedMongoDBRule();

	@Autowired
	private ScopeOwnerRepository scopeOwnerRepo;

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
		mockJiraScopeOwner2.setCollectorId(jiraCollectorId2);
		mockJiraScopeOwner2.setIsDeleted("False");
		mockJiraScopeOwner2.setChangeDate(generalUseDate);
		mockJiraScopeOwner2.setAssetState("Inactive");
		mockJiraScopeOwner2.setId(ObjectId.get());
		mockJiraScopeOwner2.setTeamId("078123416");
		mockJiraScopeOwner2.setName("Jedi Knights");
		mockJiraScopeOwner2.setDescription(mockJiraScopeOwner2.getName());
		mockJiraScopeOwner2.setEnabled(false);
	}

	@After
	public void tearDown() {
		mockV1ScopeOwner = null;
		mockJiraScopeOwner = null;
		mockJiraScopeOwner2 = null;
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
						.findTopByOrderByChangeDateDesc(mockJiraScopeOwner.getCollectorId(),
								maxDateLoser).get(0).getChangeDate().toString());
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
}