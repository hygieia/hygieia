package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A self-contained, independently deployable piece of the larger application. Each component of an application
 * has a different source repo, build job, deploy job, etc.
 *
 */
@Document(collection="components")
public class Component extends BaseModel {
    private String name; // must be unique to the application
    private String owner;
    private Map<CollectorType, List<CollectorItem>> collectorItems = new HashMap<>();

    public Component() {
    }

    public Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Map<CollectorType, List<CollectorItem>> getCollectorItems() {
        return collectorItems;
    }

    public List<CollectorItem> getCollectorItems(CollectorType type) {
        return collectorItems.get(type);
    }

    /**
     * Note multiple and repeated collector items need to be allowed here so that multiple widgets can
     * have the same collector item, and when we add/remove them we don't delete them all.
     * @param collectorType
     * @param collectorItem
     */
    public void addCollectorItem(CollectorType collectorType, CollectorItem collectorItem) {
        // Currently only one collectorItem per collectorType is supported
        if (collectorItems.get(collectorType) == null) {
            List<CollectorItem> newList = new ArrayList<>();
            newList.add(collectorItem);
            collectorItems.put(collectorType,newList);
        } else {
            List<CollectorItem> existing = new ArrayList<> (collectorItems.get(collectorType));
            existing.add(collectorItem);
            collectorItems.put(collectorType, existing);
        }
    }

    public void updateCollectorItem(CollectorType collectorType, CollectorItem collectorItem) {
            List<CollectorItem> existing = new ArrayList<> (collectorItems.get(collectorType));
            if (!isNewCollectorItem(existing, collectorItem)) {
                findCollectorItem(existing,collectorItem).setLastUpdated(collectorItem.getLastUpdated());
            }
    }

    private boolean isNewCollectorItem (List<CollectorItem> existing, CollectorItem item) {
        return existing.stream().noneMatch(ci -> Objects.equals(ci.getId(), item.getId()));
    }

    private CollectorItem findCollectorItem (List<CollectorItem> existing, CollectorItem item) {
        return existing.stream().filter(ci-> Objects.equals(ci.getId(),item.getId())).findFirst().orElse(null);
    }

    public CollectorItem getFirstCollectorItemForType(CollectorType type){

        if(getCollectorItems().get(type) == null) {
            return null;
        }
        List<CollectorItem> collectorItems = new ArrayList<>(getCollectorItems().get(type));
        return collectorItems.get(0);
    }

    public CollectorItem getCollectorItemMatchingTypeAndCollectorItemId(CollectorType type, ObjectId... collectorItemIds) {
        List<ObjectId> inputList = Arrays.asList(collectorItemIds);
        List<CollectorItem> collectorItems = getCollectorItems().get(type);
        if (null == collectorItems ) {
            return  null;
        }
        Optional<CollectorItem> found = collectorItems.stream().filter(item -> inputList.contains(item.getId())).findFirst();
        return found.isPresent() ? found.get() : null;
    }


    public CollectorItem getLastUpdatedCollectorItemForType(CollectorType type){

        if(getCollectorItems().get(type) == null || getCollectorItems().get(type).isEmpty()) {
            return null;
        }
        List<CollectorItem> collectorItems = new ArrayList<>(getCollectorItems().get(type));
        return getLastUpdateItem(collectorItems);
    }

    private CollectorItem getLastUpdateItem(List<CollectorItem> collectorItems){
        Comparator<CollectorItem> collectorItemComparator = Comparator.comparing(CollectorItem::getLastUpdated);
        collectorItems.sort(collectorItemComparator.reversed());
        return collectorItems.get(0);
    }

}
