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

    public static final String BOWER_PATTERN = "(?<group>.+)/(?<module>[^/]+)/([^/]+)/(?<version>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String CHEF_PATTERN = "(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)" ;

    public static final String CONDA_PATTERN = "(?<group>.+)/(?<artifact>[^\\.-/]+)(-(?<version>[^/]+))?\\.(?<ext>(tar.bz2|json))";

    public static final String CRAN_PATTERN = "(?<group>.+)/([^/]+)/(?<artifact>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String CRAN_PATTERN1 = "(?<group>.+)/([^/]+)/(?<module>[^/]+)/(?<version>.+)/(?<artifact>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String CRAN_PATTERN2 = "(?<group>.+)/([^/]+)/(?<artifact>.+)/(?<filename>.+)\\.?(?<ext>tar.gz)";

    public static final String CRAN_PATTERN3 = "(?<module>[^/]+)/(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)";

    public static final String GEMS_INTERNAL_PATTERN ="(?<group>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String GENERIC_INTERNAL_PATTERN ="(?<group>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String GENERIC_INTERNAL1_PATTERN ="(?<group>.+)/(?<filename>.+)\\.(?<ext>(tar.gz|json|zip|pkg|tar.gz.sha))";

    public static final String GENERIC_PUBLIC_PATTERN ="(?<group>.+)/(?<module>[^/]+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String MAVEN_PATTERN = "(?<group>.+)/(?<module>[^/]+)/(?<artifact>.+)/(?<version>.+)/(?<filename>.+)\\.(?<ext>.+)?";

    public static final String HELM_PATTERN = "(?<group>.+)/(?<artifact>[^\\./]+)-(?<version>[^/]+)\\.(?<ext>.+)";


    // Daniel added regex
    public static final String MAVEN_PUB_PATTERN1 = "(?<group>[^/]+)/(?<version>[\\d].+)/(?<artifact>[^/|.]+)(-\\k<version>([^.]+))?(\\.(?<ext>.+))?";

    public static final String MAVEN_PUB_PATTERN2 = "(?<group>[^/]+)(/[^/|\\d]+){0,2}/(?<version>[\\d].+)/(?<artifact>[^/|.]+)-\\k<version>(-\\w+)?\\.(?<ext>.+)";

    public static final String MAVEN_PUB_PATTERN3 = "(?<group>[^/]+)/(?<artifact>[^/|.|\\d]+)-(?<version>[\\d|\\.]+)\\.(?<ext>(\\w+)(.\\w+)?)";

    public static final String MAVEN_PUB_PATTERN4 = "(?<group>[^/]+)(/[^/|\\d]+){0,2}/(?<artifact>[\\w]+(-[\\w]+)+)(/|$|/maven-metadata.xml)";

    public static final String NPM_PATTERN = "(?<group>[^@|/|.]+)/((@.+)\\/)?((?<artifact>[^\\/]+)/-/\\k<artifact>-)?(?<version>\\d[^/]+)\\.(?<ext>t.*gz)";

    public static final String NUGET_PATTERN = "(?<artifact>\\D+)\\.(?<version>\\d+\\.[\\d\\.]+)(-(?<classifier>\\w+))?\\.(?<ext>nupkg)";

    public static final String PIPY_PATTERN = "(?<group>[^/]+)/([^/]+/)*(?<artifact>\\w+)-(?<version>\\d[\\d\\.]+)(-(?<classifier>[^.]+))?\\.(?<ext>\\w{2,3}(.\\w{2,3})?)";

    public static final String PIPY_INT_PATTERN = "(?<group>.+)/(?<artifact>.+)\\.(?<ext>py)";

    public static final String PIPY_PUB_PATTERN = "(?<group>[^/]+)/(?<artifact>[^/]+)/(?<version>\\d[\\d\\.]+)/\\k<artifact>-\\k<version>(-(?<classifier>[^.]+))?\\.(?<ext>\\w{2,3}(.\\w{2,3})?)";

    public static final String REMOTEKEYS_PATTERN = "(?<group>.+)/(?<artifact>.+)\\.(?<ext>key)";

    public static final String RPM_INT_PATTERN1 = "(?<module>[^/]+)/(?<filename>.+)\\.(?<ext>rpm)";

    public static final String RPM_INT_PATTERN = "(?<group>.+)/\\d/(?<module>[^/]+)/(?<artifact>[^/]+)/\\w/(?<classifier>[^\\d]+)-(?<version>.+)\\.(?<ext>rpm)";

    public static final String RPM_PUB_PATTERN = "(?<group>.+)/(?<module>[^/]+)/(?<artifact>[^/]+)/\\k<artifact>(-(?<version>.+))\\.(?<ext>rpm)";

    public static final String SBT_INT_PATTERN1 = "(?<group>.+)/(?<module>.+)/(?<artifact>.+)/(?<version>.+)/([a-z]+)/(?<filename>.+)\\.(?<ext>.+)";

    public static final String SBT_INT_PATTERN2 = "(?<group>[^/]+)/([^/]+/)*(?<artifact>[^/]+)-(?<version>\\d[\\d\\.]+)\\.(?<ext>\\w{3}(.\\w{3})?)";

    public static final String SBT_PATTERN = "(?<group>[^/]+)/(?<module>[^/]+)/([^/]+/)?(?<artifact>[^/]+)/(?<version>\\d.+)/(?<filename>.+)-\\k<version>(-(?<classifier>\\w+))?\\.(?<ext>\\w{3}(.\\w{3})?)";

    public static final String SBT_PUB_PATTERN1 = "(?<group>[^/]+)/(?<module>[^/]+)/([^/]+/)?(?<artifact>[^/]+)(/(?<version>\\d.+))?/((?<filename>.+)\\.(?<ext>xml))?";

    public static final String SBT_PUB_PATTERN2 = "(?<group>[^/]+)/([^/]+/)+(?<version>\\d.+)/([^/]+/)+((?<filename>.+)\\.(?<ext>xml|jar.*))?";

    @Test
    public void testMvnPub1() {
        String patternStr = MAVEN_PUB_PATTERN1;
        String path = "maven-publicfacing/4.3.1/myArtifactName";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("4.3.1", ba.getArtifactVersion());
        assertEquals("myArtifactName", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals(null, ba.getArtifactExtension());

        path = "myGroupName/3.1.0/ui.tar.gz";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("myGroupName", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("3.1.0", ba.getArtifactVersion());
        assertEquals("ui", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());
    }

    @Test
    public void testMvnPub2() {
        String patternStr = MAVEN_PUB_PATTERN2;
        String path = "maven-publicfacing/5.1.4/connector-java-5.1.4-bin.jar";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("5.1.4", ba.getArtifactVersion());
        assertEquals("connector-java", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar", ba.getArtifactExtension());

        path = "maven-publicfacing/myDept/myDept/0.1.0/myDept-0.1.0.pom.asc";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.1.0", ba.getArtifactVersion());
        assertEquals("myDept", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("pom.asc", ba.getArtifactExtension());
    }

    @Test
    public void testMvnPub3() {
        String patternStr = MAVEN_PUB_PATTERN3;
        String path = "maven-publicfacing/myArtifactName-2.4.4.zip.asc";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("2.4.4", ba.getArtifactVersion());
        assertEquals("myArtifactName", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("zip.asc", ba.getArtifactExtension());
    }

    @Test
    public void testMvnPub4() {
        String patternStr = MAVEN_PUB_PATTERN4;
        String path = "maven-publicfacing/.com/jar/myUtil-installer/";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("myUtil-installer", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals(null, ba.getArtifactExtension());

        path = "maven-archive-remote-cache/.com/java/jdk-linux/maven-metadata.xml";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-archive-remote-cache", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("jdk-linux", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals(null, ba.getArtifactExtension());

        path = "maven-publicfacing/@dummy/my-dummy-helper-util";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("maven-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("my-dummy-helper-util", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals(null, ba.getArtifactExtension());
    }

    @Test
    public void testNpmInt() {
        String patternStr = NPM_PATTERN;
        String path = "npm-internalfacing/@myPlace/common/-/common-0.7.87.tgz";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("npm-internalfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.7.87", ba.getArtifactVersion());
        assertEquals("common", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

        path = "NPM-cache/myUtil/-/myUtil-0.1.6.tgz";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("NPM-cache", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.1.6", ba.getArtifactVersion());
        assertEquals("myUtil", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

        path = "NPM-cache/8.9.4/-/8.9.4-1.0.5.tgz";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("NPM-cache", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.5", ba.getArtifactVersion());
        assertEquals("8.9.4", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

    }

    @Test
    public void testNpmPub() {
        String patternStr = NPM_PATTERN;
        String path = "NPM-cache/@angular/animation/-/animation-4.0.0-beta.8.tgz";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("NPM-cache", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("4.0.0-beta.8", ba.getArtifactVersion());
        assertEquals("animation", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());

        path = "npm-publicfacing/my-ui-ctrl/-/my-ui-ctrl-2.2.0.tgz";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("npm-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("2.2.0", ba.getArtifactVersion());
        assertEquals("my-ui-ctrl", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tgz", ba.getArtifactExtension());
    }

    @Test
    public void testNugetInt() {
        String patternStr = NUGET_PATTERN;
        String path = "myArtifactName.1.0.11119.0.nupkg";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals(null, ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.11119.0", ba.getArtifactVersion());
        assertEquals("myArtifactName", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("nupkg", ba.getArtifactExtension());

        path = "myArtifactName.1.0.0-prerelease0021.nupkg";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals(null, ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.0", ba.getArtifactVersion());
        assertEquals("myArtifactName", ba.getArtifactName());
        assertEquals("prerelease0021", ba.getArtifactClassifier());
        assertEquals("nupkg", ba.getArtifactExtension());
    }

    @Test
    public void testNugetPub() {
        String patternStr = NUGET_PATTERN;
        String path = "myArtifactName.18.1.1.nupkg";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals(null, ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("18.1.1", ba.getArtifactVersion());
        assertEquals("myArtifactName", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("nupkg", ba.getArtifactExtension());

        path = "dummyArtifactName.5.14.5506.26202.nupkg";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals(null, ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("5.14.5506.26202", ba.getArtifactVersion());
        assertEquals("dummyArtifactName", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("nupkg", ba.getArtifactExtension());
    }

    @Test
    public void testPypiPub() {
        String patternStr = PIPY_PATTERN;
        String path = "pypi-publicfacing/0a/00/8cc925deac3a87046a4148d7846b571cf433515872b5430de4cd9dea83cb/requests-2.7.0.tar.gz";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("pypi-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("2.7.0", ba.getArtifactVersion());
        assertEquals("requests", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());

        path = "dummyGroup/2c/2b/33af741a5f53307691382d3bd5ba45fee3da21658f0bdf1f016d70ac3fb0/dummyArtifactName-1.11.0-my36-classifier-1_2_3.whl";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("dummyGroup", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.11.0", ba.getArtifactVersion());
        assertEquals("dummyArtifactName", ba.getArtifactName());
        assertEquals("my36-classifier-1_2_3", ba.getArtifactClassifier());
        assertEquals("whl", ba.getArtifactExtension());

        path = "dummyGroup/dummyApp/1.0.3/dummyApp-1.0.3.tar.gz";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("dummyGroup", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.3", ba.getArtifactVersion());
        assertEquals("dummyApp", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());

    }
    @Test
    public void testPypiPub1() {
        String patternStr = PIPY_PUB_PATTERN;
        String path = "dummyGrp/dummyApp/0.0.2/dummyApp-0.0.2.zip";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);

        assertNotNull(ba);
        assertEquals("dummyGrp", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.0.2", ba.getArtifactVersion());
        assertEquals("dummyApp", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("zip", ba.getArtifactExtension());
    }

    @Test
    public void testPypiInt() {
        String patternStr = PIPY_INT_PATTERN;
        String path = "dummyGrp/dummyArtifact.py";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("dummyGrp", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("dummyArtifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("py", ba.getArtifactExtension());
    }

    @Test
    public void testPypiInt1() {
        String patternStr = PIPY_PATTERN;
        String path = "pypi-internalfacing/00/0e/5a8c34adb97fc1cd6636d78050e575945e874c8516d501421d5a0f377a6c/dummyArtifact-1.15.4-my37-n-sys_w86.whl";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("pypi-internalfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.15.4", ba.getArtifactVersion());
        assertEquals("dummyArtifact", ba.getArtifactName());
        assertEquals("my37-n-sys_w86", ba.getArtifactClassifier());
        assertEquals("whl", ba.getArtifactExtension());
    }

    @Test
    public void testRemote() {
        String patternStr = REMOTEKEYS_PATTERN;
        String path = "remote-repository-keys/dummy-rpm.key";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("remote-repository-keys", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("dummy-rpm", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("key", ba.getArtifactExtension());

        path = "dummyGrp/my-d-artifact.key";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("dummyGrp", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("my-d-artifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("key", ba.getArtifactExtension());

        path = "dummyGrp/my.dc.org.key";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("dummyGrp", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("my.dc.org", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("key", ba.getArtifactExtension());
    }

    @Test
    public void testRpmInt() {
        String patternStr = RPM_INT_PATTERN;
        String path = "rpm-internalfacing/7/myx98_23/Packages/t/tar-1.29-4.fc26.myx98_23.rpm";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("rpm-internalfacing", ba.getArtifactGroupId());
        assertEquals("myx98_23", ba.getArtifactModule());
        assertEquals("1.29-4.fc26.myx98_23", ba.getArtifactVersion());
        assertEquals("Packages", ba.getArtifactName());
        assertEquals("tar", ba.getArtifactClassifier());
        assertEquals("rpm", ba.getArtifactExtension());
    }

    @Test
    public void testRpmPub() {
        String patternStr = RPM_PUB_PATTERN;
        String path = "rpm-publicfacing/myModule/myApp/myApp-1.12.1-1.el6.rpm";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("rpm-publicfacing", ba.getArtifactGroupId());
        assertEquals("myModule", ba.getArtifactModule());
        assertEquals("1.12.1-1.el6", ba.getArtifactVersion());
        assertEquals("myApp", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("rpm", ba.getArtifactExtension());
    }

    @Test
    public void testSbtInt1() {
        String patternStr = SBT_INT_PATTERN1;
        String path = "myGrp/ai.my88gt/my88gt-myModel/3.12.0.1/jars/my88gt-myModel.jar";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("myGrp", ba.getArtifactGroupId());
        assertEquals("ai.my88gt", ba.getArtifactModule());
        assertEquals("3.12.0.1", ba.getArtifactVersion());
        assertEquals("my88gt-myModel", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar", ba.getArtifactExtension());

        path = "myGrp/ai.my88gt/my88gt-myModel/3.12.0.1/ivys/ivy.xml";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("myGrp", ba.getArtifactGroupId());
        assertEquals("ai.my88gt", ba.getArtifactModule());
        assertEquals("3.12.0.1", ba.getArtifactVersion());
        assertEquals("my88gt-myModel", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("xml", ba.getArtifactExtension());

    }

    @Test
    public void testSbtInt2() {
        String patternStr = SBT_INT_PATTERN2;
        String path = "myGrp/myModel-package-01.00.02.01.tar";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("myGrp", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("01.00.02.01", ba.getArtifactVersion());
        assertEquals("myModel-package", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar", ba.getArtifactExtension());

        path = "sbt-internalfacing/myModel/myModel/1.0.0.12/myModel-1.0.0.12.jar";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-internalfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("1.0.0.12", ba.getArtifactVersion());
        assertEquals("myModel", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar", ba.getArtifactExtension());

    }

    @Test
    public void testSbtPub() {
        String patternStr = SBT_PATTERN;
        String path = "sbt-publicfacing/myApp/myApp/1.4.0/myApp-1.4.0-javadoc.jar.asc";

        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals("myApp", ba.getArtifactModule());
        assertEquals("1.4.0", ba.getArtifactVersion());
        assertEquals("myApp", ba.getArtifactName());
        assertEquals("javadoc", ba.getArtifactClassifier());
        assertEquals("jar.asc", ba.getArtifactExtension());


        path = "sbt-publicfacing/myModule/alex/myArtifact/1.0/myArtifact-1.0-sources.jar.asc";

        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals("myModule", ba.getArtifactModule());
        assertEquals("1.0", ba.getArtifactVersion());
        assertEquals("myArtifact", ba.getArtifactName());
        assertEquals("sources", ba.getArtifactClassifier());
        assertEquals("jar.asc", ba.getArtifactExtension());
    }

    @Test
    public void testSbtPub1() {
        String patternStr = SBT_PUB_PATTERN1;
        String path = "sbt-publicfacing/myModule/alex/myArtifact/1.0/";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals("myModule", ba.getArtifactModule());
        assertEquals("1.0", ba.getArtifactVersion());
        assertEquals("myArtifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals(null, ba.getArtifactExtension());

        path = "sbt-publicfacing/myModule/alex/myArtifact/maven-metadata.xml";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals("myModule", ba.getArtifactModule());
        assertEquals(null, ba.getArtifactVersion());
        assertEquals("myArtifact", ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("xml", ba.getArtifactExtension());

    }

    @Test
    public void testSbtPub2() {
        String patternStr = SBT_PUB_PATTERN2;
        String path = "sbt-publicfacing/com.github.sbt/dummy/scala_2.12/sbt_1.0/3.2.0/jars/dummy.jar.asc.md5.asc";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("3.2.0", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar.asc.md5.asc", ba.getArtifactExtension());

        path = "sbt-publicfacing/com.dummy/dummy-sbt-community-settings/scala_2.10/sbt_0.13/3.12.0/abcd/dummy-sbt-community-settings-sources.jar";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("3.12.0", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("jar", ba.getArtifactExtension());

        path = "sbt-publicfacing/com.dummy/my-app_2.12/scala_2.12/sbt_1.0/0.2.0/jars/";
        pattern = Pattern.compile(patternStr);
        ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("sbt-publicfacing", ba.getArtifactGroupId());
        assertEquals(null, ba.getArtifactModule());
        assertEquals("0.2.0", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals(null, ba.getArtifactExtension());

    }

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
        String path = "bin/artifactname/3.4/data.table_1.12.0.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals(null, ba.getArtifactGroupId());
        assertEquals("bin", ba.getArtifactModule());
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
        String path = "artifact-repo/module/tags/v1.2.7/abc-builds-v1.2.7.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("artifact-repo", ba.getArtifactGroupId());
        assertEquals("module", ba.getArtifactModule());
        assertEquals("v1.2.7", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
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
        String path = "groupname/abc-module/tags/v1.18.4/abc-v1.18.4.tar.gz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals("groupname", ba.getArtifactGroupId());
        assertEquals("abc-module", ba.getArtifactModule());
        assertEquals("v1.18.4", ba.getArtifactVersion());
        assertEquals(null, ba.getArtifactName());
        assertEquals(null, ba.getArtifactClassifier());
        assertEquals("tar.gz", ba.getArtifactExtension());
    }




    @Test
    public void testChef() {
        String patternStr = CHEF_PATTERN;
        String path = "007_abc_artifact_Instance/1.0.0/007_abc_artifact_Instance-1.0.0.tgz";
        Pattern pattern = Pattern.compile(patternStr);
        BinaryArtifact ba = ArtifactUtil.parse(pattern, path);
        assertNotNull(ba);
        assertEquals(null, ba.getArtifactGroupId());
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


