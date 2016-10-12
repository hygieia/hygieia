package com.capitalone.dashboard.jenkins;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class Artifact {
  private String path;
  private String artifactName;

  private Artifact(Builder builder) {
    this.path = builder.path;
    this.artifactName = builder.artifactName;
  }

  public String getName() {
    return artifactName;
  }

  public String getPath() {
    return path;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    public String artifactName;
    public String path;

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder artifactName(String artifactName) {
      this.artifactName = artifactName;
      return this;
    }

    public Artifact build() {
      return new Artifact(this);
    }
  }
}
