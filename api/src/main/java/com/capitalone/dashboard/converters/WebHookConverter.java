package com.capitalone.dashboard.converters;

import com.capitalone.dashboard.webhook.settings.GitHubWebHookSettings;
import com.capitalone.dashboard.webhook.settings.WebHookSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

@Component
@ConfigurationPropertiesBinding
public class WebHookConverter implements Converter<String, WebHookSettings> {
    private static final Logger LOG = LoggerFactory.getLogger(WebHookConverter.class);

    private static final String GITHUB_PROPERTY = "gitHub";

    @Override
    public WebHookSettings convert(String s) {
        if (StringUtils.isEmpty(s)) { return null; }

        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(s);
        } catch (ParseException e) {
           LOG.error("Cannot be parsed into JSONObject : "+s);
        }

        if (json == null) { return null; }

        WebHookSettings webHookSettings = new WebHookSettings();
        setGithubSettingsProperty(json, webHookSettings);

        return webHookSettings;
    }

    protected void setGithubSettingsProperty(JSONObject json, WebHookSettings webHookSettings) {
        JSONObject gitHubWebHookSettings = (JSONObject)json.get(GITHUB_PROPERTY);
        if (gitHubWebHookSettings == null) { return; }

        GitHubWebHookSettings settings = parseAsTargetType(gitHubWebHookSettings.toJSONString(), GitHubWebHookSettings.class);
        webHookSettings.setGitHubWebHookSettings(settings);
    }

    protected <T> T parseAsTargetType(String jsonString, Class<T> targetType) {
        T type = null;

        try {
            type = new ObjectMapper().readValue(jsonString, targetType);
        } catch (IOException e) {
            LOG.info("Could not be converted into "+targetType+": "+jsonString);
        }

        return type;
    }
}
