package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.SonarProject;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultSonar56Client extends DefaultSonarClient {
    private static final Log LOG = LogFactory.getLog(DefaultSonar56Client.class);

    private static final String URL_PROJECTS = "/api/projects?format=json";

    @Autowired
    public DefaultSonar56Client(Supplier<RestOperations> restOperationsSupplier, SonarSettings settings) {
      super(restOperationsSupplier,settings);
    }

    @Override
    public List<SonarProject> getProjects(String instanceUrl) {
        List<SonarProject> projects = new ArrayList<>();
        String url = instanceUrl + URL_PROJECTS;
        try {

            for (Object obj : parseAsArray(url)) {
                JSONObject prjData = (JSONObject) obj;

                SonarProject project = new SonarProject();
                project.setInstanceUrl(instanceUrl);
                project.setProjectId(str(prjData, ID));
                project.setProjectName(str(prjData, "nm"));
                projects.add(project);
            }

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return projects;
    }
}
