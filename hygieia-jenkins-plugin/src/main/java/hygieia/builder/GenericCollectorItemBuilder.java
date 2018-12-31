package hygieia.builder;

import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import hudson.model.Run;
import hygieia.utils.HygieiaUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class GenericCollectorItemBuilder {

    private String buildId;
    private String toolName;
    private String pattern;

    private Run<?, ?> run;

    public GenericCollectorItemBuilder(Run<?, ?> run, String toolName, String pattern, String buildId) {
        this.buildId = buildId;
        this.toolName = toolName;
        this.pattern = ".*" + Pattern.quote(pattern) + "(.*)";
        this.run = run;
    }

    public List<GenericCollectorItemCreateRequest> getRequests() throws IOException {
        List<GenericCollectorItemCreateRequest> requests = new ArrayList<>();

        Set<String> matchedData = HygieiaUtils.getMatchedLinesFromLog(run, pattern);
        if (CollectionUtils.isEmpty(matchedData)) return requests;
        for (String line: matchedData) {
            GenericCollectorItemCreateRequest gc = new GenericCollectorItemCreateRequest();
            gc.setRelatedCollectorItemId(HygieiaUtils.getCollectorItemId(buildId));
            gc.setRawData(line.trim());
            gc.setBuildId(HygieiaUtils.getBuildCollectionId(buildId));
            gc.setSource(run.getParent().getAbsoluteUrl());
            gc.setToolName(toolName);
            requests.add(gc);
        }
        return requests;
    }
}
