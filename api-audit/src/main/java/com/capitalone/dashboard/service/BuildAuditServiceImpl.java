package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.BuildAuditResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuildAuditServiceImpl implements BuildAuditService {



//    private static final Log LOGGER = LogFactory.getLog(BuildAuditServiceImpl.class);

    @Autowired
    public BuildAuditServiceImpl() {

    }


    @Override
    public BuildAuditResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt) {
        return null;
    }
}
