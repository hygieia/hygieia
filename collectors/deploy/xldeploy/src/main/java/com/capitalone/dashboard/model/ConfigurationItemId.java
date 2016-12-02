package com.capitalone.dashboard.model;

public class ConfigurationItemId {
	private String ref;
	private String type;
	
	public ConfigurationItemId(String ref, String type) {
		super();
		this.ref = ref;
		this.type = type;
	}
	/**
	 * @return the id
	 */
	public String getRef() {
		return ref;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.ref = id;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
}
