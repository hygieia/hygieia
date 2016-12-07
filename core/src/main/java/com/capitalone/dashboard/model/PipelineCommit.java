package com.capitalone.dashboard.model;

public class PipelineCommit extends SCM{
	
    public PipelineCommit() {
    }

    public PipelineCommit(long timestamp) {
        this.timestamp = timestamp;
    }

    public PipelineCommit(SCM scm, long timestamp){
        super(scm);
        this.timestamp = timestamp;
    }
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scmRevisionNumber == null) ? 0 : scmRevisionNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PipelineCommit other = (PipelineCommit) obj;
		if (scmRevisionNumber == null) {
			if (other.scmRevisionNumber != null)
				return false;
		} else if (!scmRevisionNumber.equals(other.scmRevisionNumber))
			return false;
		return true;
	}
    
    
}
