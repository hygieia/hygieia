package com.capitalone.dashboard.model.score.settings;

/**
 * Bean to hold score settings specific to quality
 */
public class QualityScoreSettings extends ScoreComponentSettings {

  //Settings for Code Coverage
  private ScoreComponentSettings codeCoverage;

  //Settings for Unit Tests
  private ScoreComponentSettings unitTests;

  //Settings for Violations
  private ViolationsScoreSettings violations;

  public static QualityScoreSettings cloneQualityScoreSettings(QualityScoreSettings qualityScoreSettings) {
    if (null == qualityScoreSettings) {
      return null;
    }

    QualityScoreSettings qualityScoreSettingsClone = new QualityScoreSettings();
    ScoreComponentSettings.copyScoreComponentSettings(qualityScoreSettings, qualityScoreSettingsClone);
    qualityScoreSettingsClone.setCodeCoverage(
      ScoreComponentSettings.cloneScoreComponentSettings(qualityScoreSettings.getCodeCoverage())
    );
    qualityScoreSettingsClone.setUnitTests(
      ScoreComponentSettings.cloneScoreComponentSettings(qualityScoreSettings.getUnitTests())
    );
    qualityScoreSettingsClone.setViolations(
      ViolationsScoreSettings.cloneViolationsScoreSettings(qualityScoreSettings.getViolations())
    );

    return qualityScoreSettingsClone;
  }


  public ScoreComponentSettings getCodeCoverage() {
    return codeCoverage;
  }

  public void setCodeCoverage(ScoreComponentSettings codeCoverage) {
    this.codeCoverage = codeCoverage;
  }

  public ScoreComponentSettings getUnitTests() {
    return unitTests;
  }

  public void setUnitTests(ScoreComponentSettings unitTests) {
    this.unitTests = unitTests;
  }

  public ViolationsScoreSettings getViolations() {
    return violations;
  }

  public void setViolations(ViolationsScoreSettings violations) {
    this.violations = violations;
  }

  public static class ViolationsScoreSettings extends ScoreComponentSettings {

    public static final int BLOCKER_VIOLATIONS_WEIGHT = 20;
    public static final int CRITICAL_VIOLATIONS_WEIGHT = 5;
    public static final int MAJOR_VIOLATIONS_WEIGHT = 1;

    private int blockerViolationsWeight = BLOCKER_VIOLATIONS_WEIGHT;
    private int criticalViolationsWeight = CRITICAL_VIOLATIONS_WEIGHT;
    private int majorViolationWeight = MAJOR_VIOLATIONS_WEIGHT;
    private boolean allowNegative = false;

    public static ViolationsScoreSettings cloneViolationsScoreSettings(ViolationsScoreSettings violationsScoreSettings) {
      if (null == violationsScoreSettings) {
        return null;
      }

      ViolationsScoreSettings violationsScoreSettingsClone = new ViolationsScoreSettings();
      ScoreComponentSettings.copyScoreComponentSettings(violationsScoreSettings, violationsScoreSettingsClone);
      violationsScoreSettingsClone.setAllowNegative(
        violationsScoreSettings.isAllowNegative()
      );
      violationsScoreSettingsClone.setBlockerViolationsWeight(
        violationsScoreSettings.getBlockerViolationsWeight()
      );
      violationsScoreSettingsClone.setCriticalViolationsWeight(
        violationsScoreSettings.getCriticalViolationsWeight()
      );
      violationsScoreSettingsClone.setMajorViolationWeight(
        violationsScoreSettings.getMajorViolationWeight()
      );

      return violationsScoreSettingsClone;
    }

    public int getBlockerViolationsWeight() {
      return blockerViolationsWeight;
    }

    public void setBlockerViolationsWeight(int blockerViolationsWeight) {
      this.blockerViolationsWeight = blockerViolationsWeight;
    }

    public int getCriticalViolationsWeight() {
      return criticalViolationsWeight;
    }

    public void setCriticalViolationsWeight(int criticalViolationsWeight) {
      this.criticalViolationsWeight = criticalViolationsWeight;
    }

    public int getMajorViolationWeight() {
      return majorViolationWeight;
    }

    public void setMajorViolationWeight(int majorViolationWeight) {
      this.majorViolationWeight = majorViolationWeight;
    }

    public boolean isAllowNegative() {
      return allowNegative;
    }

    public void setAllowNegative(boolean allowNegative) {
      this.allowNegative = allowNegative;
    }

    @Override public String toString() {
      return "ViolationsScoreSettings{" +
        "blockerViolationsWeight=" + blockerViolationsWeight +
        ", criticalViolationsWeight=" + criticalViolationsWeight +
        ", majorViolationWeight=" + majorViolationWeight +
        ", allowNegative=" + allowNegative +
        ", disabled=" + isDisabled() +
        ", weight=" + getWeight() +
        ", criteria=" + getCriteria() +
        '}';
    }
  }

  @Override public String toString() {
    return "QualityScoreSettings{" +
      "codeCoverage=" + codeCoverage +
      ", unitTests=" + unitTests +
      ", violations=" + violations +
      ", disabled=" + isDisabled() +
      ", weight=" + getWeight() +
      ", criteria=" + getCriteria() +
      '}';
  }
}
