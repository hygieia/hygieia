package com.capitalone.dashboard.event;

import com.capitalone.dashboard.model.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;


public class CommitEventListener extends AbstractMongoEventListener<Commit> {
    private static final Logger LOG = LoggerFactory.getLogger(CommitEventListener.class);

    @Override
    public void onAfterSave(AfterSaveEvent<Commit> event) {
        super.onAfterSave(event);
        LOG.debug("Commit saved: " + event.getSource().getScmRevisionNumber());
    }
}
