package com.capitalone.dashboard.model;


import java.net.MalformedURLException;
import java.net.URL;

public class RepoBranch {
    private String url = "";
    private String branch = "";
    private RepoType type = RepoType.Unknown;

    public enum RepoType {
        SVN,
        GIT,
        Unknown;

        public static com.capitalone.dashboard.model.RepoBranch.RepoType fromString(String value) {
            if (value ==  null) return RepoType.Unknown;
            for (com.capitalone.dashboard.model.RepoBranch.RepoType repoType : values()) {
                if (repoType.toString().equalsIgnoreCase(value)) {
                    return repoType;
                }
            }
            throw new IllegalArgumentException(value + " is not a valid RepoType.");
        }
    }

    public RepoBranch(String url, String branch, RepoType repoType) {
        this.url = url;
        this.branch = branch;
        this.type = repoType;
    }

    public RepoBranch() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public RepoType getType() {
        return type;
    }

    public void setType(RepoType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepoBranch that = (RepoBranch) o;

        return getRepoName().equals(that.getRepoName()) && branch.equals(that.branch);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + branch.hashCode();
        return result;
    }

    protected String getRepoName() {
        try {
            URL temp = new URL(url);
            return temp.getHost() + temp.getPath();
        } catch (MalformedURLException e) {
            return url;
        }

    }
}
