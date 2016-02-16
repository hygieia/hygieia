package jenkins.plugins.hygieia;

import hudson.model.AbstractBuild;

@SuppressWarnings("rawtypes")
public class DisabledNotifier implements FineGrainedNotifier {
    public void started(AbstractBuild r) {
    }

    public void deleted(AbstractBuild r) {
    }

    public void finalized(AbstractBuild r) {
    }

    public void completed(AbstractBuild r) {
    }

    public void aborted(AbstractBuild r) {

    }
}
