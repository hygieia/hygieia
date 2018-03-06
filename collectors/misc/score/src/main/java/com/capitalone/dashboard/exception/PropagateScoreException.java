package com.capitalone.dashboard.exception;

import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import com.capitalone.dashboard.model.ScoreWeight;

public class PropagateScoreException extends Exception {

  private final ScoreTypeValue score;

  private final ScoreWeight.ProcessingState state;

  public PropagateScoreException(ScoreTypeValue score, ScoreWeight.ProcessingState state) {
    super();
    this.score = score;
    this.state = state;
  }

  public PropagateScoreException(String message, ScoreTypeValue score, ScoreWeight.ProcessingState state) {
    super(message);
    this.score = score;
    this.state = state;
  }

  public PropagateScoreException(String message, Throwable cause, ScoreTypeValue score, ScoreWeight.ProcessingState state) {
    super(message, cause);
    this.score = score;
    this.state = state;
  }

  public PropagateScoreException(Throwable cause, ScoreTypeValue score, ScoreWeight.ProcessingState state) {
    super(cause);
    this.score = score;
    this.state = state;
  }

  public ScoreTypeValue getScore() {
    return score;
  }

  public ScoreWeight.ProcessingState getState() {
    return state;
  }
}
