package hygieia.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jenkins.plugins.hygieia.CustomObjectMapper;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
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

    public static Object convertJsonToObject (String json, Class thisClass) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        return mapper.readValue(json,thisClass);
    }

    public static List<File> getArtifactFiles(File rootDirectory, String pattern, List<File> results) {
        FileFilter filter = new WildcardFileFilter(pattern.replace("**", "*"), IOCase.SYSTEM);
        File[] temp = rootDirectory.listFiles(filter);
        if ((temp != null) && (temp.length > 0)) {
            results.addAll(Arrays.asList(temp));
        }

        temp = rootDirectory.listFiles();
        if ((temp != null) && (temp.length > 0))
            for (File currentItem : rootDirectory.listFiles()) {
                if (currentItem.isDirectory()) {
                    getArtifactFiles(currentItem, pattern, results);
                }
            }

        return results;
    }

    public static List<File> getArtifactFiles(String rootDirectoryString, String pattern, List<File> results) {
        return getArtifactFiles(new File(rootDirectoryString), pattern, results);
    }
}
