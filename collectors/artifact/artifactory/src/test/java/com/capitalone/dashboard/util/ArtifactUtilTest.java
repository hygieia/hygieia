package com.capitalone.dashboard.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.BinaryArtifact;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactUtilTest {
	public static final String IVY_PATTERN1 = "(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<artifact>ivy)-\\k<version>(-(?<classifier>[^\\.]+))?\\.(?<ext>xml)";
	public static final String IVY_ARTIFACT_PATTERN1 = "(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<type>[^/]+)/(?<artifact>[^\\.-/]+)-\\k<version>(-(?<classifier>[^\\.]+))?(\\.(?<ext>.+))?";
	
	public static final String MAVEN_PATTERN1 = "(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<artifact>\\k<module>)-\\k<version>(-(?<classifier>[^\\.]+))?(\\.(?<ext>.+))?";
	
	public static final String MISC_PATTERN1 = "(?<group>.+)/([^/]+)/(?<artifact>[^\\.-/]+)-(?<version>[^/]+)\\.(?<ext>zip)";
	public static final String MISC_PATTERN2 = "(?<group>.+)/(?<buildnumber>\\d+)/([^/]+/)*(?<artifact>[^\\./]+)-(?<version>[^/]+)\\.(?<ext>zip)";

	public static final String ARTIFACT_PATTERN = "(?<group>.+)[\\/](?<artifact>.+)\\/(?<version>.+)\\/(?<filename>.+)\\.(?<ext>.+)";


	@Test
	public void testIvy() {
		String patternStr = IVY_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/ivy-1.0.0.xml";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("ivy", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("xml", ba.getArtifactExtension());
	}
	
	@Test
	public void testIvy_WithClassifier() {
		String patternStr = IVY_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/ivy-1.0.0-myclassifier.xml";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("ivy", ba.getArtifactName());
		assertEquals("myclassifier", ba.getArtifactClassifier());
		assertEquals("xml", ba.getArtifactExtension());
	}
	
	@Test
	public void testIvy_WithSlashes() {
		String patternStr = IVY_PATTERN1;
		String path = "com/my/group/mymodule/1.0.0/ivy-1.0.0.xml";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("ivy", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("xml", ba.getArtifactExtension());
	}
	
	@Test
	public void testIvyArtifact() {
		String patternStr = IVY_ARTIFACT_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/jar/foobar-1.0.0.jar";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("foobar", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("jar", ba.getArtifactExtension());
	}
	
	@Test
	public void testIvyArtifact_WithClassifier() {
		String patternStr = IVY_ARTIFACT_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/jar/foobar-1.0.0-myclassifier.jar";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("foobar", ba.getArtifactName());
		assertEquals("myclassifier", ba.getArtifactClassifier());
		assertEquals("jar", ba.getArtifactExtension());
	}
	
	@Test
	public void testIvyArtifact_NoExt() {
		String patternStr = IVY_ARTIFACT_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/jar/foobar-1.0.0";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("foobar", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals(null, ba.getArtifactExtension());
	}
	
	@Test
	public void testIvyArtifact_WithSlashes() {
		String patternStr = IVY_ARTIFACT_PATTERN1;
		String path = "com/my/group/mymodule/1.0.0/jar/foobar-1.0.0";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("foobar", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals(null, ba.getArtifactExtension());
	}
	
	@Test
	public void testMaven() {
		String patternStr = MAVEN_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/mymodule-1.0.0.txt";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("mymodule", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("txt", ba.getArtifactExtension());
	}
	
	@Test
	public void testMaven_WithClassifier() {
		String patternStr = MAVEN_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/mymodule-1.0.0-myclassifier.txt";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("mymodule", ba.getArtifactName());
		assertEquals("myclassifier", ba.getArtifactClassifier());
		assertEquals("txt", ba.getArtifactExtension());
	}
	
	@Test
	public void testMaven_NoExt() {
		String patternStr = MAVEN_PATTERN1;
		String path = "com.my.group/mymodule/1.0.0/mymodule-1.0.0";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("mymodule", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals(null, ba.getArtifactExtension());
	}
	
	@Test
	public void testMaven_WithSlashes() {
		String patternStr = MAVEN_PATTERN1;
		String path = "com/my/group/mymodule/1.0.0/mymodule-1.0.0";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("com.my.group", ba.getArtifactGroupId());
		assertEquals("mymodule", ba.getArtifactModule());
		assertEquals("1.0.0", ba.getArtifactVersion());
		assertEquals("mymodule", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals(null, ba.getArtifactExtension());
	}
	
	@Test
	public void testMisc1() {
		String patternStr = MISC_PATTERN1;
		String path = "myjob/20/My-Project-1.0.master.20.zip";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("myjob", ba.getArtifactGroupId());
		assertEquals(null, ba.getArtifactModule());
		assertEquals("1.0.master.20", ba.getArtifactVersion());
		assertEquals("My-Project", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("zip", ba.getArtifactExtension());
	}
	
	@Test
	public void testMisc2_1() {
		String patternStr = MISC_PATTERN2;
		String path = "myjob/20/folder1/folder2/My-Project-1.0.master.20.zip";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("myjob", ba.getArtifactGroupId());
		assertEquals(null, ba.getArtifactModule());
		assertEquals("1.0.master.20", ba.getArtifactVersion());
		assertEquals("My-Project", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("zip", ba.getArtifactExtension());
	}
	
	@Test
	public void testMisc2_2() {
		String patternStr = MISC_PATTERN2;
		String path = "JOB_Helloworld/125/Helloworld-5.1.0.master.125.zip";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("JOB_Helloworld", ba.getArtifactGroupId());
		assertEquals(null, ba.getArtifactModule());
		assertEquals("5.1.0.master.125", ba.getArtifactVersion());
		assertEquals("Helloworld", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("zip", ba.getArtifactExtension());
	}
	
	@Test
	public void testMisc2_3() {
		String patternStr = MISC_PATTERN2;
		String path = "JOB_Helloworld/125/app-app-app-5.1.0.master.125.zip";
		
		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("JOB_Helloworld", ba.getArtifactGroupId());
		assertEquals(null, ba.getArtifactModule());
		assertEquals("5.1.0.master.125", ba.getArtifactVersion());
		assertEquals("app-app-app", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("zip", ba.getArtifactExtension());
	}

	@Test
	public void testArtifactPattern() {
		String patternStr = ARTIFACT_PATTERN;
		String path = "dummy/test-dev/1/manifest.json";

		Pattern pattern = Pattern.compile(patternStr);
		BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
		assertNotNull(ba);
		assertEquals("dummy", ba.getArtifactGroupId());
		assertEquals(null, ba.getArtifactModule());
		assertEquals("1", ba.getArtifactVersion());
		assertEquals("test-dev", ba.getArtifactName());
		assertEquals(null, ba.getArtifactClassifier());
		assertEquals("json", ba.getArtifactExtension());
	}

}
