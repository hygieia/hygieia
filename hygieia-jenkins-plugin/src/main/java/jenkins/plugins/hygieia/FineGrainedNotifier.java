package jenkins.plugins.hygieia;

import hudson.model.AbstractBuild;

public interface FineGrainedNotifier {

//    @SuppressWarnings("rawtypes")
    void started(AbstractBuild r);

    @SuppressWarnings("rawtypes")
    void deleted(AbstractBuild r);

//    @SuppressWarnings("rawtypes")
    void finalized(AbstractBuild r);

    @SuppressWarnings("rawtypes")
    void completed(AbstractBuild r);


}
