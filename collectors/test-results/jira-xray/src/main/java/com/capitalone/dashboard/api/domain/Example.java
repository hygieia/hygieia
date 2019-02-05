package com.capitalone.dashboard.api.domain;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;

import java.net.URI;
import java.util.List;

/**
 * This class will get the details from example section
 */
public class Example extends BasicIssue {
    private Long id;
    private Integer rank;
    private List<Object> values;
    private Status status;

    public Example(URI self, String key, Long id){
        super(self, key, id);
    }

    public Example(URI self, String key, Long id, Integer rank, List<Object> values, Status status){
        super(self,key,id);
        this.rank=rank;
        this.values=values;
        this.status=status;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;

    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status{TODO,FAIL,PASS}

}