package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.request.TestDataCreateRequest;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.git.GitSCM;
import hudson.scm.SubversionSCM;
import hygieia.builder.ArtifactBuilder;
import hygieia.builder.CommitBuilder;
import hygieia.builder.CucumberTestBuilder;
import hygieia.builder.DeployBuilder;
import hygieia.builder.SonarBuilder;
import hygieia.utils.HygieiaUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.jenkinsci.plugins.multiplescms.MultiSCM;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public class ActiveNotifier implements FineGrainedNotifier {

    private static final Logger logger = Logger.getLogger(HygieiaListener.class.getName());

    private HygieiaPublisher publisher;
    private BuildListener listener;

    public ActiveNotifier(HygieiaPublisher publisher, BuildListener listener) {
        super();
        this.publisher = publisher;
        this.listener = listener;
    }

    private HygieiaService getHygieiaService(AbstractBuild r) {
        return publisher.newHygieiaService(r, listener);
    }

    public void started(AbstractBuild r) {
        boolean publish = (publisher.getHygieiaArtifact() != null) ||
                ((publisher.getHygieiaBuild() != null) && publisher.getHygieiaBuild().isPublishBuildStart()) ||
                ((publisher.getHygieiaTest() != null) && publisher.getHygieiaTest().isPublishTestStart()) ||
                ((publisher.getHygieiaSonar() != null) && publisher.getHygieiaSonar().isPublishBuildStart()) ||
                ((publisher.getHygieiaDeploy() != null) && publisher.getHygieiaDeploy().isPublishDeployStart());


        if (publish) {
            HygieiaResponse response = getHygieiaService(r).publishBuildData(getBuildData(r, false));
            if (response.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Published Build Complete Data. " + response.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + response.toString());
            }
        }

    }

    public void deleted(AbstractBuild r) {
    }


    public void finalized(AbstractBuild r) {

    }

    public void completed(AbstractBuild r) {
        boolean publishBuild = (publisher.getHygieiaArtifact() != null) || (publisher.getHygieiaSonar() != null) ||
                (publisher.getHygieiaBuild() != null) || (publisher.getHygieiaTest() != null) || (publisher.getHygieiaDeploy() != null);

        if (publishBuild) {
            HygieiaResponse buildResponse = getHygieiaService(r).publishBuildData(getBuildData(r, true));
            if (buildResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                listener.getLogger().println("Hygieia: Published Build Complete Data. " + buildResponse.toString());
            } else {
                listener.getLogger().println("Hygieia: Failed Publishing Build Complete Data. " + buildResponse.toString());
            }

            boolean successBuild = ("success".equalsIgnoreCase(r.getResult().toString()) ||
                    "unstable".equalsIgnoreCase(r.getResult().toString()));
            boolean publishArt = (publisher.getHygieiaArtifact() != null) && successBuild;

            if (publishArt) {
                ArtifactBuilder builder = new ArtifactBuilder(r, publisher, listener, buildResponse.getResponseValue());
                Set<BinaryArtifactCreateRequest> requests = builder.getArtifacts();
                for (BinaryArtifactCreateRequest bac : requests) {
                    HygieiaResponse artifactResponse = getHygieiaService(r).publishArtifactData(bac);
                    if (artifactResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                        listener.getLogger().println("Hygieia: Published Build Artifact Data. Filename=" +
                                bac.getCanonicalName() + ", Name=" + bac.getArtifactName() + ", Version=" + bac.getArtifactVersion() +
                                ", Group=" + bac.getArtifactGroup() + ". " + artifactResponse.toString());
                    } else {
                        listener.getLogger().println("Hygieia: Failed Publishing Build Artifact Data. " + bac.getCanonicalName() + ", Name=" + bac.getArtifactName() + ", Version=" + bac.getArtifactVersion() +
                                ", Group=" + bac.getArtifactGroup() + ". " + artifactResponse.toString());
                    }
                }
            }

            boolean publishTest = (publisher.getHygieiaTest() != null) && (successBuild || publisher.getHygieiaTest().isPublishEvenBuildFails());

            if (publishTest) {
                CucumberTestBuilder builder = new CucumberTestBuilder(r, publisher, listener, buildResponse.getResponseValue());
                TestDataCreateRequest request = builder.getTestDataCreateRequest();
                if (request != null) {
                    HygieiaResponse testResponse = getHygieiaService(r).publishTestResults(request);
                    if (testResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                        listener.getLogger().println("Hygieia: Published Test Data. " + testResponse.toString());
                    } else {
                        listener.getLogger().println("Hygieia: Failed Publishing Test Data. " + testResponse.toString());
                    }
                } else {
                    listener.getLogger().println("Hygieia: Published Test Data. Nothing to publish");
                }
            }

            boolean publishSonar = (publisher.getHygieiaSonar() != null) && successBuild;

            if (publishSonar) {
                try {
                    SonarBuilder builder = new SonarBuilder(r, publisher, listener, buildResponse.getResponseValue());
                    CodeQualityCreateRequest request = builder.getSonarMetrics();
                    if (request != null) {
                        HygieiaResponse sonarResponse = getHygieiaService(r).publishSonarResults(request);
                        if (sonarResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                            listener.getLogger().println("Hygieia: Published Sonar Data. " + sonarResponse.toString());
                        } else {
                            listener.getLogger().println("Hygieia: Failed Publishing Sonar Data. " + sonarResponse.toString());
                        }
                    } else {
                        listener.getLogger().println("Hygieia: Published Sonar Result. Nothing to publish");
                    }
                } catch (IOException | URISyntaxException | ParseException e) {
                    listener.getLogger().println("Hygieia: Publishing error" + '\n' + e.getMessage());
                }

            }

            boolean publishDeploy = (publisher.getHygieiaDeploy() != null) && successBuild;
            if (publishDeploy) {
                DeployBuilder builder = new DeployBuilder(r, publisher, listener, buildResponse.getResponseValue());
                Set<DeployDataCreateRequest> requests = builder.getDeploys();
                for (DeployDataCreateRequest bac : requests) {
                    HygieiaResponse deployResponse = getHygieiaService(r).publishDeployData(bac);
                    if (deployResponse.getResponseCode() == HttpStatus.SC_CREATED) {
                        listener.getLogger().println("Hygieia: Published Deploy Data: " + deployResponse.toString());
                    } else {
                        listener.getLogger().println("Hygieia: Failed Publishing Deploy Data:" + deployResponse.toString());
                    }
                }
            }
        }
    }

    private BuildDataCreateRequest getBuildData(AbstractBuild r, boolean isComplete) {
        BuildDataCreateRequest request = new BuildDataCreateRequest();
        request.setNiceName(publisher.getDescriptor().getHygieiaJenkinsName());
        request.setJobName(HygieiaUtils.getJobName(r));
        request.setBuildUrl(HygieiaUtils.getBuildUrl(r));
        request.setJobUrl(HygieiaUtils.getJobUrl(r));
        request.setInstanceUrl(HygieiaUtils.getInstanceUrl(r, listener));
        request.setNumber(HygieiaUtils.getBuildNumber(r));
        request.setStartTime(r.getStartTimeInMillis());
        request.setCodeRepos(getRepoBranch(r));
        request.setSourceChangeSet(getCommitList(r));

        if (isComplete) {
            request.setBuildStatus(r.getResult().toString());
            request.setDuration(r.getDuration());
            request.setEndTime(r.getStartTimeInMillis() + r.getDuration());
        } else {
            request.setBuildStatus("InProgress");
        }
        return request;
    }

    private List<SCM> getCommitList(AbstractBuild r) {
        CommitBuilder commitBuilder = new CommitBuilder(r);
        return commitBuilder.getCommits();
    }

    private List<RepoBranch> getRepoBranch(AbstractBuild r) {
        List<RepoBranch> list = new ArrayList<>();
        if (r.getProject().getScm() instanceof SubversionSCM) {
            list = getSVNRepoBranch((SubversionSCM) r.getProject().getScm(), r);
        } else if (r.getProject().getScm() instanceof GitSCM) {
            list = getGitHubRepoBranch((GitSCM) r.getProject().getScm(), r);
        } else if (r.getProject().getScm() instanceof MultiSCM) {
            List<hudson.scm.SCM> multiScms = ((MultiSCM) r.getProject().getScm()).getConfiguredSCMs();
            for (hudson.scm.SCM scm : multiScms) {
                if (scm instanceof SubversionSCM) {
                    list.addAll(getSVNRepoBranch((SubversionSCM) scm, r));
                } else if (scm instanceof GitSCM) {
                    list.addAll(getGitHubRepoBranch((GitSCM) scm, r));
                }
            }
        }
        return list;
    }

    private List<RepoBranch> getGitHubRepoBranch(GitSCM scm, AbstractBuild r) {
        List<RepoBranch> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(scm.getBuildData(r).remoteUrls)) {
            for (String url : scm.getBuildData(r).remoteUrls) {
                if (url.endsWith(".git")) {
                    url =  url.substring(0, url.lastIndexOf(".git"));
                }
                list.add(new RepoBranch(url, "", RepoBranch.RepoType.GIT));
            }
        }
        return list;
    }

    private List<RepoBranch> getSVNRepoBranch(SubversionSCM scm, AbstractBuild r) {
        List<RepoBranch> list = new ArrayList<>();
        SubversionSCM.ModuleLocation[] mLocations = scm.getLocations();
        if (mLocations != null) {
            for (int i = 0; i < mLocations.length; i++) {
                list.add(new RepoBranch(mLocations[i].getURL(), "", RepoBranch.RepoType.SVN));
            }
        }
        return list;
    }
}
