package hygieia.builder;

import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import hudson.model.Run;
import hygieia.utils.HygieiaUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class GenericCollectorItemBuilder {

    private GenericCollectorItemBuilder() {
    }

    public static GenericCollectorItemBuilder getInstance() {
        return new GenericCollectorItemBuilder();
    }

    public List<GenericCollectorItemCreateRequest> getRequests(@NotNull Run<?, ?> run,@NotNull String toolName,@NotNull String pattern, String buildId) throws IOException {
        List<GenericCollectorItemCreateRequest> requests = new ArrayList<>();
        pattern = ".*" + Pattern.quote(pattern) + "(.*)";
        Set<String> matchedData = HygieiaUtils.getMatchedLinesFromLog(run, pattern);
        if (CollectionUtils.isEmpty(matchedData)) return requests;
        for (String line: matchedData) {
            GenericCollectorItemCreateRequest gc = new GenericCollectorItemCreateRequest();
            gc.setRelatedCollectorItemId(HygieiaUtils.getCollectorItemId(buildId));
            gc.setRawData(line.trim());
            gc.setBuildId(HygieiaUtils.getBuildCollectionId(buildId));
            gc.setSource(run.getParent().getAbsoluteUrl());
            gc.setToolName(toolName);
            gc.setPattern(pattern);
            requests.add(gc);
        }
        return requests;
    }
}
