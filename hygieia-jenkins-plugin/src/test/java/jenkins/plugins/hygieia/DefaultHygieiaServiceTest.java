package jenkins.plugins.hygieia;

import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.request.BuildDataCreateRequest;
import org.junit.Test;

public class DefaultHygieiaServiceTest {

    /**
     * Publish should generally not rethrow exceptions, or it will cause a build job to fail at end.
     */
    @Test
    public void publishWithBadHostShouldNotRethrowExceptions() {
        DefaultHygieiaService service = new DefaultHygieiaService("foo", "token", "myname", false);
        service.setHygieiaAPIUrl("hostvaluethatwillcausepublishtofail");

        service.publishBuildData(makeBuildDataRequestData());
    }


    /**
     * Use a valid team domain, but a bad token
     */
    @Test
    public void invalidTokenShouldFail() {
        DefaultHygieiaService service = new DefaultHygieiaService("tinyspeck", "token", "myname", false);
        service.publishBuildData(makeBuildDataRequestData());
    }


//    @Test
//    public void successfulPublishBuildDataReturnsTrue() {
//        DefaultHygieiaServiceStub service = new DefaultHygieiaServiceStub("domain", "token");
//        HttpClientStub httpClientStub = new HttpClientStub();
//        httpClientStub.setHttpStatus(HttpStatus.SC_OK);
//        service.setHttpClient(httpClientStub);
//        assertTrue(service.publishBuildData(makeBuildDataRequestData()));
//    }

    private BuildDataCreateRequest makeBuildDataRequestData() {
        BuildDataCreateRequest build = new BuildDataCreateRequest();
        build.setNumber("1");
        build.setBuildUrl("buildUrl");
        build.setStartTime(3);
        build.setEndTime(8);
        build.setDuration(5);
        build.setBuildStatus("Success");
        build.setStartedBy("foo");
        build.setJobName("MyJob");
        build.getSourceChangeSet().add(makeScm());
        return build;
    }

    private SCM makeScm() {
        SCM scm = new SCM();
        scm.setScmUrl("scmUrl");
        scm.setScmRevisionNumber("revNum");
        scm.setNumberOfChanges(20);
        scm.setScmCommitTimestamp(200);
        scm.setScmCommitLog("Log message");
        scm.setScmAuthor("bob");
        return scm;
    }
}
