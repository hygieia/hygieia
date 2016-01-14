package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.mysema.query.BooleanBuilder;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScopeServiceImpl implements ScopeService {

	private final ComponentRepository componentRepository;
	private final ScopeRepository scopeRepository;
	private final CollectorRepository collectorRepository;

	/**
	 * Default autowired constructor for repositories
	 * 
	 * @param componentRepository
	 *            Repository containing components used by the UI (populated by
	 *            UI)
	 * @param collectorRepository
	 *            Repository containing all registered collectors
	 * @param scopeRepository
	 *            Repository containing all scopes
	 */
	@Autowired
	public ScopeServiceImpl(ComponentRepository componentRepository,
			CollectorRepository collectorRepository,
			ScopeRepository scopeRepository) {
		this.componentRepository = componentRepository;
		this.scopeRepository = scopeRepository;
		this.collectorRepository = collectorRepository;
	}

	/**
	 * Retrieves all unique scopes
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * 
	 * @return A data response list of type Scope containing all unique scopes
	 */
	@Override
	public DataResponse<List<Scope>> getAllScopes(ObjectId componentId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get all available scopes
		List<Scope> scope = scopeRepository.findByOrderByProjectPathDesc();

		Collector collector = collectorRepository
				.findOne(item.getCollectorId());

		return new DataResponse<>(scope, collector.getLastExecuted());
	}

	/**
	 * Retrieves the scope information for a given scope source system ID
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param scopeId
	 *            A given scope's source-system ID
	 * 
	 * @return A data response list of type Scope containing all data for a
	 *         given scope source-system ID
	 */
	@Override
	public DataResponse<List<Scope>> getScope(ObjectId componentId,
			String scopeId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get one scope by Id
		List<Scope> scope = scopeRepository.getScopeById(scopeId);

		Collector collector = collectorRepository
				.findOne(item.getCollectorId());

		return new DataResponse<>(scope, collector.getLastExecuted());
	}
}
