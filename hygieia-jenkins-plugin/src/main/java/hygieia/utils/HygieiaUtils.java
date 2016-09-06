package hygieia.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.FilePath;
import jenkins.plugins.hygieia.CustomObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.util.CollectionUtils;

import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


public class HygieiaUtils {
    private static final Logger logger = Logger.getLogger(HygieiaUtils.class.getName());
    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static Object convertJsonToObject(String json, Class thisClass) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        return mapper.readValue(json, thisClass);
    }

//    public static List<File> getArtifactFiles(File rootDirectory, String pattern, List<File> results) {
//        FileFilter filter = new WildcardFileFilter(pattern.replace("**", "*"), IOCase.SYSTEM);
//        File[] temp = rootDirectory.listFiles(filter);
//        if ((temp != null) && (temp.length > 0)) {
//            results.addAll(Arrays.asList(temp));
//        }
//
//        temp = rootDirectory.listFiles();
//        if ((temp != null) && (temp.length > 0))
//            for (File currentItem : rootDirectory.listFiles()) {
//                if (currentItem.isDirectory()) {
//                    getArtifactFiles(currentItem, pattern, results);
//                }
//            }
//
//        return results;
//    }

    public static List<FilePath> getArtifactFiles(FilePath rootDirectory, String pattern, List<FilePath> results) throws IOException, InterruptedException {
        FileFilter filter = new WildcardFileFilter(pattern.replace("**", "*"), IOCase.SYSTEM);
        List<FilePath> temp = rootDirectory.list(filter);
        if (!CollectionUtils.isEmpty(temp)) {
            results.addAll(temp);
        }

        temp = rootDirectory.list();
        if (!CollectionUtils.isEmpty(temp)) {
            for (FilePath currentItem : rootDirectory.list()) {
                if (currentItem.isDirectory()) {
                    getArtifactFiles(currentItem, pattern, results);
                }
            }
        }
        return results;
    }

    public static String getFileNameMinusVersion(FilePath file, String version) {
        String ext = FilenameUtils.getExtension(file.getName());
        if ("".equals(version)) return file.getName();

        int vIndex = file.getName().indexOf(version);
        if (vIndex <= 0) return file.getName();
        if ((file.getName().charAt(vIndex - 1) == '-') || (file.getName().charAt(vIndex - 1) == '_')) {
            vIndex = vIndex - 1;
        }
        return file.getName().substring(0, vIndex) + "." + ext;
    }

    public static String guessVersionNumber(String source) {
        String versionNumber = "";
        String fileName = source.substring(0, source.lastIndexOf("."));
        if (fileName.contains(".")) {
            String majorVersion = fileName.substring(0, fileName.indexOf("."));
            String minorVersion = fileName.substring(fileName.indexOf("."));
            int delimiter = majorVersion.lastIndexOf("-");
            if (majorVersion.indexOf("_") > delimiter) delimiter = majorVersion.indexOf("_");
            majorVersion = majorVersion.substring(delimiter + 1, fileName.indexOf("."));
            versionNumber = majorVersion + minorVersion;
        }
        return versionNumber;
    }
//    public static List<File> getArtifactFiles(String rootDirectoryString, String pattern, List<File> results) {
//        return getArtifactFiles(new File(rootDirectoryString), pattern, results);
//    }
}
