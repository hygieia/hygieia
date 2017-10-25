package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.repository.CommitRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PipelineUtils {

    private PipelineUtils(){

    }

    public static Map<String, PipelineCommit> commitSetToMap(Set<PipelineCommit> set){
        Map<String, PipelineCommit> returnMap = new HashMap<>();
        for(PipelineCommit commit : set){
            returnMap.put(commit.getScmRevisionNumber(), commit);
        }
        return returnMap;
    }

    public static Map<PipelineStage, String> getStageToEnvironmentNameMap(Dashboard dashboard) {
        Map<PipelineStage, String> rt = new LinkedHashMap<>();

        for(Widget widget : dashboard.getWidgets()) {
            if(widget.getName().equalsIgnoreCase("build")){
                rt.put(PipelineStage.valueOf("Build"), "Build");
            }if(widget.getName().equalsIgnoreCase("repo")){
                rt.put(PipelineStage.valueOf("Commit"), "Commit");
            }
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                Map<?,?> gh = (Map<?,?>) widget.getOptions().get("mappings");
                for (Map.Entry<?, ?> entry : gh.entrySet()) {
                    rt.put(PipelineStage.valueOf((String) entry.getKey()), (String) entry.getValue());

                }

            }
        }

        return rt;
    }

    public static Map<String, String> getOrderForStages(Dashboard dashboard) {
        Map<String, String> rt = new LinkedHashMap<>();
        rt.put("0", "Commit");
        rt.put("1", "Build");
        for(Widget widget : dashboard.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                Map<?,?> gh = (Map<?,?>) widget.getOptions().get("order");
                int count = 2;
                if(gh!=null) {
                    for (Map.Entry<?, ?> entry : gh.entrySet()) {
                        rt.put(Integer.parseInt((String) entry.getKey())+count+"", (String) entry.getValue());
                    }
                }

            }
        }

        return rt;
    }

    public static String getProdStage(Dashboard dashboard) {
        String prodStage = "";
        for(Widget widget : dashboard.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                prodStage =  (String)widget.getOptions().get("prod");
            }
        }
        return prodStage;
    }

    public static void setStageToEnvironmentNameMap(Dashboard dashboard, Map<PipelineStage, String> map) {
        Map<String, String> mappingsMap = new HashMap<>();

        for (Map.Entry<PipelineStage, String> e : map.entrySet()) {
            if (PipelineStage.BUILD.equals(e.getKey()) || PipelineStage.COMMIT.equals(e.getKey())) {
                continue;
            }

            mappingsMap.put(e.getKey().getName().toLowerCase(), e.getValue());
        }

        for(Widget widget : dashboard.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {

                widget.getOptions().put("mappings", mappingsMap);
            }
        }
    }

    /**
     * Iterate over failed builds, if the failed build collector item id matches the successful builds collector item id
     * take all the commits from the changeset of the failed build and add them to the pipeline.
     * Then remove the failed build from the collection after it has been processed.
     *
     * @param successfulBuild
     * @param pipeline
     */
    public static void processPreviousFailedBuilds(Build successfulBuild, Pipeline pipeline) {

        if (!pipeline.getFailedBuilds().isEmpty()) {
            Iterator<Build> failedBuilds = pipeline.getFailedBuilds().iterator();

            while (failedBuilds.hasNext()) {
                Build b = failedBuilds.next();
                if (b.getCollectorItemId().equals(successfulBuild.getCollectorItemId())) {
                    for (SCM scm : b.getSourceChangeSet()) {
                        PipelineCommit failedBuildCommit = new PipelineCommit(scm, successfulBuild.getStartTime());
                        pipeline.addCommit(PipelineStage.BUILD.getName(), failedBuildCommit);
                    }
                    failedBuilds.remove();
                }
            }
        }
    }


    public static boolean isMoveCommitToBuild(Build build, SCM scm, CommitRepository commitRepository) {
        List<Commit> commitsFromRepo = getCommitsFromCommitRepo(scm, commitRepository);
        List<RepoBranch> codeReposFromBuild = build.getCodeRepos();
        Set<String> codeRepoUrlsFromCommits = new HashSet<>();
        for (Commit c : commitsFromRepo) {
            codeRepoUrlsFromCommits.add(getRepoNameOnly(c.getScmUrl()));
        }

        for (RepoBranch rb : codeReposFromBuild) {
            if (codeRepoUrlsFromCommits.contains(getRepoNameOnly(rb.getUrl()))) {
                return true;
            }
        }
        return false;
    }

    private static List<Commit> getCommitsFromCommitRepo(SCM scm, CommitRepository commitRepository) {
        return commitRepository.findByScmRevisionNumber(scm.getScmRevisionNumber());
    }

    private static String getRepoNameOnly(String url) {
        try {
            URL temp = new URL(url);
            return temp.getHost() + temp.getPath();
        } catch (MalformedURLException e) {
            return url;
        }
    }
}
