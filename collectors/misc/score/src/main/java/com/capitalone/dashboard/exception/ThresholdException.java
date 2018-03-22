package com.capitalone.dashboard.exception;

import com.capitalone.dashboard.model.score.settings.ScoreTypeValue;
import org.apache.commons.lang3.tuple.Pair;

public class ThresholdException extends Exception {

  private final ScoreTypeValue score;
  private final Pair<String, String> messagePair;

  public ThresholdException(Pair<String, String> messagePair, ScoreTypeValue score) {
    super(messagePair.getRight());
    this.messagePair = messagePair;
    this.score = score;
  }


  public ScoreTypeValue getScore() {
    return score;
  }

  public Pair<String, String> getMessagePair() {
    return messagePair;
  }
}
