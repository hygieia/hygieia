package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModelProperty;

public class CodeReviewAuditRequest extends AuditReviewRequest {
    @ApiModelProperty(value = "Repo Description", example="https://github.com/somerepo")
	@NotNull
    private String repo;
    @ApiModelProperty(value = "Branch Description", example="master")
	@NotNull
    private String branch;
    @ApiModelProperty(value = "SCM Name", example="GitHub")
    private String scmName;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getScmName() {
        return scmName;
    }

    public void setScmName(String scmName) {
        this.scmName = scmName;
    }
}
