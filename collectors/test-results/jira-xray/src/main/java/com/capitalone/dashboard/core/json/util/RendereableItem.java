package com.capitalone.dashboard.core.json.util;

/**
 * Created by lucho on 22/08/16.
 */
public interface RendereableItem {

    String getRendered();
    void setRendered(String rendered);

    String getRaw();
    void setRaw(String raw);
}
