package com.capitalone.dashboard.jenkins;

import org.bson.codecs.DecoderContext;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class Artefact {
    private String path;
    private String artefactName;

    private Artefact(Builder builder) {
        this.path=builder.path;
        this.artefactName=builder.artefactName;
    }

    public String getName(){return artefactName;}

    public String getPath(){return path;}

    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static class Builder {

        public String artefactName;
        public String path;

        public Builder path(String path) {
            this.path=path;
            return this;
        }

        public Builder artefactName(String artefactName) {
            this.artefactName=artefactName;
            return this;
        }

        public Artefact build() {
            return new Artefact(this);
        }
    }
}
