package com.capitalone.dashboard.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.capitalone.dashboard.model.BinaryArtifact;

public class ArtifactUtil {
	private static final String ORG_REGEX_GROUP = "group";
	private static final String MODULE_REGEX_GROUP = "module";
	private static final String ARTIFACT_REGEX_GROUP = "artifact";
	private static final String VERSION_REGEX_GROUP = "version";
	private static final String CLASSIFIER_REGEX_GROUP = "classifier";
	private static final String EXT_REGEX_GROUP = "ext";

	public static final BinaryArtifact parse(Pattern pattern, String path) {
		Matcher matcher = pattern.matcher(path);
		
		if (matcher.matches()) {
			String org = null;
			String module = null;
			String version = null;
			String artifact = null;
			String classifier = null;
			String ext = null;
			
			if (pattern.pattern().contains("<" + ORG_REGEX_GROUP + ">")) {
				org = matcher.group(ORG_REGEX_GROUP);
				
				org = org.replace('/', '.');
			}
			
			if (pattern.pattern().contains("<" + MODULE_REGEX_GROUP + ">")) {
				module = matcher.group(MODULE_REGEX_GROUP);
			}
			
			if (pattern.pattern().contains("<" + VERSION_REGEX_GROUP + ">")) {
				version = matcher.group(VERSION_REGEX_GROUP);
			}
			
			if (pattern.pattern().contains("<" + ARTIFACT_REGEX_GROUP + ">")) {
				artifact = matcher.group(ARTIFACT_REGEX_GROUP);
			}
			
			if (pattern.pattern().contains("<" + CLASSIFIER_REGEX_GROUP + ">")) {
				classifier = matcher.group(CLASSIFIER_REGEX_GROUP);
			}
			
			if (pattern.pattern().contains("<" + EXT_REGEX_GROUP + ">")) {
				ext = matcher.group(EXT_REGEX_GROUP);
			}
			
			BinaryArtifact ba = new BinaryArtifact();
			ba.setArtifactGroupId(org);
			ba.setArtifactModule(module);
			ba.setArtifactVersion(version);
			ba.setArtifactName(artifact);
			ba.setArtifactClassifier(classifier);
			ba.setArtifactExtension(ext);
			
			return ba;
		} else {
			return null;
		}
	}
}
