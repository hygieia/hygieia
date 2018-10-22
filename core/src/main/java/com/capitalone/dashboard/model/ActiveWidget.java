package com.capitalone.dashboard.model;

import java.util.Objects;

/**
 * Created by stevegal on 20/10/2018.
 */
public class ActiveWidget {

  private String type;

  private String title;

  public ActiveWidget(Builder builder) {
    this.type = builder.type;
    this.title = builder.title;
  }

  public ActiveWidget(){

  }

  public static Builder newActiveWidget() {
    return new Builder();
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ActiveWidget that = (ActiveWidget) o;
    return Objects.equals(type, that.type) &&
        Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title);
  }

  public static final class Builder {
    private String type;
    private String title;

    public Builder() {
    }

    public ActiveWidget build() {
      return new ActiveWidget(this);
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }
  }
}
