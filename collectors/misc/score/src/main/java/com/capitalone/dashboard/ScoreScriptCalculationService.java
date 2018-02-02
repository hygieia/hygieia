package com.capitalone.dashboard;

/*import com.capitalone.dashboard.collector.ScoreParamSettings;
import com.capitalone.dashboard.collector.ScoreValueType;
import com.capitalone.dashboard.collector.ScoreTypeValue;
import com.capitalone.dashboard.model.PropagateType;
import com.capitalone.dashboard.model.ScoreWeight;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectBooleanHashMap;*/
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;*/

@Service
public class ScoreScriptCalculationService {

  //private static final Logger LOGGER = LoggerFactory.getLogger(ScoreScriptCalculationService.class);

/*
  public ScoreWeight runScriptToCalculateWeight(
    ScoreWeight scoreWeight,
    Map<String, Object> data,
    ScoreParamSettings scoreParamSettings) throws FileNotFoundException, ScriptException, NoSuchMethodException, JsonProcessingException, IOException{

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval(new String(Resources.asByteSource(Resources.getResource((scoreParamSettings.getFuncFilePath()))).read()));
    Invocable invocable = (Invocable) engine;
    ObjectMapper mapper = new ObjectMapper();
    JSObject result = (JSObject)invocable.invokeFunction(scoreParamSettings.getFuncName(), mapper.writeValueAsString(scoreWeight), data, scoreParamSettings);


    return getScoreWeightFromJSObject(result, scoreWeight);
  }


  private ScoreWeight getScoreWeightFromJSObject(JSObject result, ScoreWeight scoreWeight) {
    if (null == result) {
      return null;
    }

    JSObject score = (JSObject) result.getMember("score");
    if (null == score ) {
      return null;
    }

    String scoreType = (String) score.getMember("scoreType");
    if (StringUtils.isEmpty(scoreType)) {
      return null;
    }

    ScoreTypeValue scoreTypeValue = new ScoreTypeValue();
    scoreTypeValue.setScoreType(ScoreValueType.valueOf(scoreType));

    Object scoreValue = score.getMember("scoreValue");
    if (null != scoreValue) {
      scoreTypeValue.setScoreValue(Double.valueOf(String.valueOf(scoreValue)));
    }

    String propagate = (String) score.getMember("propagate");
    if (StringUtils.isNotEmpty(propagate)) {
      scoreTypeValue.setPropagate(PropagateType.valueOf(propagate));
    }
    scoreWeight.setScore(scoreTypeValue);

    String state = (String) result.getMember("state");
    if (StringUtils.isNotEmpty(state)) {
      scoreWeight.setState(ScoreWeight.ProcessingState.valueOf(state));
    }

    String failureMssg = (String) result.getMember("failureMssg");
    if (StringUtils.isNotEmpty(failureMssg)) {
      scoreWeight.setFailureMssg(failureMssg);
    }


    JSObject children = (JSObject) result.getMember("children");
    if (null != children && children.isArray()) {
       for (Object childObj : children.values()) {
         JSObject child = (JSObject) childObj;
       }


    }

    return scoreWeight;
  }
*/


}
