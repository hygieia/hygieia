package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.util.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Bean to hold settings specific to the git collector.
 */
@Component
@ConfigurationProperties(prefix = "git")
public class GitSettings {
    private String cron;
    private String host;
    private String key;
    private int firstRunHistoryDays;
    private String api;


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
    public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

    private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

    Date getRunDate(GitRepo repo, boolean firstRun) {
        if (firstRun) {
            int firstRunDaysHistory = this.getFirstRunHistoryDays();
            if (firstRunDaysHistory > 0) {
                return DateUtils.getDate(new Date(), -firstRunDaysHistory, 0);
            } else {
                return DateUtils.getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
            }
        } else {
            return DateUtils.getDate(repo.getLastUpdateTime(), 0, -10);
        }
    }
}
