package com.capitalone.dashboard.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.capitalone.dashboard.auth.exceptions.DeleteLastAdminException;
import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.UserInfoRepository;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.repository.ServiceRepository;
import com.capitalone.dashboard.util.UnsafeDeleteException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CustomRepositoryQuery customRepositoryQuery;
    @SuppressWarnings("unused")
    private final PipelineRepository pipelineRepository; //NOPMD
    private final ServiceRepository serviceRepository;
    private final UserInfoRepository userInfoRepository;

    @Autowired
    public DashboardServiceImpl(DashboardRepository dashboardRepository,
                                ComponentRepository componentRepository,
                                CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                CustomRepositoryQuery customRepositoryQuery,
                                ServiceRepository serviceRepository,
                                PipelineRepository pipelineRepository,
                                UserInfoRepository userInfoRepository) {
        this.dashboardRepository = dashboardRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.customRepositoryQuery = customRepositoryQuery;
        this.serviceRepository = serviceRepository;
        this.pipelineRepository = pipelineRepository;   //TODO - Review if we need this param, seems it is never used according to PMD
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public Iterable<Dashboard> all() {
        return dashboardRepository.findAll(new Sort(Sort.Direction.ASC, "title"));
    }

    @Override
    public Dashboard get(ObjectId id) {
        Dashboard dashboard = dashboardRepository.findOne(id);

        if (!dashboard.getApplication().getComponents().isEmpty()) {
            // Add transient Collector instance to each CollectorItem
            Map<CollectorType, List<CollectorItem>> itemMap = dashboard.getApplication().getComponents().get(0).getCollectorItems();

            Iterable<Collector> collectors = collectorsFromItems(itemMap);

            for (List<CollectorItem> collectorItems : itemMap.values()) {
                for (CollectorItem collectorItem : collectorItems) {
                    collectorItem.setCollector(getCollector(collectorItem.getCollectorId(), collectors));
                }
            }
        }

        return dashboard;
    }

    @Override
    public Dashboard create(Dashboard dashboard) throws HygieiaException {
        Iterable<Component> components = componentRepository.save(dashboard.getApplication().getComponents());
        try {
            return dashboardRepository.save(dashboard);
        } catch (Exception e) {
            componentRepository.delete(components);
            throw new HygieiaException("Failed creating dashboard.", HygieiaException.ERROR_INSERTING_DATA);
        }
    }

    @Override
    public Dashboard update(Dashboard dashboard) throws HygieiaException {
        return create(dashboard);
    }

    @Override
    public void delete(ObjectId id) {
        Dashboard dashboard = dashboardRepository.findOne(id);

        if (!isSafeDelete(dashboard)) {
            throw new UnsafeDeleteException("Cannot delete team dashboard " + dashboard.getTitle() + " as it is referenced by program dashboards.");
        }

        componentRepository.delete(dashboard.getApplication().getComponents());

        // Remove this Dashboard's services and service dependencies
        serviceRepository.delete(serviceRepository.findByDashboardId(id));
        for (com.capitalone.dashboard.model.Service service : serviceRepository.findByDependedBy(id)) { //NOPMD - using fully qualified or we pickup an incorrect spring class
            service.getDependedBy().remove(id);
            serviceRepository.save(service);
        }

        dashboardRepository.delete(dashboard);
    }

    private boolean isSafeDelete(Dashboard dashboard) {
        return !(dashboard.getType() == null || dashboard.getType().equals(DashboardType.Team)) || isSafeTeamDashboardDelete(dashboard);
    }

    private boolean isSafeTeamDashboardDelete(Dashboard dashboard) {
        boolean isSafe = false;
        List<Collector> productCollectors = collectorRepository.findByCollectorType(CollectorType.Product);
        if (productCollectors.isEmpty()) {
            return true;
        }

        Collector productCollector = productCollectors.get(0);

        CollectorItem teamDashboardCollectorItem = collectorItemRepository.findTeamDashboardCollectorItemsByCollectorIdAndDashboardId(productCollector.getId(), dashboard.getId().toString());

        //// TODO: 1/21/16 Is this safe? What if we add a new team dashbaord and quickly add it to a product and then delete it?
        if (teamDashboardCollectorItem == null) {
            return true;
        }

        if (dashboardRepository.findProductDashboardsByTeamDashboardCollectorItemId(teamDashboardCollectorItem.getId().toString()).isEmpty()) {
            isSafe = true;
        }
        return isSafe;
    }

    @Override
    public Component associateCollectorToComponent(ObjectId componentId, List<ObjectId> collectorItemIds) {
        if (componentId == null || collectorItemIds == null) {
            // Not all widgets gather data from collectors
            return null;
        }

        com.capitalone.dashboard.model.Component component = componentRepository.findOne(componentId); //NOPMD - using fully qualified name for clarity
        //we can not assume what collector item is added, what is removed etc so, we will
        //refresh the association. First disable all collector items, then remove all and re-add

        //First: disable all collectorItems of the Collector TYPEs that came in with the request.
        //Second: remove all the collectorItem association of the Collector Type  that came in
        HashSet<CollectorType> incomingTypes = new HashSet<>();
        HashMap<ObjectId, CollectorItem> toSaveCollectorItems = new HashMap<>();
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            if (!incomingTypes.contains(collector.getCollectorType())) {
                incomingTypes.add(collector.getCollectorType());
                List<CollectorItem> cItems = component.getCollectorItems(collector.getCollectorType());
                // Save all collector items as disabled for now
                if (!CollectionUtils.isEmpty(cItems)) {
                    for (CollectorItem ci : cItems) {
                        //if item is orphaned, disable it. Otherwise keep it enabled.
                        ci.setEnabled(!isLonely(ci, collector, component));
                        toSaveCollectorItems.put(ci.getId(), ci);
                    }
                }
                // remove all collector items of a type
                component.getCollectorItems().remove(collector.getCollectorType());
            }
        }

        //Last step: add collector items that came in
        for (ObjectId collectorItemId : collectorItemIds) {
            CollectorItem collectorItem = collectorItemRepository.findOne(collectorItemId);
            //the new collector items must be set to true
            collectorItem.setEnabled(true);
            Collector collector = collectorRepository.findOne(collectorItem.getCollectorId());
            component.addCollectorItem(collector.getCollectorType(), collectorItem);
            toSaveCollectorItems.put(collectorItemId, collectorItem);
            // set transient collector property
            collectorItem.setCollector(collector);
        }

        Set<CollectorItem> deleteSet = new HashSet<>();
        for (ObjectId id : toSaveCollectorItems.keySet()) {
            deleteSet.add(toSaveCollectorItems.get(id));
        }
        collectorItemRepository.save(deleteSet);
        componentRepository.save(component);
        return component;
    }


    private boolean isLonely(CollectorItem item, Collector collector, Component component) {
        List<Component> components = customRepositoryQuery.findComponents(collector, item);
        //if item is not attached to any component, it is orphaned.
        if (CollectionUtils.isEmpty(components)) return true;
        //if item is attached to more than 1 component, it is NOT orphaned
        if (components.size() > 1) return false;
        //if item is attached to ONLY 1 component it is the current one, it is going to be orphaned after this
        return (components.get(0).getId().equals(component.getId()));
    }

    @Override
    public Widget addWidget(Dashboard dashboard, Widget widget) {
        widget.setId(ObjectId.get());
        dashboard.getWidgets().add(widget);
        dashboardRepository.save(dashboard);
        return widget;
    }

    @Override
    public Widget getWidget(Dashboard dashboard, ObjectId widgetId) {
        return Iterables.find(dashboard.getWidgets(), new WidgetByIdPredicate(widgetId));
    }

    @Override
    public Widget updateWidget(Dashboard dashboard, Widget widget) {
        int index = dashboard.getWidgets().indexOf(widget);
        dashboard.getWidgets().set(index, widget);
        dashboardRepository.save(dashboard);
        return widget;
    }

    private static final class WidgetByIdPredicate implements Predicate<Widget> {
        private final ObjectId widgetId;

        public WidgetByIdPredicate(ObjectId widgetId) {
            this.widgetId = widgetId;
        }

        @Override
        public boolean apply(Widget widget) {
            return widget.getId().equals(widgetId);
        }
    }

    private Iterable<Collector> collectorsFromItems(Map<CollectorType, List<CollectorItem>> itemMap) {
        Set<ObjectId> collectorIds = new HashSet<>();
        for (List<CollectorItem> collectorItems : itemMap.values()) {
            for (CollectorItem collectorItem : collectorItems) {
                collectorIds.add(collectorItem.getCollectorId());
            }
        }

        return collectorRepository.findAll(collectorIds);
    }

    private Collector getCollector(final ObjectId collectorId, Iterable<Collector> collectors) {
        return Iterables.tryFind(collectors, new Predicate<Collector>() {
            @Override
            public boolean apply(Collector collector) {
                return collector.getId().equals(collectorId);
            }
        }).orNull();
    }

	@Override
	public List<Dashboard> getOwnedDashboards() {
		Set<Dashboard> myDashboards = new HashSet<Dashboard>();
		
		Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
		myDashboards.addAll(dashboardRepository.findByOwners(owner));
		
		// TODO: This if check is to ensure backwards compatibility for dashboards created before AuthenticationTypes were introduced.
		if (AuthenticationUtil.getAuthTypeFromContext() == AuthType.STANDARD) {
			myDashboards.addAll(dashboardRepository.findByOwner(AuthenticationUtil.getUsernameFromContext()));
		}
		
		return Lists.newArrayList(myDashboards);
	}

    @Override
    public Iterable<UserInfo> getAllUsers() {
        return userInfoRepository.findByOrderByUsernameAsc();
    }

    @Override
    public Iterable<Owner> getOwners(ObjectId id) {
        Dashboard dashboard = get(id);
        return dashboard.getOwners();
    }

    @Override
    public UserInfo promoteToOwner(ObjectId dashboardId, String username, AuthType authType) {
        Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        List<Owner> owners = dashboard.getOwners();
        Owner promotedOwner = new Owner(username, authType);
        owners.add(promotedOwner);
        dashboardRepository.save(dashboard);

        UserInfo user = userInfoRepository.findByUsernameAndAuthType(username, authType);
        if (user == null) {
            throw new UserNotFoundException(username, authType);
        }
        user.getAuthorities().add(UserRole.ROLE_ADMIN);

        return user;
    }

    @Override
    public UserInfo demoteFromOwner(ObjectId dashboardId, String username, AuthType authType) {
        Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        int numberOfOwners = dashboard.getOwners().size();

        //get admin users
        Collection<UserInfo> adminUsers = userInfoRepository.findByAuthoritiesIn(UserRole.ROLE_ADMIN);

        numberOfOwners += adminUsers.size();

        if(numberOfOwners <= 1) {
            throw new DeleteLastAdminException();
        }

        Owner demotedOwner = new Owner(username, authType);
        dashboard.getOwners().remove(demotedOwner);
        dashboardRepository.save(dashboard);

        UserInfo user = userInfoRepository.findByUsernameAndAuthType(username, authType);
        if (user == null) {
            throw new UserNotFoundException(username, authType);
        }

        user.getAuthorities().remove(UserRole.ROLE_ADMIN);
        return user;

    }

	@Override
	public String getDashboardOwner(String dashboardTitle) {

		String dashboardOwner=dashboardRepository.findByTitle(dashboardTitle).get(0).getOwner();
		
		return dashboardOwner;
	}

    @SuppressWarnings("unused")
    private DashboardType getDashboardType(Dashboard dashboard) {
        if (dashboard.getType() != null) {
            return dashboard.getType();
        }
        return DashboardType.Team;
    }

    @Override
    public Component getComponent(ObjectId componentId){

        Component component = componentRepository.findOne(componentId);
        return component;
    }


}
