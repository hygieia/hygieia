package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.deploy.DeployableUnit;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.model.deploy.Server;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.capitalone.dashboard.request.CollectorRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeployServiceImpl implements DeployService {

    private final ComponentRepository componentRepository;
    private final EnvironmentComponentRepository environmentComponentRepository;
    private final EnvironmentStatusRepository environmentStatusRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CollectorService collectorService;

    @Autowired
    public DeployServiceImpl(ComponentRepository componentRepository,
                             EnvironmentComponentRepository environmentComponentRepository,
                             EnvironmentStatusRepository environmentStatusRepository,
                             CollectorRepository collectorRepository, CollectorItemRepository collectorItemRepository, CollectorService collectorService) {
        this.componentRepository = componentRepository;
        this.environmentComponentRepository = environmentComponentRepository;
        this.environmentStatusRepository = environmentStatusRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.collectorService = collectorService;
    }

    @Override
    public DataResponse<List<Environment>> getDeployStatus(ObjectId componentId) {
        Component component = componentRepository.findOne(componentId);
        
        Collection<CollectorItem> cis = component.getCollectorItems()
                .get(CollectorType.Deployment);
        
        return getDeployStatus(cis);
    }
    
    private DataResponse<List<Environment>> getDeployStatus(Collection<CollectorItem> deployCollectorItems) {
    	List<Environment> environments = new ArrayList<>();
    	long lastExecuted = 0;
    	
        if (deployCollectorItems == null) {
            return new DataResponse<>(environments, 0);
        }
        
        // We will assume that if the component has multiple deployment collectors
        // then each collector will have a different url which means each Environment will be different
        for (CollectorItem item : deployCollectorItems) {
	        ObjectId collectorItemId = item.getId();
	
	        List<EnvironmentComponent> components = environmentComponentRepository
	                .findByCollectorItemId(collectorItemId);
	        List<EnvironmentStatus> statuses = environmentStatusRepository
	                .findByCollectorItemId(collectorItemId);
	
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
	        
	        if (collector.getLastExecuted() > lastExecuted) {
	        	lastExecuted = collector.getLastExecuted();
	        }
        }
        return new DataResponse<>(environments, lastExecuted);
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

        public ComponentMatches(EnvironmentComponent component) {
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


    @Override
    public String create(DeployDataCreateRequest request) throws HygieiaException {
        /**
         * Step 1: create Collector if not there
         * Step 2: create Collector item if not there
         * Step 3: Insert build data if new. If existing, update it.
         */
        Collector collector = createCollector();

        if (collector == null) {
            throw new HygieiaException("Failed creating Deploy collector.", HygieiaException.COLLECTOR_CREATE_ERROR);
        }

        CollectorItem collectorItem = createCollectorItem(collector, request);

        if (collectorItem == null) {
            throw new HygieiaException("Failed creating Deploy collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }

        EnvironmentComponent deploy = createEnvComponent(collectorItem, request);

        if (deploy == null) {
            throw new HygieiaException("Failed inserting/updating Deployment information.", HygieiaException.ERROR_INSERTING_DATA);
        }

        return deploy.getId().toString();

    }

    @Override
    public DataResponse<List<Environment>> getDeployStatus(String applicationName) {
        //FIXME: Remove hardcoding of Jenkins.
        List<Collector> collectorList = collectorRepository.findByCollectorTypeAndName(CollectorType.Deployment, "Jenkins");
        if (CollectionUtils.isEmpty(collectorList)) return new DataResponse<>(null, 0);

        Collector collector = collectorList.get(0);
        List<CollectorItem> cis = collectorItemRepository.findByOptionsAndDeployedApplicationName(collector.getId(), applicationName);

        return getDeployStatus(cis);
    }

    private Collector createCollector() {
        CollectorRequest collectorReq = new CollectorRequest();
        collectorReq.setName("Jenkins");  //for now hardcode it.
        collectorReq.setCollectorType(CollectorType.Deployment);
        Collector col = collectorReq.toCollector();
        col.setEnabled(true);
        col.setOnline(true);
        col.setLastExecuted(System.currentTimeMillis());
        return collectorService.createCollector(col);
    }

    private CollectorItem createCollectorItem(Collector collector, DeployDataCreateRequest request) {
        CollectorItem tempCi = new CollectorItem();
        tempCi.setCollectorId(collector.getId());
        tempCi.setDescription(request.getAppName());
        tempCi.setPushed(true);
        tempCi.setLastUpdated(System.currentTimeMillis());
        tempCi.setNiceName(request.getNiceName());
        Map<String, Object> option = new HashMap<>();
        option.put("applicationName", request.getAppName());
        option.put("instanceUrl", request.getInstanceUrl());
        tempCi.getOptions().putAll(option);

        CollectorItem collectorItem = collectorService.createCollectorItem(tempCi);
        return collectorItem;
    }

    private EnvironmentComponent createEnvComponent(CollectorItem collectorItem, DeployDataCreateRequest request) {
        EnvironmentComponent deploy = environmentComponentRepository.
                findByUniqueKey(collectorItem.getId(), request.getArtifactName(), request.getArtifactName(), request.getEndTime());
        if ( deploy == null) {
            deploy = new EnvironmentComponent();
        }

        deploy.setAsOfDate(System.currentTimeMillis());
        deploy.setCollectorItemId(collectorItem.getId());
        deploy.setComponentID(request.getArtifactGroup());
        deploy.setComponentName(request.getArtifactName());
        deploy.setComponentVersion(request.getArtifactVersion());
        deploy.setEnvironmentName(request.getEnvName());
        deploy.setDeployTime(request.getEndTime());
        deploy.setDeployed("SUCCESS".equalsIgnoreCase(request.getDeployStatus()));

        return environmentComponentRepository.save(deploy); // Save = Update (if ID present) or Insert (if ID not there)
    }
}
