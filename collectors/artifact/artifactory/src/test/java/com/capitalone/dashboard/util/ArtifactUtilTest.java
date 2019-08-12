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

    public static final String APACHE2_PATTERN = "(?<group>.+)/(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String BOWER_PATTERN = "(?<group>.+)/(?<module>[^/]+)/(?<artifact>.+)/([^/]+)/(?<version>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String CHEF_PATTERN = "(?<group>.+)/(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)" ;

    public static final String CONDA_PATTERN = "(?<group>.+)/(?<artifact>[^\\.-/]+)(-(?<version>[^/]+))?\\.(?<ext>(tar.bz2|json))";

    public static final String CRAN_PATTERN = "(?<group>.+)/([^/]+)/(?<artifact>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String CRAN_PATTERN1 = "(?<group>.+)/([^/]+)/(?<module>[^/]+)/(?<version>.+)/(?<artifact>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String CRAN_PATTERN2 = "(?<group>.+)/([^/]+)/(?<artifact>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String CRAN_PATTERN3 = "(?<group>.+)/([^/]+)/(?<module>[^/]+)/(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)";

    public static final String GEMS_INTERNAL_PATTERN ="(?<group>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String GENERIC_INTERNAL_PATTERN ="(?<group>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String GENERIC_INTERNAL1_PATTERN ="(?<group>.+)/(?<filename>.+)\\.(?<ext>(tar.gz|json|zip|pkg|tar.gz.sha))";

    public static final String GENERIC_PUBLIC_PATTERN ="(?<group>.+)/(?<module>[^/]+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String MAVEN_PATTERN = "(?<group>.+)/(?<module>[^/]+)/(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String HELM_PATTERN = "(?<group>.+)/(?<artifact>[^\\./]+)-(?<version>[^/]+)\\.(?<ext>.+)";

    @Test
    public void testGeneric() {
        String patternStr = GENERIC_INTERNAL_PATTERN;
        String path = "artifact-repo/1.0.1/abc-filename1-file-filename-to-message-1.0.0.jar";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.1", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar", ba.getArtifactExtension());

    }

    @Test
    public void testMaven_Pattern() {
        String patternStr = MAVEN_PATTERN;
        String path = "artifact-repo/mymodule/artifact/0.1.0-SNAPSHOT/artifact-0.1.0-20171110.202829-1.jar";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("mymodule", ba.getArtifactModule());
        assertEquals("0.1.0-SNAPSHOT", ba.getArtifactVersion());
        assertEquals("artifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar", ba.getArtifactExtension());

    }

    @Test
    public void testGeneric_publicfacing_WithVersion() {
        String patternStr = GENERIC_PUBLIC_PATTERN;
        String path = "artifact-repo/module/5.13.5/module-parent-5.13.5-source-release.zip";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("module", ba.getArtifactModule());
        assertEquals("5.13.5", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("zip", ba.getArtifactExtension());

    }

    @Test
    public void testGeneric_WithoutVersion_WithDiffEXT() {
        String patternStr = GENERIC_INTERNAL1_PATTERN;
        String path = "artifact-repo/file-2.0.0-0.9.rc4.tar.gz.sha";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz.sha", ba.getArtifactExtension());

    }

    @Test
    public void testGeneric_Publicfacing() {
        String patternStr = GENERIC_INTERNAL_PATTERN;
        String path = "artifact-repo/1.0.2/filename-1.0.2-bin.zip";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.2", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("zip", ba.getArtifactExtension());

    }

    @Test
    public void testGeneric_WithoutVersion() {
        String patternStr = GENERIC_INTERNAL1_PATTERN;
        String path = "artifact-repo/filename.zip";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("zip", ba.getArtifactExtension());

    }

    @Test
    public void testGems() {
        String patternStr = GEMS_INTERNAL_PATTERN;
        String path = "artifact-repo/atREST-0.0.12.gem";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("gem", ba.getArtifactExtension());

    }


    @Test
    public void testCRAN_WithTarGZ() {
        String patternStr = CRAN_PATTERN1;
        String path = "artifact-repo/src/myModule/1.4.0/artifacttest/boot_1.2-6.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("myModule", ba.getArtifactModule());
        assertEquals("1.4.0", ba.getArtifactVersion());
        assertEquals("artifacttest", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());

    }

    @Test
    public void testCRAN_WithDiffEXT() {
        String patternStr = CRAN_PATTERN3;
        String path = "artifact-repo/bin/myModule/artifactname/3.4/data.table_1.12.0.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("myModule", ba.getArtifactModule());
        assertEquals("3.4", ba.getArtifactVersion());
        assertEquals("artifactname", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

    }

    @Test
    public void testCRAN_WithTarGZ_WithoutVersion() {
        String patternStr = CRAN_PATTERN2;
        String path = "artifact-repo/src/my-artifactName/R-test.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("my-artifactName", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());
    }


    @Test
    public void testBower() {
        String patternStr = BOWER_PATTERN;
        String path = "artifact-repo/module/abc-builds/tags/v1.2.7/abc-builds-v1.2.7.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("module", ba.getArtifactModule());
        assertEquals("v1.2.7", ba.getArtifactVersion());
        assertEquals("abc-builds", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());
    }


    @Test
    public void testConda() {
        String patternStr = CONDA_PATTERN;
        String path = "artifact-repo/testdeploy-1.0-py37_0.tar.bz2";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0-py37_0", ba.getArtifactVersion());
        assertEquals("testdeploy", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.bz2", ba.getArtifactExtension());


    }


    @Test
    public void testConda_DiffExt() {
        String patternStr = CONDA_PATTERN;
        String path = "artifact-repo/test.json";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("test", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("json", ba.getArtifactExtension());

    }

    @Test
    public void testHelm() {
        String patternStr = HELM_PATTERN;
        String path = "artifact-repo/acs-filename-artifact-2.1.0.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("2.1.0", ba.getArtifactVersion());
        assertEquals("acs-filename-artifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());
    }

    @Test
    public void testHelm_withDiffExt() {
        String patternStr = HELM_PATTERN;
        String path = "my-artifact-repo-cache/filename-0.1.5.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("my-artifact-repo-cache", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.1.5", ba.getArtifactVersion());
        assertEquals("filename", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

    }

    @Test
    public void testHelm_WithSlashes_withDiffExt() {
        String patternStr = HELM_PATTERN;
        String path = "my-abc-artifact-repo/abc-myfilename-0.1.7.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("my-abc-artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.1.7", ba.getArtifactVersion());
        assertEquals("abc-myfilename", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

    }

    @Test
    public void test_Bower_Publicfacing() {
        String patternStr = BOWER_PATTERN;
        String path = "artifact-repo/abc-module/abc-artifact/tags/v1.18.4/abc-v1.18.4.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("abc-module", ba.getArtifactModule());
        assertEquals("v1.18.4", ba.getArtifactVersion());
        assertEquals("abc-artifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());
    }




    @Test
    public void testChef() {
        String patternStr = CHEF_PATTERN;
        String path = "artifact-repo/007_abc_artifact_Instance/1.0.0/007_abc_artifact_Instance-1.0.0.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.0", ba.getArtifactVersion());
        assertEquals("007_abc_artifact_Instance", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());
    }

    @Test
    public void test_Chef_Internalfacing() {
        String patternStr = APACHE2_PATTERN;
        String path = "artifact-repo/apache2/3.1.0/apache2-3.1.0.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("3.1.0", ba.getArtifactVersion());
        assertEquals("apache2", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());

    }

    @Test
    public void testCran_WithSlashes_withExt() {
        String patternStr = CRAN_PATTERN;
        String path = "my-artifact-repo/bin/macos/ReadMe.txt";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("my-artifact-repo", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("macos", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("txt", ba.getArtifactExtension());

    }





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

