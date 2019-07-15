package hygieia.utils;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.BuildStage;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HygieiaUtilsTest {

    @Test
    public void getBuildCollectionId() {
        String input = "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa";
        String output = HygieiaUtils.getBuildCollectionId(input);
        assertTrue(output.equals("5ba16a0b0be2d34a64291205"));
    }

    @Test
    public void getCollectorItemId() {
        String input = "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa";
        String output = HygieiaUtils.getCollectorItemId(input);
        assertTrue(output.equals("56c39f487fab7c63c8f947aa"));
    }

    @Test
    public void getBuildCollectionIdJustOne() {
        String input = "5ba16a0b0be2d34a64291205a";
        String output = HygieiaUtils.getBuildCollectionId(input);
        assertTrue(output.equals("5ba16a0b0be2d34a64291205a"));
    }

    @Test
    public void getCollectorItemIdJustOne() {
        String input = "5ba16a0b0be2d34a64291205";
        String output = HygieiaUtils.getCollectorItemId(input);
        assertTrue(output.equals(""));
    }

    @Test
    public void getBuildCollectionIdJustOneComma() {
        String input = ",5ba16a0b0be2d34a64291205a";
        String output = HygieiaUtils.getBuildCollectionId(input);
        assertTrue(output.equals(""));
    }

    @Test
    public void getCollectorItemIdJustOneComma() {
        String input = ",5ba16a0b0be2d34a64291205";
        String output = HygieiaUtils.getCollectorItemId(input);
        assertTrue(output.equals("5ba16a0b0be2d34a64291205"));
    }

    @Test
    public void getBuildStages_describe_one_build() throws HygieiaException {
        String json = loadJson("json/wfapi-describe-one-build.json");
        LinkedList<BuildStage> buildStages = HygieiaUtils.getBuildStages(json);
        Assertions.assertThat(buildStages.get(0)).isEqualToComparingFieldByField(generateExpected("json/build-stage.json"));
        assertThat(buildStages.size()).isEqualTo(4);
    }

    @Test
    public void getBuildStages_describe_zero_build() throws HygieiaException {
        String json = loadJson("json/wfapi-describe-zero-build.json");
        LinkedList<BuildStage> buildStages = HygieiaUtils.getBuildStages(json);
        assertThat(buildStages.size()).isEqualTo(0);
    }
    @Test(expected = HygieiaException.class)
    public void getBuildStages_Parse_Exception() throws HygieiaException {
        String json = loadJson("json/wfapi-describe-one-build-exception.json");
        LinkedList<BuildStage> buildStages = HygieiaUtils.getBuildStages(json);
        Assertions.assertThat(buildStages.get(0)).isEqualToComparingFieldByField(generateExpected("json/build-stage.json"));
        assertThat(buildStages.size()).isEqualTo(4);
    }

    @Test
    public void setLogUrl(){
        String json = loadJson("json/wfapi-describe-one-build-log-url.json");
        BuildStage bs = HygieiaUtils.setLogUrl(json,new BuildStage());
        assertEquals("/job/testGenericItem/61/execution/node/7/wfapi/log",bs.getExec_node_logUrl());
    }

    @Test
    public void setLogUrl_zero_stage_flow_nodes(){
        String json = loadJson("json/wfapi-describe-one-build-zero-stage-flow-nodes.json");
        BuildStage bs = HygieiaUtils.setLogUrl(json,new BuildStage());
        assertNull(bs.getExec_node_logUrl());
    }


    private String loadJson(String fileName){
        try {
            return IOUtils.toString(Resources.getResource(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BuildStage generateExpected(String fileName){
         return new Gson().fromJson(loadJson(fileName), new TypeToken<BuildStage>(){}.getType());
    }
}