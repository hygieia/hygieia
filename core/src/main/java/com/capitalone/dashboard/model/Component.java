package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

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

    public void addCollectorItem(CollectorType collectorType, CollectorItem collectorItem) {
        // Currently only one collectorItem per collectorType is supported
        if (collectorItems.get(collectorType) == null) {
            collectorItems.put(collectorType, Arrays.asList(collectorItem));
        } else {
            List<CollectorItem> existing = collectorItems.get(collectorType);
            List<CollectorItem> newList = new ArrayList<>(existing);
            if (isNewCollectorItem(existing, collectorItem)) {
                newList.add(collectorItem);
                collectorItems.replace(collectorType, newList);
            }
        }
    }

    private boolean isNewCollectorItem (List<CollectorItem> existing, CollectorItem item) {
        for (CollectorItem ci : existing) {
            if (ci.getId().equals(item.getId())) return false;
        }
        return true;
    }
}
