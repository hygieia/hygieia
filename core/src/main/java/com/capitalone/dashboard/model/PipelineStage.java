package com.capitalone.dashboard.model;

import java.util.Locale;

public final class PipelineStage {

	public static final PipelineStage COMMIT = new PipelineStage("Commit", PipelineStageType.COMMIT);
	public static final PipelineStage BUILD = new PipelineStage("Build", PipelineStageType.BUILD);

	private final String name;
	private final PipelineStageType type;
	
	private PipelineStage(String name, PipelineStageType type) { 
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public PipelineStageType getType() {
		return type;
	}
	
	public static PipelineStage valueOf(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		
		if (COMMIT.name.equalsIgnoreCase(name)) {
			return COMMIT;
		} else if (BUILD.name.equalsIgnoreCase(name)) {
			return BUILD;
		} else {
			return new PipelineStage(name, PipelineStageType.DEPLOY);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.toLowerCase(Locale.getDefault()).hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@SuppressWarnings("PMD.SimplifyBooleanReturns")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PipelineStage other = (PipelineStage) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "PipelineStage [name=" + name + ", type=" + type + "]";
	}
}
