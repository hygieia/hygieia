package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Incident;

public interface HpsmIncidentUpdateClient {
    Incident getIncident(String incidentId) throws HygieiaException;
}
