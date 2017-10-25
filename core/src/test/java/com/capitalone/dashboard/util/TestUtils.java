package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.EnvironmentStage;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by syq410 on 2/23/17.
 */
@Ignore
public class TestUtils {

    private static final ObjectId DASHBOARD_ID = new ObjectId();

    public static Commit createCommit(String revisionNumber,String scmUrl) {
        Commit commit = new Commit();
        commit.setScmRevisionNumber(revisionNumber);
        commit.setCollectorItemId(ObjectId.get());
        commit.setType(CommitType.New);
        commit.setScmUrl(scmUrl);
        return commit;
    }

    public static Pipeline getPipeline(ObjectId collectorItemId) {
        Pipeline pipeline = new Pipeline();
        pipeline.addCommit(PipelineStage.COMMIT.getName(), createPipelineCommit("scmRev3"));
        EnvironmentStage environmentStage = new EnvironmentStage();
        environmentStage.setLastArtifact(getBinaryArtifact());
        pipeline.getEnvironmentStageMap().put("DEV",environmentStage);
        Set<Build> failedBuilds = new HashSet<>();
        Build failedBuild = createBuild();
        failedBuild.setCollectorItemId(collectorItemId);
        failedBuilds.add(failedBuild);
        pipeline.setFailedBuilds(failedBuilds);
        return pipeline;
    }

    public static PipelineCommit createPipelineCommit(String revisionNumber) {
        PipelineCommit commit = new PipelineCommit();
        commit.setScmRevisionNumber(revisionNumber);
        return commit;
    }

    public static BinaryArtifact getBinaryArtifact() {
        BinaryArtifact binaryArtifact = new BinaryArtifact();
        binaryArtifact.setTimestamp(374268428);
        binaryArtifact.setBuildInfo(createBuild());
        return binaryArtifact;
    }

    public static Build createBuild() {
        Build build = new Build();
        build.setBuildStatus(BuildStatus.Success);
        build.setNumber("1");
        build.setCollectorItemId(new ObjectId());
        List<SCM> sourceChangeSet = new ArrayList();
        sourceChangeSet.add(getScm("scmRev1"));
        sourceChangeSet.add(getScm("scmRev2"));
        build.setSourceChangeSet(sourceChangeSet);
        build.setStartTime(12286435);
        RepoBranch repoBranch = new RepoBranch();
        repoBranch.setUrl("http://github.com/scmurl");
        List<RepoBranch> repos = new ArrayList<>();
        repos.add(repoBranch);
        build.setCodeRepos(repos);
        return build;
    }

    public static SCM getScm(String scmRevNumber) {
        SCM scm = new SCM();
        scm.setScmRevisionNumber(scmRevNumber);
        return scm;
    }

}
