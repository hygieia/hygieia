package jenkins.plugins.hygieia;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;

public class HttpClientStub extends HttpClient {

    private int numberOfCallsToExecuteMethod;
    private int httpStatus;
    private boolean failAlternateResponses = false;

    @Override
    public int executeMethod(HttpMethod httpMethod) {
        numberOfCallsToExecuteMethod++;
        if (failAlternateResponses && (numberOfCallsToExecuteMethod % 2 == 0)) {
            return HttpStatus.SC_NOT_FOUND;
        } else {
            return httpStatus;
        }
    }

    public int getNumberOfCallsToExecuteMethod() {
        return numberOfCallsToExecuteMethod;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setFailAlternateResponses(boolean failAlternateResponses) {
        this.failAlternateResponses = failAlternateResponses;
    }
}
