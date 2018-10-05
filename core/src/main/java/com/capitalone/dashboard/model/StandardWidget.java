package com.capitalone.dashboard.model;

import org.apache.commons.collections.MapUtils;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class StandardWidget {
    private CollectorType collectorType;
    private static final String REPO = "repo";
    private static final String BUILD = "build";
    private static final String CODEQUALITY = "codeanalysis";
    private static final String FEATURE = "feature";
    private static final String DEPLOY = "deploy";

    private static final String REPO_ID = "repo0";
    private static final String BUILD_ID = "build0";
    private static final String CODEQUALITY_ID = "codeanalysis0";
    private static final String FEATURE_ID = "feature0";
    private static final String DEPLOY_ID = "deploy0";

    private Map<String, Object> options = new HashMap<>();
    private String name;
    private ObjectId componentId;

    public StandardWidget(CollectorType collectorType, ObjectId componentId) {
        this.collectorType = collectorType;
        this.componentId = componentId;
        setNameAndId();
    }

    /**
     * Setup the standard name, id etc for the widget
     */

    private void setNameAndId() {
        switch (collectorType) {
            case SCM:
                name = REPO;
                options.put("id", REPO_ID);
                break;

            case Build:
                name = BUILD;
                options.put("id", BUILD_ID);
                options.put("buildDurationThreshold", 3);
                options.put("consecutiveFailureThreshold", 5);
                break;

            case Deployment:
                name = DEPLOY;
                options.put("id", DEPLOY_ID);
                break;

            case AgileTool:
                name = FEATURE;
                options.put("id", FEATURE_ID);
                break;

            case CodeQuality:
            case Test:
            case StaticSecurityScan:
            case LibraryPolicy:
            case AppPerformance:
                name =CODEQUALITY;
                options.put("id", CODEQUALITY_ID);
                break;

            default:
                break;
        }
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public String getName() {
        return name;
    }

    public Widget getWidget() {
        Widget widget = new Widget();
        widget.setId(ObjectId.get());
        widget.setName(name);
        widget.setComponentId(componentId);
        if ((options != null) && !options.isEmpty()) {
            widget.getOptions().putAll(options);
        }
        return widget;
    }
}
