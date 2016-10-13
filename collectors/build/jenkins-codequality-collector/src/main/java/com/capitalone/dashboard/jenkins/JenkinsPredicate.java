package com.capitalone.dashboard.jenkins;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsPredicate {

    public static Predicate<JenkinsJob> artifactInJobContaining(List<Pattern> patterns) {
        return job ->
                null != job.getLastSuccessfulBuild() && job.getLastSuccessfulBuild().getArtifacts().stream().anyMatch(
                        JenkinsPredicate.artifactContaining(patterns)
                );
    }

    public static Predicate<Artifact> artifactContaining(List<Pattern> patterns) {
        return artifact -> patterns.stream().anyMatch(
                pattern -> pattern.asPredicate().test(artifact.getName()));
    }

}
