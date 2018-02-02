package com.capitalone.dashboard.collector;

/**
 * Bean to hold settings specific to the score collector.
 */
public class QualityScoreSettings extends ScoreParamSettings {

  private ScoreParamSettings codeCoverage;

  private ScoreParamSettings unitTests;

  private ViolationsScoreSettings violations;

  public ScoreParamSettings getCodeCoverage() {
    return codeCoverage;
  }

  public void setCodeCoverage(ScoreParamSettings codeCoverage) {
    this.codeCoverage = codeCoverage;
  }

  public ScoreParamSettings getUnitTests() {
    return unitTests;
  }

  public void setUnitTests(ScoreParamSettings unitTests) {
    this.unitTests = unitTests;
  }

  public ViolationsScoreSettings getViolations() {
    return violations;
  }

  public void setViolations(ViolationsScoreSettings violations) {
    this.violations = violations;
  }

  public static class ViolationsScoreSettings extends ScoreParamSettings {

    public static final int BLOCKER_VIOLATIONS_WEIGHT = 20;
    public static final int CRITICAL_VIOLATIONS_WEIGHT = 5;
    public static final int MAJOR_VIOLATIONS_WEIGHT = 1;

    private int blockerViolationsWeight = BLOCKER_VIOLATIONS_WEIGHT;
    private int criticalViolationsWeight = CRITICAL_VIOLATIONS_WEIGHT;
    private int majorViolationWeight = MAJOR_VIOLATIONS_WEIGHT;
    private boolean allowNegative = false;

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
        '}';
    }
  }

  @Override public String toString() {
    return "QualityScoreSettings{" +
      "codeCoverage=" + codeCoverage +
      ", unitTests=" + unitTests +
      ", violations=" + violations +
      '}';
  }
}
