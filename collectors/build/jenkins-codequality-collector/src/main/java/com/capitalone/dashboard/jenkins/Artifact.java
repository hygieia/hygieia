package com.capitalone.dashboard.jenkins;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class Artifact {
  private String relativePath;
  private String fileName;

  private Artifact() {
    // required for converter
  }

  private Artifact(Builder builder) {
    this.relativePath = builder.relativePath;
    this.fileName = builder.fileName;
  }

  public String getName() {
    return fileName;
  }

  public String getRelativePath() {
    return relativePath;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    public String fileName;
    public String relativePath;

    public Builder path(String relativePath) {
      this.relativePath = relativePath;
      return this;
    }

    public Builder fileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Artifact build() {
      return new Artifact(this);
    }
  }
}
