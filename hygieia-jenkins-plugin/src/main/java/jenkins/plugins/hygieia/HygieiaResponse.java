package jenkins.plugins.hygieia;


import org.apache.commons.lang3.StringUtils;

public class HygieiaResponse {
    private int responseCode;
    private String responseValue;

    public HygieiaResponse(int responseCode, String responseValue) {
        this.responseCode = responseCode;
        this.responseValue = responseValue;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(String responseValue) {
        this.responseValue = responseValue;
    }

    public String toString() {
        String resp = "Response Code: " + responseCode + ". ";
        if (StringUtils.isEmpty(responseValue)) return resp;
        return resp + "Response Value= " + responseValue;
    }
}
