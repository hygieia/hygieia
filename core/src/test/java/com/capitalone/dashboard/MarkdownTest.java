package com.capitalone.dashboard;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MarkdownTest {

    private Pattern absoluteLinkPattern = Pattern.compile(".*github.com/capitalone/Hygieia.*", Pattern.DOTALL);
    private File root;

    @Before
    public void before(){
        root = getProjectRoot();
    }

    @Test
    public void licenseInRoot(){
        File license = new File(root, "LICENSE");
        assertTrue(license.exists());
    }

    @Test
    public void noAbsoluteLinksInMarkdown() throws Exception {
        Collection<File> recursiveFileList = FileUtils.listFiles(root, new String[]{"md"}, true);
        for (File file: recursiveFileList){
            String absolutePath = file.getAbsolutePath();
            if(absoluteLinkPattern.matcher(fromFile(absolutePath)).matches()){
                fail(String.format(Locale.US, "Use relative links in markdown documents: [%s]", absolutePath));
            }
        }
    }

    private File getProjectRoot() {
        return new File(".").getAbsoluteFile().getParentFile().getParentFile();
    }

    CharSequence fromFile(String filename) throws IOException {
        FileChannel channel = new FileInputStream(filename).getChannel();
        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int) channel.size());
        return Charset.forName("8859_1").newDecoder().decode(buffer);
    }
}
