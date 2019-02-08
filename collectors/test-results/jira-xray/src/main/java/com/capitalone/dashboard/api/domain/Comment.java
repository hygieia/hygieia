package com.capitalone.dashboard.api.domain;

import com.capitalone.dashboard.core.json.util.RendereableItem;

/**
 * This class will get the details from comment section
 */
public class Comment implements RendereableItem {
    private String raw;
    private String rendered;
    public Comment(String raw,String rendered){
        this.raw=raw;
        this.rendered=rendered;
    }

    public Comment(RendereableItem item){
        this.raw=item.getRaw();
        this.rendered=item.getRendered();
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

    public Comment cloneComment(){
        return new Comment(this.raw,this.rendered);
    }
}
