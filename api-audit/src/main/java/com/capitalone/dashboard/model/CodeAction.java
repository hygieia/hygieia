package com.capitalone.dashboard.model;

public class CodeAction {

    private CodeActionType type;
    private long timestamp;
    private String actor;
    private String actorLDAPDN;
    private String message;

    public CodeAction() {
    }

    public CodeAction(CodeActionType type, long timestamp, String actor, String actorLDAPDN, String message) {
        this.type = type;
        this.timestamp = timestamp;
        this.actor = actor;
        this.actorLDAPDN = actorLDAPDN;
        this.message = message;
    }

    public CodeAction(CodeAction item) {
        this.type = item.getType();
        this.timestamp = item.getTimestamp();
        this.actor = item.getActor();
        this.actorLDAPDN = item.actorLDAPDN;
        this.message = item.getMessage();
    }

    public CodeActionType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getActor() {
        return actor;
    }

    public String getActorLDAPDN() {
        return actorLDAPDN;
    }

    public String getMessage() {
        return message;
    }
}
