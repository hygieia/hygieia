package com.capitalone.dashboard.model;

import org.joda.time.DateTime;

/**
 * CollectorItem extension to store the chatops serverUrl, roomName and authToken.
 */
public class ChatOpsRepo extends CollectorItem {
    private static final String CHATOPS_SERVER_URL = "chatOpsServerUrl";
    private static final String CHATOPS_ROOM_NAME = "roomName";
    private static final String CHATOPS_AUTH_TOKEN = "chatOpsAuthToken";
    private static final String LAST_UPDATE_TIME = "lastUpdate";

    
    
    public static String getChatopsAuthToken() {
		return CHATOPS_AUTH_TOKEN;
	}

	public static String getChatopsServerUrl() {
		return CHATOPS_SERVER_URL;
	}

	public static String getChatopsRoomName() {
		return CHATOPS_ROOM_NAME;
	}

	public DateTime getLastUpdateTime() {
        Object latest = getOptions().get(LAST_UPDATE_TIME);
        return (DateTime) latest;
    }

    public void setLastUpdateTime(DateTime date) {
        getOptions().put(LAST_UPDATE_TIME, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) {
        	return false;
        }

        return getChatopsServerUrl().equals(ChatOpsRepo.getChatopsServerUrl()) &&  getChatopsAuthToken().equals(ChatOpsRepo.getChatopsAuthToken());
    }

    @Override
    public int hashCode() {
        return getChatopsServerUrl().hashCode();
    }

}
