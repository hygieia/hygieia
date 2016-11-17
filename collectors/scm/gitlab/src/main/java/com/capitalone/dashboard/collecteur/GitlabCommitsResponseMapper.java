package com.capitalone.dashboard.collecteur;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Commit;

@Component
public class GitlabCommitsResponseMapper {
	
	private static final Log LOG = LogFactory.getLog(GitlabCommitsResponseMapper.class);
	
	public List<Commit> map(String jsonResponse, String repoUrl, String branch) {
		List<Commit> commits = new ArrayList<>();
		JSONArray jsonArray = paresAsArray(jsonResponse);
		for (Object item : jsonArray) {
			JSONObject jsonObject = (JSONObject) item;
			commits.add(buildCommit(jsonObject, repoUrl, branch));
		}
		return commits;
	}
	
	private Commit buildCommit(JSONObject jsonObject, String repoUrl, String repoBranch) {
		String author = str(jsonObject, "author_name");
		String message = str(jsonObject, "message");
		String id = str(jsonObject, "id");
		long timestamp = new DateTime(str(jsonObject, "created_at")).getMillis();

		Commit commit = new Commit();
		commit.setTimestamp(System.currentTimeMillis());
		commit.setScmUrl(repoUrl);
		commit.setScmBranch(repoBranch);
		commit.setScmRevisionNumber(id);
		commit.setScmAuthor(author);
		commit.setScmCommitLog(message);
		commit.setScmCommitTimestamp(timestamp);
		commit.setNumberOfChanges(1);
		return commit;
	}
	
	private String str(JSONObject json, String key) {
		Object value = json.get(key);
		return value == null ? null : value.toString();
	}
	
	private JSONArray paresAsArray(String response) {
		try {
			return (JSONArray) new JSONParser().parse(response);
		} catch (ParseException pe) {
			LOG.error(pe.getMessage());
		}
		return new JSONArray();
	}

}
