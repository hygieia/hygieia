package com.capitalone.dashboard.jenkins;

import javax.print.attribute.standard.JobName;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsPredicate {

    public static Predicate<JenkinsJob> artefactContaining(List<Pattern> patterns) {
        return job ->
            job.getArtefacts().stream().anyMatch(
                    artefact -> patterns.stream().anyMatch(
                            pattern -> pattern.asPredicate().test(artefact.getName())
                    )
            ) ;
    }

}
