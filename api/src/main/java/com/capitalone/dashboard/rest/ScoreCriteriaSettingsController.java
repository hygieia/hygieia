package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveScoreValueTypeEditor;
import com.capitalone.dashboard.model.score.ScoreValueType;
import com.capitalone.dashboard.model.score.settings.ScoreCriteriaSettings;
import com.capitalone.dashboard.service.ScoreCriteriaSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ScoreCriteriaSettingsController {

  private final ScoreCriteriaSettingsService scoreCriteriaSettingsService;

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(ScoreValueType.class, new CaseInsensitiveScoreValueTypeEditor());
  }

  @Autowired
  public ScoreCriteriaSettingsController(
    ScoreCriteriaSettingsService scoreCriteriaSettingsService
  ) {
    this.scoreCriteriaSettingsService = scoreCriteriaSettingsService;
  }

  @RequestMapping(value = "/score/settings/type/{scoreValueType}",
    method = GET, produces = APPLICATION_JSON_VALUE)
  public ScoreCriteriaSettings getScoreCriteriaSettingsByType(@PathVariable ScoreValueType scoreValueType) {
     return this.scoreCriteriaSettingsService.getScoreCriteriaSettingsByType(scoreValueType);
  }
}
