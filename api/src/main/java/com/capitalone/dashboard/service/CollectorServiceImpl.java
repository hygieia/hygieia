package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.MultiSearchFilter;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.CustomRepositoryQuery;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollectorServiceImpl implements CollectorService {

    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final ComponentRepository componentRepository;
    private final DashboardRepository dashboardRepository;
    private final CustomRepositoryQuery customRepositoryQuery;

    @Autowired
    public CollectorServiceImpl(CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                ComponentRepository componentRepository, DashboardRepository dashboardRepository, CustomRepositoryQuery customRepositoryQuery) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.componentRepository = componentRepository;
        this.dashboardRepository = dashboardRepository;
        this.customRepositoryQuery = customRepositoryQuery;
    }

    @Override
    public List<Collector> collectorsByType(CollectorType collectorType) {
        return collectorRepository.findByCollectorType(collectorType);
    }

    @Override
    public List<Collector> collectorsById(ObjectId id) {
        return collectorRepository.findById(id);
    }

    @Override
    public Page<CollectorItem> collectorItemsByTypeWithFilter(CollectorType collectorType, String searchFilterValue, Pageable pageable) {
        List<Collector> collectors = collectorRepository.findByCollectorType(collectorType);
        List<ObjectId> collectorIds = Lists.newArrayList(Iterables.transform(collectors, new ToCollectorId()));
        Page<CollectorItem> collectorItems;
        MultiSearchFilter searchFilter = new MultiSearchFilter(searchFilterValue).invoke();
        List<String> criteria = getSearchFields(collectors);
        String defaultSearchField = getDefaultSearchField(criteria);
        // multiple search criteria
        if(!StringUtils.isEmpty(searchFilter.getAdvancedSearchKey()) && criteria.size()>1){
            String advSearchField = getAdvSearchField(criteria);
            collectorItems = collectorItemRepository.findByCollectorIdAndSearchFields(collectorIds,defaultSearchField,searchFilter.getSearchKey(),advSearchField,searchFilter.getAdvancedSearchKey(),pageable);
        }else{
            // single search criteria
            collectorItems = collectorItemRepository.findByCollectorIdAndSearchField(collectorIds,defaultSearchField,searchFilterValue,pageable);
        }
        removeJobUrlAndInstanceUrl(collectorItems);
        for (CollectorItem options : collectorItems) {
            options.setCollector(collectorById(options.getCollectorId(), collectors));
        }

        return collectorItems;
    }

    // method to remove jobUrl and instanceUrl from build collector items.
    private Page<CollectorItem> removeJobUrlAndInstanceUrl(Page<CollectorItem> collectorItems) {
        for (CollectorItem cItem : collectorItems) {
            cItem.getOptions().remove("jobUrl");
            cItem.getOptions().remove("instanceUrl");
        }
        return collectorItems;
    }

    /**
     * We want to initialize the Quasi-product collector when the API starts up
     * so that any existing Team dashboards will be added as CollectorItems.
     * <p>
     * TODO - Is this the best home for this method??
     */
    @PostConstruct
    public void initProductCollectorOnStartup() {
        Collector productCollector = collectorRepository.findByName("Product");
        if (productCollector == null) {
            productCollector = new Collector();
            productCollector.setName("Product");
            productCollector.setCollectorType(CollectorType.Product);
            productCollector.setEnabled(true);
            productCollector.setOnline(true);
            collectorRepository.save(productCollector);

            // Create collector items for existing team dashboards
            for (Dashboard dashboard : dashboardRepository.findTeamDashboards()) {
                CollectorItem item = new CollectorItem();
                item.setCollectorId(productCollector.getId());
                item.getOptions().put("dashboardId", dashboard.getId().toString());
                item.setDescription(dashboard.getTitle());
                collectorItemRepository.save(item);
            }
        }
    }

    @Override
    public CollectorItem getCollectorItem(ObjectId id) throws HygieiaException {
        CollectorItem item = collectorItemRepository.findOne(id);
        if(item == null){
            throw new HygieiaException("Failed to find collectorItem by Id.", HygieiaException.BAD_DATA);
        }
        item.setCollector(collectorRepository.findOne(item.getCollectorId()));
        return item;
    }

    @Override
    public CollectorItem createCollectorItem(CollectorItem item) {
        List<CollectorItem> existing = lookUpCollectorItem(item);
        existing.sort(Comparator.comparing(CollectorItem::getLastUpdated).reversed());
        if (CollectionUtils.isNotEmpty(existing)) {
            Optional<CollectorItem> enabledItem = existing.stream().filter(CollectorItem::isEnabled).findFirst();
            //if enabled item is found, set itemId
            if(enabledItem.isPresent()){
                item.setId(enabledItem.get().getId());
            }else{    // if no enabled item found, get first from list sorted by lastUpdated.
                item.setId(existing.stream().findFirst().get().getId());
            }
        }
        return collectorItemRepository.save(item);
    }

    private  List<CollectorItem> lookUpCollectorItem(CollectorItem collectorItem){
        if (collectorItem==null){
            return Collections.emptyList();
        }
        Collector collector = collectorRepository.findOne(collectorItem.getId());
        if (collector == null){
            return Collections.emptyList();
        }
        Map<String, Object> uniqueOptions = collector.getUniqueFields()
                .keySet()
                .stream()
                .filter(option ->collectorItem.getOptions().get(option)!=null )
                .collect(Collectors.toMap(java.util.function.Function.identity(),option-> collectorItem.getOptions().get(option),(a,b)->a));
        if(MapUtils.isEmpty(uniqueOptions)){
            return Collections.emptyList();
        }
        return IterableUtils.toList(collectorItemRepository.findAllByOptionMapAndCollectorIdsIn(uniqueOptions,Lists.newArrayList(collector.getId())));

    }

    // This is to handle scenarios where the option contains user credentials etc. We do not want to create a new collector item -
    // just update the new credentials.
    @Override
    public CollectorItem createCollectorItemSelectOptions(CollectorItem item, Map<String, Object> allOptions, Map<String, Object> uniqueOptions) {
        Collector collector =  collectorRepository.findOne(item.getCollectorId());
        Map<String,Object> uniqueFieldsFromCollector = collector.getUniqueFields();
        List<CollectorItem> existing = customRepositoryQuery.findCollectorItemsBySubsetOptions(
                item.getCollectorId(), allOptions, uniqueOptions,uniqueFieldsFromCollector);

        if (!CollectionUtils.isEmpty(existing)) {
            CollectorItem existingItem = existing.get(0);
            existingItem.getOptions().putAll(item.getOptions());
            return collectorItemRepository.save(existingItem);
        }
        return collectorItemRepository.save(item);
    }


    @Override
    public CollectorItem createCollectorItemByNiceNameAndProjectId(CollectorItem item, String projectId) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        CollectorItem existing = collectorItemRepository.findByCollectorIdNiceNameAndProjectId(item.getCollectorId(), item.getNiceName(), projectId);

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (existing == null) return createCollectorItem(item);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save = Update or Insert.
        item.setId(existing.getId());
        return collectorItemRepository.save(item);
    }

    @Override
    public CollectorItem createCollectorItemByNiceNameAndJobName(CollectorItem item, String jobName) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        CollectorItem existing = collectorItemRepository.findByCollectorIdNiceNameAndJobName(item.getCollectorId(), item.getNiceName(), jobName);

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (existing == null) return createCollectorItem(item);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save = Update or Insert.
        item.setId(existing.getId());
        return collectorItemRepository.save(item);
    }

    @Override
    public Collector createCollector(Collector collector) {
        Collector existing = collectorRepository.findByName(collector.getName());
        if (existing != null) {
            collector.setId(existing.getId());
            /*
             * Since this is invoked by api it always needs to be enabled and online,
             * additionally since this record is fetched from the database existing record
             * needs to updated with these values.
             * */
            existing.setEnabled(true);
            existing.setOnline(true);
            existing.setLastExecuted(System.currentTimeMillis());
            return collectorRepository.save(existing);
        }
        /*
         * create a new collector record
         * */
        return collectorRepository.save(collector);
    }

    @Override
    public List<CollectorItem> getCollectorItemForComponent(String id, String type) {
        ObjectId oid = new ObjectId(id);
        CollectorType ctype = CollectorType.fromString(type);
        Component component = componentRepository.findOne(oid);

        List<CollectorItem> items = component.getCollectorItems(ctype);

        // the collector items from component are not updated for collector run. We need to
        // get the 'live' collector items from the collectorItemRepository
        List<ObjectId> ids = new ArrayList<>();
        for (CollectorItem item : items) {
            ids.add(item.getId());
        }
        return (List<CollectorItem>) collectorItemRepository.findAll(ids);
    }

    @Override
    public void deleteCollectorItem(String id, boolean deleteFromComponent) throws HygieiaException {
        ObjectId objectId = new ObjectId(id);
        CollectorItem ci = getCollectorItem(objectId);
        if(ci == null) {return;}
        CollectorType type = ci.getCollector().getCollectorType();
        // First remove the association from component
        if(deleteFromComponent) {
            List<Component> components = componentRepository.findByCollectorTypeAndItemIdIn(type, Arrays.asList(objectId));
            if (CollectionUtils.isEmpty(components)) return;
            for (Component component : components) {
                if (component == null) continue;
                Map<CollectorType, List<CollectorItem>> itemMap = component.getCollectorItems();
                if(MapUtils.isEmpty(itemMap)) continue;
                List<CollectorItem> items = component.getCollectorItems(type);
                if(CollectionUtils.isEmpty(items)) continue;
                List<CollectorItem> itemsCopy = Lists.newArrayList(items);
                items.stream().filter(item -> objectId.equals(item.getId())).forEach(itemsCopy::remove);
                if(CollectionUtils.isEmpty(itemsCopy)) {
                    itemMap.remove(type);
                } else {
                    itemMap.put(type,itemsCopy);
                }
                componentRepository.save(component);
            }
        }

        //delete the collector item.
        collectorItemRepository.delete(objectId);
    }

    private Collector collectorById(ObjectId collectorId, List<Collector> collectors) {
        for (Collector collector : collectors) {
            if (collector.getId().equals(collectorId)) {
                return collector;
            }
        }
        return null;
    }

    private static class ToCollectorId implements Function<Collector, ObjectId> {
        @Override
        public ObjectId apply(Collector input) {
            return input.getId();
        }
    }

    private String getAdvSearchField(List<String> searchList) {
        return searchList!=null && searchList.size()>1?searchList.get(1):null;
    }

    private String getDefaultSearchField(List<String> searchList) {
        return searchList!=null?searchList.get(0):null;
    }

    private List<String> getSearchFields(List<Collector> collectors){
        List<List<String>> searchList  = Lists.newArrayList(Iterables.transform(collectors, new ToCollectorSearchFields()));
        return (!searchList.isEmpty() && searchList.get(0)!=null)? searchList.stream().flatMap(List::stream).collect(Collectors.toList()): null;
    }

    private static class ToCollectorSearchFields implements Function<Collector, List<String>> {
        @Override
        public List<String> apply(Collector input) {
            return input.getSearchFields();
        }
    }
}
