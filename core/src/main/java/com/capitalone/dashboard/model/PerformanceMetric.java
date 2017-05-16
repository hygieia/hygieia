package com.capitalone.dashboard.model;

public class PerformanceMetric {
    //  private String type; //example "Business Transaction", "Health Rules" etc.
    private String name; //Example: "totalTransactions",
    private Object value; //original value coming from the tool.
    //private String formattedValue; //formatted for Hygieia, if needed
    //private PerformanceMetricStatus status;
    //private String statusMessage; // free format text


    /* public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }
 */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

     /*  public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    public PerformanceMetricStatus getStatus() {
        return status;
    }

    public void setStatus(PerformanceMetricStatus status) {
        this.status = status;
    }

 public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }*/
}
