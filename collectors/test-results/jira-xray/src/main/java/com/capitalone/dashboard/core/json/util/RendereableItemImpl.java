package com.capitalone.dashboard.core.json.util;

/**
 * Created by lucho on 22/08/16.
 */
public class RendereableItemImpl implements RendereableItem {

    private String raw;
    private String rendered;

    public RendereableItemImpl(String raw, String rendered) {
        this.raw=raw;
        this.rendered=rendered;
    }

    public String getRendered() {
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered=rendered;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw=raw;
    }
}
