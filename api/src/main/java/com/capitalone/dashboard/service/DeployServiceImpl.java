package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.model.deploy.Server;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeployServiceImpl implements DeployService {

	private final ComponentRepository componentRepository;
	private final EnvironmentComponentRepository environmentComponentRepository;
	private final EnvironmentStatusRepository environmentStatusRepository;
	private final CollectorRepository collectorRepository;

	@Autowired
	public DeployServiceImpl(ComponentRepository componentRepository,
			EnvironmentComponentRepository environmentComponentRepository,
			EnvironmentStatusRepository environmentStatusRepository,
			CollectorRepository collectorRepository) {
		this.componentRepository = componentRepository;
		this.environmentComponentRepository = environmentComponentRepository;
		this.environmentStatusRepository = environmentStatusRepository;
		this.collectorRepository = collectorRepository;
	}

	@Override
	public DataResponse<List<Environment>> getDeployStatus(ObjectId componentId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Deployment).get(0);
		ObjectId collectorItemId = item.getId();

		List<EnvironmentComponent> components = environmentComponentRepository
				.findByCollectorItemId(collectorItemId);
		List<EnvironmentStatus> statuses = environmentStatusRepository
				.findByCollectorItemId(collectorItemId);
		
		List<Environment> environments = new ArrayList<>();
		for (Map.Entry<Environment, List<EnvironmentComponent>> entry : groupByEnvironment(
				components).entrySet()) {
			Environment env = entry.getKey();
			environments.add(env);
			for (EnvironmentComponent envComponent : entry.getValue()) {
				env.getUnits().add(
						new DeployableUnit(envComponent, servers(envComponent,
								statuses)));
			}
		}

		Collector collector = collectorRepository
				.findOne(item.getCollectorId());
		return new DataResponse<>(environments, collector.getLastExecuted());
	}

	private Map<Environment, List<EnvironmentComponent>> groupByEnvironment(
			List<EnvironmentComponent> components) {
		Map<Environment, List<EnvironmentComponent>> map = new LinkedHashMap<>();
		for (EnvironmentComponent component : components) {
			Environment env = new Environment(component.getEnvironmentName(),
					component.getEnvironmentUrl());

			if (!map.containsKey(env)) {
				map.put(env, new ArrayList<EnvironmentComponent>());
			}

			// Following logic is to send only the latest deployment status - there may be better way to do this
			Iterator<EnvironmentComponent> alreadyAddedIter = map.get(env)
					.iterator();

			boolean found = false;
			ArrayList<EnvironmentComponent> toRemove = new ArrayList<EnvironmentComponent>();
			ArrayList<EnvironmentComponent> toAdd = new ArrayList<EnvironmentComponent>();
			while (alreadyAddedIter.hasNext()) {
				EnvironmentComponent ec = (EnvironmentComponent) alreadyAddedIter
						.next();
				if (component.getComponentName().equalsIgnoreCase(
						ec.getComponentName())) {
					found = true;
					if (component.getAsOfDate() > ec.getAsOfDate()) {
						toRemove.add(ec);
						toAdd.add(component);
					}
				}
			}
			if (!found) {
				toAdd.add(component);
			}
			map.get(env).removeAll(toRemove);
			map.get(env).addAll(toAdd);
		}

		return map;
	}

	private Iterable<Server> servers(final EnvironmentComponent component,
			List<EnvironmentStatus> statuses) {
		return Iterables.transform(
				Iterables.filter(statuses, new ComponentMatches(component)),
				new ToServer());
	}

	private class ComponentMatches implements Predicate<EnvironmentStatus> {
		private EnvironmentComponent component;

		private ComponentMatches(EnvironmentComponent component) {
			this.component = component;
		}

		@Override
		public boolean apply(EnvironmentStatus environmentStatus) {
			return environmentStatus.getEnvironmentName().equals(
					component.getEnvironmentName())
					&& environmentStatus.getComponentName().equals(
							component.getComponentName());
		}
	}

	private class ToServer implements Function<EnvironmentStatus, Server> {
		@Override
		public Server apply(EnvironmentStatus status) {
			return new Server(status.getResourceName(), status.isOnline());
		}
	}
}
