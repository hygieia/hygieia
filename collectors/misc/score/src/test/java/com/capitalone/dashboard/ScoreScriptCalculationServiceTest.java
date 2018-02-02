package com.capitalone.dashboard;

import com.capitalone.dashboard.collector.ScoreParamSettings;
import com.capitalone.dashboard.model.ScoreWeight;
import com.capitalone.dashboard.widget.QualityWidgetScore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class ScoreScriptCalculationServiceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScoreScriptCalculationServiceTest.class);
  @InjectMocks
  private ScoreScriptCalculationService scoreScriptCalculationService;


 @Test
  public void calculateScore() throws NoSuchMethodException, ScriptException, IOException {
     /*ScoreWeight scoreWeight = new ScoreWeight(
      Constants.WIDGET_CODE_ANALYSIS,
      Constants.WIDGET_CODE_ANALYSIS_NAME
    );

    scoreWeight.setWeight(25);

    ScoreWeight scoreWeightChildCC = new ScoreWeight(
      QualityWidgetScore.WIDGET_QUALITY_CC_ID_NAME.getId(),
      QualityWidgetScore.WIDGET_QUALITY_CC_ID_NAME.getName()
    );

    ScoreWeight scoreWeightChildUT = new ScoreWeight(
      QualityWidgetScore.WIDGET_QUALITY_UT_ID_NAME.getId(),
      QualityWidgetScore.WIDGET_QUALITY_UT_ID_NAME.getName()
    );


    ScoreParamSettings scoreParamSettings = new ScoreParamSettings();

    scoreParamSettings.setFuncFilePath("quality-config.js");

    ScoreWeight scoreWeight1 =  scoreScriptCalculationService.runScriptToCalculateWeight(scoreWeight, null, scoreParamSettings);
    LOGGER.info("scoreWeight1 {}", scoreWeight1);*/

  }


}
