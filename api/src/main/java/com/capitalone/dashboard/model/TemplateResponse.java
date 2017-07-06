package com.capitalone.dashboard.model;

public class TemplateResponse {
    private final Component component;
    private final Widget widget;

    public TemplateResponse(Component component, Widget widget) {
        this.component = component;
        this.widget = widget;
    }

    public Component getComponent() {
        return component;
    }

    public Widget getWidget() {
        return widget;
    }
}
