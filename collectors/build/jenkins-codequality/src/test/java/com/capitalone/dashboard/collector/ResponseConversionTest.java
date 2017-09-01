package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JobContainer;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ResponseConversionTest {

    @Test
    public void ensureConversionHandledFromServer() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        server.expect(requestTo("/jobs")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\n" +
                        "    \"_class\": \"hudson.model.FreeStyleProject\",\n" +
                        "    \"jobs\": [\n" +
                        "        {\n" +
                        "            \"name\": \"job1\",\n" +
                        "            \"url\": \"http://localhost:8080/job/sss/2/\",\n" +
                        "            \"lastSuccessfulBuild\": {\n" +
                        "                \"timestamp\":123455,\n" +
                        "                \"artifacts\":[{\"relativePath\":\"rel\",\"fileName\":\"name\"}]\n" +
                        "            }\n"+
                        "        },\n" +
                        "        {\n" +
                        "            \"name\": \"job2\",\n" +
                        "            \"url\": \"http://localhost:8080/job/sss/1/\",\n" +
                        "            \"lastSuccessfulBuild\": {\n" +
                        "                \"timestamp\":123455,\n" +
                        "                \"artifacts\":[{\"relativePath\":\"rel\",\"fileMame\":\"name\"}]\n" +
                        "            }\n"+
                        "        }\n" +
                        "    ]\n" +
                        "}", MediaType.APPLICATION_JSON));

        ResponseEntity<JobContainer> jobResponse = restTemplate.exchange(new URI("/jobs"), HttpMethod.GET, null, JobContainer.class);

        //

        // Verify all expectations met
        server.verify();

        assertThat(jobResponse.getBody().getJobs()).hasSize(2);
        assertThat(jobResponse.getBody().getJobs().get(0).getName()).isEqualTo("job1");
    }

}
