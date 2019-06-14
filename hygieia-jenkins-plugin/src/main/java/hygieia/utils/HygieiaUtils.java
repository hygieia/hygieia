package hygieia.utils;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.BuildStage;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.adapter.BuildStageAdapter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.util.Build;
import hudson.scm.SubversionSCM;
import hudson.util.IOUtils;
import jenkins.model.Jenkins;
import jenkins.plugins.hygieia.CustomObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HygieiaUtils {
    private static final Logger logger = Logger.getLogger(HygieiaUtils.class.getName());
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final String JOB_URL_SEARCH_PARM = "job/";
    public static final String SEPERATOR = ",";
    public static final String DASHBOARD_URI = "#/dashboard/";
    public static final String STAGES="stages";
    public static final String STAGE_FLOW_NODES="stageFlowNodes";
    public static final String LINKS="_links";
    public static final String LOG="log";
    public static final String HREF="href";
    public static final GsonBuilder buildStageGsonBuilder = new GsonBuilder().registerTypeAdapter(BuildStage.class, new BuildStageAdapter());

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static <T> T  convertJsonToObject(String json, Class<T> thisClass) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        return mapper.readValue(json, thisClass);
    }

    public static List<FilePath> getArtifactFiles(FilePath rootDirectory, String pattern, List<FilePath> results) throws IOException, InterruptedException {
        FileFilter filter = new WildcardFileFilter(pattern.replace("**", "*"), IOCase.SYSTEM);
        List<FilePath> temp = rootDirectory.list(filter);
        if (!CollectionUtils.isEmpty(temp)) {
            results.addAll(temp);
        }

        temp = rootDirectory.list();
        if (!CollectionUtils.isEmpty(temp)) {
            for (FilePath currentItem : rootDirectory.list()) {
                if (currentItem.isDirectory()) {
                    getArtifactFiles(currentItem, pattern, results);
                }
            }
        }
        return results;
    }

    /**
     * Determine the artifact's name. The name excludes the version string and the file extension.
     *
     * Does not currently support classifiers
     *
     * @param file
     * @param version
     * @return
     */
    public static String determineArtifactName(FilePath file, String version) {
        String fileName = file.getBaseName();

        if ("".equals(version)) return fileName;

        int vIndex = fileName.indexOf(version);
        if (vIndex <= 0) return fileName;
        if ((fileName.charAt(vIndex - 1) == '-') || (fileName.charAt(vIndex - 1) == '_')) {
            vIndex = vIndex - 1;
        }
        return fileName.substring(0, vIndex);
    }

    public static String getFileNameMinusVersion(FilePath file, String version) {
        String ext = FilenameUtils.getExtension(file.getName());
        if ("".equals(version)) return file.getName();

        int vIndex = file.getName().indexOf(version);
        if (vIndex <= 0) return file.getName();
        if ((file.getName().charAt(vIndex - 1) == '-') || (file.getName().charAt(vIndex - 1) == '_')) {
            vIndex = vIndex - 1;
        }
        return file.getName().substring(0, vIndex) + "." + ext;
    }

    public static String guessVersionNumber(String source) {
        String versionNumber = "";
        String fileName = source.substring(0, source.lastIndexOf("."));
        if (fileName.contains(".")) {
            String majorVersion = fileName.substring(0, fileName.indexOf("."));
            String minorVersion = fileName.substring(fileName.indexOf("."));
            int delimiter = majorVersion.lastIndexOf("-");
            if (majorVersion.indexOf("_") > delimiter) delimiter = majorVersion.indexOf("_");
            majorVersion = majorVersion.substring(delimiter + 1, fileName.indexOf("."));
            versionNumber = majorVersion + minorVersion;
        }
        return versionNumber;
    }

    public static String getBuildUrl(AbstractBuild<?, ?> build) {
        return build.getProject().getAbsoluteUrl() + build.getNumber() + "/";
    }

    public static String getBuildUrl(Run<?, ?> run) {
        return run.getParent().getAbsoluteUrl() + run.getNumber() + "/";
    }

    public static String getBuildNumber(AbstractBuild<?, ?> build) {
        return String.valueOf(build.getNumber());
    }

    public static String getBuildNumber(Run<?, ?> run) {
        return String.valueOf(run.getNumber());
    }

    public static String getJobUrl(AbstractBuild<?, ?> build) {
        return build.getProject().getAbsoluteUrl();
    }

    public static String getJobUrl(Run<?, ?> run) {
        return run.getParent().getAbsoluteUrl();
    }


    public static String getJobName(AbstractBuild<?, ?> build) {
        return build.getProject().getName();
    }

    public static String getJobName(Run<?, ?> run) {
        return run.getParent().getDisplayName();
    }

    public static String getJobPath(AbstractBuild<?, ?> build){
        String jobUrl = getJobUrl(build);
        if(jobUrl == null || !jobUrl.contains(JOB_URL_SEARCH_PARM))return build.getProject().getName();

        return jobUrl.substring(jobUrl.indexOf(JOB_URL_SEARCH_PARM));
    }
    public static String getJobPath(Run<?, ?> run){
        String jobUrl = getJobUrl(run);
        if(jobUrl == null || !jobUrl.contains(JOB_URL_SEARCH_PARM))return run.getParent().getDisplayName();

        return jobUrl.substring(jobUrl.indexOf(JOB_URL_SEARCH_PARM));
    }

    public static String getInstanceUrl(AbstractBuild<?, ?> build, TaskListener listener) {
        String envValue = getEnvironmentVariable(build, listener, "JENKINS_URL");

        if (envValue != null) {
            return envValue;
        } else {
            String jobPath = "/job" + "/" + build.getProject().getName() + "/";
            int ind = build.getProject().getAbsoluteUrl().indexOf(jobPath);
            return build.getProject().getAbsoluteUrl().substring(0, ind);
        }
    }

    public static String getInstanceUrl(Run<?, ?> run, TaskListener listener) {
        String envValue = getEnvironmentVariable(run, listener, "JENKINS_URL");

        if (envValue != null) {
            return envValue;
        } else {
            String jobPath = "/job" + "/" + run.getParent().getName() + "/";
            int ind = run.getParent().getAbsoluteUrl().indexOf(jobPath);
            return run.getParent().getAbsoluteUrl().substring(0, ind);
        }
    }

    public static String getScmUrl(AbstractBuild<?, ?> build, TaskListener listener) {
        if (isGitScm(build)) {
            return getEnvironmentVariable(build, listener, "GIT_URL");
        } else if (isSvnScm(build)) {
            return getEnvironmentVariable(build, listener, "SVN_URL");
        }

        return null;
    }

    public static String getScmBranch(AbstractBuild<?, ?> build, TaskListener listener) {
        if (isGitScm(build)) {
            return getEnvironmentVariable(build, listener, "GIT_BRANCH");
        } else if (isSvnScm(build)) {
            return null;
        }

        return null;
    }


    public static String getScmRevisionNumber(AbstractBuild<?, ?> build, TaskListener listener) {
        if (isGitScm(build)) {
            return getEnvironmentVariable(build, listener, "GIT_COMMIT");
        } else if (isSvnScm(build)) {
            return getEnvironmentVariable(build, listener, "SVN_REVISION");
        }

        return null;
    }

    private static boolean isGitScm(AbstractBuild<?, ?> build) {
        return "hudson.plugins.git.GitSCM".equalsIgnoreCase(build.getProject().getScm().getType());
    }


    private static boolean isSvnScm(AbstractBuild<?, ?> build) {
        return "hudson.scm.SubversionSCM".equalsIgnoreCase(build.getProject().getScm().getType());
    }

    public static EnvVars getEnvironment(Run<?, ?> run, TaskListener listener) {
        EnvVars env = null;
        try {
            env = run.getEnvironment(listener);
        } catch (IOException | InterruptedException e) {
            logger.warning("Error getting environment variables");
        }
        return env;
    }


    public static String getEnvironmentVariable(Run<?, ?> run, TaskListener listener, String key) {
        EnvVars env = getEnvironment(run, listener);
        if (env != null) {
            return env.get(key);
        } else {
            return null;
        }
    }

    /** moved from BuildBuilder class **/


    public static List<RepoBranch> getRepoBranch(AbstractBuild r) {
        List<RepoBranch> list = new ArrayList<>();
        return getRepoBranchFromScmObject(r.getProject().getScm(), r);
    }


    public static List<RepoBranch> getRepoBranch(Run run) {
        List<RepoBranch> list = new ArrayList<>();
        if (run instanceof WorkflowRun) {
            WorkflowRun r = (WorkflowRun) run;
            for (Object o : r.getParent().getSCMs()) {
                list.addAll(getRepoBranchFromScmObject(o, run));
            }
        }
        return list;
    }

    private static List<RepoBranch> getRepoBranchFromScmObject(Object scm, Run r) {
        List<RepoBranch> list = new ArrayList<>();
        if (scm instanceof SubversionSCM) {
            list = getSVNRepoBranch((SubversionSCM) scm);
        } else if (scm instanceof GitSCM) {
            list = getGitHubRepoBranch((GitSCM) scm, r);
        }
        // Removing multi SCM support. MultiSCM plugin is unstable
//        else if (scm instanceof MultiSCM) {
//            List<hudson.scm.SCM> multiScms = ((MultiSCM) scm).getConfiguredSCMs();
//            for (hudson.scm.SCM hscm : multiScms) {
//                if (hscm instanceof SubversionSCM) {
//                    list.addAll(getSVNRepoBranch((SubversionSCM) hscm));
//                } else if (hscm instanceof GitSCM) {
//                    list.addAll(getGitHubRepoBranch((GitSCM) hscm, r));
//                }
//            }
//        }
        return list;
    }


    private static List<RepoBranch> getGitHubRepoBranch(GitSCM scm, Run r) {
        List<RepoBranch> list = new ArrayList<>();
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(Objects.requireNonNull(scm.getBuildData(r)).remoteUrls)) {
            for (String url : Objects.requireNonNull(scm.getBuildData(r)).remoteUrls) {
                if (url.endsWith(".git")) {
                    url = url.substring(0, url.lastIndexOf(".git"));
                }
                Map<String, Build> branches = Objects.requireNonNull(scm.getBuildData(r)).getBuildsByBranchName();
                String branch = "";
                for (String key : branches.keySet()) {
                    hudson.plugins.git.util.Build b = branches.get(key);
                    if (b.hudsonBuildNumber == r.getNumber()) {
                        branch = key;
                    }
                }
                list.add(new RepoBranch(url, branch, RepoBranch.RepoType.GIT));
            }
        }
        return list;
    }

    private static List<RepoBranch> getSVNRepoBranch(SubversionSCM scm) {
        List<RepoBranch> list = new ArrayList<>();
        SubversionSCM.ModuleLocation[] mLocations = scm.getLocations();
        if (mLocations != null) {
            for (SubversionSCM.ModuleLocation mLocation : mLocations) {
                list.add(new RepoBranch(mLocation.getURL(), "", RepoBranch.RepoType.SVN));
            }
        }
        return list;
    }

    public static int getSafePositiveInteger(String value, int defaultValue) {
        int returnValue = defaultValue;
        if (value != null) {
            try {
                returnValue = Integer.parseInt(value.trim());
                if (returnValue < 0) {
                    returnValue = defaultValue;
                }
            } catch (java.lang.NumberFormatException nfe) {
                //do nothing. will return default at the end.
            }
        }
        return returnValue;
    }

    public static BuildStatus getBuildStatus (Result result) {

        if(Objects.equals(Result.SUCCESS,result))
        {
            return BuildStatus.Success ;
        }
        else if(Objects.equals(Result.ABORTED, result))
        {
            return BuildStatus.Aborted;
        }
        else if(Objects.equals(Result.UNSTABLE, result)) {
            return BuildStatus.Unstable;
        }
        else if(Objects.equals(Result.FAILURE, result)) {
            return BuildStatus.Failure;
        }
        else {
            return BuildStatus.Unknown;
        }
    }

    public static String getMatchFromLog(Run run, String pattern) throws IOException {
        BufferedReader br = null;
        String matchLine = null;
        try {
            br = new BufferedReader(run.getLogReader());
            String strLine;
            Pattern p = Pattern.compile(pattern);
            while ((strLine = br.readLine()) != null) {
                Matcher match = p.matcher(strLine);
                if (match.matches()) {
                    matchLine = match.group(1);
                }
            }
        } finally {
            IOUtils.closeQuietly(br);
        }
        return matchLine;
    }

    public static Set<String> getMatchedLinesFromLog(Run run, String pattern) throws IOException {
        BufferedReader br = null;
        Set<String> matchLines = new HashSet<>();
        try {
            br = new BufferedReader(run.getLogReader());
            String strLine;
            Pattern p = Pattern.compile(pattern);
            while ((strLine = br.readLine()) != null) {
                Matcher match = p.matcher(strLine);
                if (match.matches()) {
                    matchLines.add(match.group(1));
                }
            }
        } finally {
            IOUtils.closeQuietly(br);
        }
        return matchLines;
    }

    public static String getBuildCollectionId (String buildResponse) {
        String[] parts = buildResponse.split(",");
        return parts[0];
    }

    public static String getCollectorItemId (String buildResponse) {
        String[] parts = buildResponse.split(",");
        if (parts.length < 2) return "";
        return parts[1];
    }

    public static boolean isJobExcluded (String jobName, String patterns) {
        if(StringUtils.isNotBlank(patterns)){
            String[] patternsList = patterns.split(SEPERATOR);
            for (String pattern : patternsList) {
                if (StringUtils.startsWithIgnoreCase(jobName, pattern)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public static LinkedList<BuildStage> getBuildStages(String responseJSON) throws HygieiaException{
        if(responseJSON==null) return new LinkedList<>();
        try{
            JSONObject buildJSON = (JSONObject) new JSONParser().parse(responseJSON);
            if(Objects.isNull(buildJSON)) return new LinkedList<>();
            JSONArray stages = (JSONArray) buildJSON.get(STAGES);
            if (stages == null) return new LinkedList<>();
            LinkedList<BuildStage> buildStages = new LinkedList<>();
            for (Object stage: stages) {
                JSONObject j =(JSONObject) stage;
                Gson gson = buildStageGsonBuilder.create();
                BuildStage bs = gson.fromJson(j.toJSONString(), BuildStage.class);
                buildStages.add(bs);
            }
            return buildStages;
        }catch (ParseException parseException){
            logger.log(Level.INFO,ExceptionUtils.getStackTrace(parseException));
            throw new HygieiaException("Error parsing stage information", HygieiaException.JSON_FORMAT_ERROR);
        }catch (Exception ex){
            logger.log(Level.INFO,ExceptionUtils.getStackTrace(ex));
            throw new HygieiaException("Error in method :: HygieiaUtils.getBuildStages() :: ", HygieiaException.BAD_DATA);
        }
    }

    public static BuildStage setLogUrl(String responseJSON, BuildStage stage){
        if(responseJSON==null) return stage;
        try{
            JSONObject stageJSON = (JSONObject) new JSONParser().parse(responseJSON);
            String url = getLogUrl(stageJSON);
            stage.setExec_node_logUrl(url);
        }catch (ParseException parseException){
            logger.log(Level.INFO,ExceptionUtils.getStackTrace(parseException));
        }
        return stage;
    }

    public static String getLogUrl(JSONObject jsonObject){
        JSONArray stageFlowNodes = (JSONArray) jsonObject.get(STAGE_FLOW_NODES);
        if (CollectionUtils.isEmpty(stageFlowNodes)) return null;
        JSONObject firstNode = (JSONObject) stageFlowNodes.get(0);
        JSONObject _links = (JSONObject) firstNode.get(LINKS);
        String url = getLog_href(_links);
        return url;
    }

    public static String getLog_href(JSONObject jsonObject){
        JSONObject logUrl = (JSONObject) jsonObject.get(LOG);
        String url = (String) logUrl.get(HREF);
        return url;
    }

    public static BuildStage set_logs(String responseJSON, BuildStage stage){
        if (responseJSON==null) return stage;
        try{
            JSONObject logJSON = (JSONObject) new JSONParser().parse(responseJSON);
            stage.setLog(logJSON!=null?logJSON.toJSONString():"");
        }catch (ParseException parseException){
            logger.log(Level.SEVERE,ExceptionUtils.getStackTrace(parseException));
        }
        return stage;
    }

    public static String getUserID(@Nonnull Run run, TaskListener listener) {
        // If build has been triggered form an upstream build, get UserCause from there to set user build variables
        Cause.UpstreamCause upstreamCause = (Cause.UpstreamCause) run.getCause(Cause.UpstreamCause.class);

        if (upstreamCause != null) {
            Job job = Jenkins.getInstance().getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
            if (job != null) {
                Run upstream = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
                if (upstream != null) {
                    getUserID(upstream, listener);
                }
            }
        }

        Cause.UserIdCause userIdCause = (Cause.UserIdCause) run.getCause(Cause.UserIdCause.class);
        String userId = "";
        if (userIdCause != null) {
            userId = StringUtils.trimToEmpty(userIdCause.getUserId());
        }
        return StringUtils.isEmpty(userId) ? "anonymous" : userId;
    }


}
