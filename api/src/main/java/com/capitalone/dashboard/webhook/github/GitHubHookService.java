package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.misc.HygieiaException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface GitHubHookService {
    String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException;
}
