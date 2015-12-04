package co.leantechniques.hygieia.rally.domain;

import com.google.gson.annotations.SerializedName;

public class Project extends RallyObject{
    @SerializedName("_refObjectName")
    String name;
}