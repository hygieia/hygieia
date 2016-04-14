package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.util.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "github")
public class GitHubSettings {
    private String cron;
    private String host;
    private String key;
    private int firstRunHistoryDays;


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

    private static final int FIRST_RUN_HISTORY_DEFAULT = 14;

    Date getRunDate(GitHubRepo repo, boolean firstRun) {
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
