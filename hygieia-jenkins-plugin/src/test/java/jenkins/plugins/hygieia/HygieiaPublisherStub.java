package jenkins.plugins.hygieia;

public class HygieiaPublisherStub extends HygieiaPublisher {

    public HygieiaPublisherStub(HygieiaBuild buildStub, HygieiaTest testStub, HygieiaArtifactStub artifactStub, HygieiaSonar sonarStub, HygieiaDeploy deployStub) {
        super(buildStub, testStub, artifactStub, sonarStub, deployStub );
    }

    public static class DescriptorImplStub extends HygieiaPublisher.DescriptorImpl {

        private HygieiaService hygieiaService;

        @Override
        public synchronized void load() {
        }

        @Override
        public HygieiaService getHygieiaService(final String host, final String authToken, final String jenkinsName, final boolean useProxy) {
            return hygieiaService;
        }

        public void setHygieiaService(HygieiaService hygieiaService) {
            this.hygieiaService = hygieiaService;
        }
    }

    public static class HygieiaArtifactStub extends HygieiaArtifact {
        public HygieiaArtifactStub (String artifactDirectory, String artifactName, String artifactGroup, String artifactVersion ) {
            super(artifactDirectory, artifactName, artifactGroup, artifactVersion);
        }
    }

    public static class HygieiaBuildStub extends HygieiaBuild {
        public HygieiaBuildStub (boolean publishBuildStart ) {
            super(publishBuildStart);
        }
    }

    public static class HygieiaTestStub extends HygieiaTest {
        public HygieiaTestStub (boolean publishTestStart, boolean publishEvenBuildFails, String testFileNamePattern, String testResultsDirectory, String testType, String applicationName, String environmentName) {
            super(publishTestStart, publishEvenBuildFails, testFileNamePattern, testResultsDirectory, testType, applicationName, environmentName);
        }
    }
}
