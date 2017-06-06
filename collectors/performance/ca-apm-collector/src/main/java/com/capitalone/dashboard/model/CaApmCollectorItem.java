package com.capitalone.dashboard.model;

public class CaApmCollectorItem extends CollectorItem {

	private static final String MODULE_NAME = "manModuleName";
	private static final String DESCRIPTION = "description";
	private static final String DOMAIN_NAME = "domainName";
	private static final String JAR_FILE_NAME = "jarFileName";

	public String getModuleName() {
		return (String) getOptions().get(MODULE_NAME);
	}

	public void setModuleName(String manModuleName) {
		getOptions().put(MODULE_NAME, manModuleName);
	}

	public String getDescription() {
		return (String) getOptions().get(DESCRIPTION);
	}

	public void setDescription(String description) {
		getOptions().put(DESCRIPTION, description);
	}

	public String getDomainName() {
		return (String) getOptions().get(DOMAIN_NAME);
	}

	public void setDomainName(String domainName) {
		getOptions().put(DOMAIN_NAME, domainName);
	}

	public String getJarFileName() {
		return (String) getOptions().get(JAR_FILE_NAME);
	}

	public void setJarFileName(String jarFileName) {
		getOptions().put(JAR_FILE_NAME, jarFileName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CaApmCollectorItem moduleInfo = (CaApmCollectorItem) o;

		return getModuleName().equals(moduleInfo.getModuleName()) && getDomainName().equals(moduleInfo.getDomainName());
	}

	@Override
	public int hashCode() {
		int result = getModuleName().hashCode();
		result = 31 * result + getDomainName().hashCode();
		return result;
	}

}
