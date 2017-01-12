package hygieia.utils;

import com.capitalone.dashboard.model.RepoBranch;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.util.Build;
import hudson.scm.SubversionSCM;
import jenkins.plugins.hygieia.CustomObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.multiplescms.MultiSCM;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.springframework.util.CollectionUtils;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class HygieiaUtils {
    private static final Logger logger = Logger.getLogger(HygieiaUtils.class.getName());
    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static Object convertJsonToObject(String json, Class thisClass) throws IOException {
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
    	return build.getProject().getAbsoluteUrl() + String.valueOf(build.getNumber()) + "/";
    }

    public static String getBuildUrl(Run<?, ?> run) {
        return run.getParent().getAbsoluteUrl() + String.valueOf(run.getNumber()) + "/";
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
    
    public static boolean isGitScm(AbstractBuild<?, ?> build) {
    	return "hudson.plugins.git.GitSCM".equalsIgnoreCase(build.getProject().getScm().getType());
    }


    public static boolean isSvnScm(AbstractBuild<?, ?> build) {
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
        } else if (scm instanceof MultiSCM) {
            List<hudson.scm.SCM> multiScms = ((MultiSCM) scm).getConfiguredSCMs();
            for (hudson.scm.SCM hscm : multiScms) {
                if (hscm instanceof SubversionSCM) {
                    list.addAll(getSVNRepoBranch((SubversionSCM) hscm));
                } else if (hscm instanceof GitSCM) {
                    list.addAll(getGitHubRepoBranch((GitSCM) hscm, r));
                }
            }
        }
        return list;
    }


    private static List<RepoBranch> getGitHubRepoBranch(GitSCM scm, Run r) {
        List<RepoBranch> list = new ArrayList<>();
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(scm.getBuildData(r).remoteUrls)) {
            for (String url : scm.getBuildData(r).remoteUrls) {
                if (url.endsWith(".git")) {
                    url = url.substring(0, url.lastIndexOf(".git"));
                }
                Map<String, Build> branches = scm.getBuildData(r).getBuildsByBranchName();
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
            for (int i = 0; i < mLocations.length; i++) {
                list.add(new RepoBranch(mLocations[i].getURL(), "", RepoBranch.RepoType.SVN));
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

}
