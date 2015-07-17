package com.capitalone.dashboard.model;

public class WidgetResponse {
    private final Component component;
    private final Widget widget;

    public WidgetResponse(Component component, Widget widget) {
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
