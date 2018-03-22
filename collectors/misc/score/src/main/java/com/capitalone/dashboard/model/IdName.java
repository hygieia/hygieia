package com.capitalone.dashboard.model;


public class IdName {

  private String name;
  private String id;

  public IdName(String id, String name) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    IdName idName = (IdName) o;

    if (!name.equals(idName.name))
      return false;
    return id.equals(idName.id);

  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + id.hashCode();
    return result;
  }
}
