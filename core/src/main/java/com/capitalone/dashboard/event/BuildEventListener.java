package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Build;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

public class BuildEventListener extends AbstractMongoEventListener<Build> {
    private static final Logger LOG = LoggerFactory.getLogger(BuildEventListener.class);

    @Override
    public void onAfterSave(AfterSaveEvent<Build> event) {
        super.onAfterSave(event);
        LOG.debug("Build saved: " + event.getSource().getNumber());
    }
}
