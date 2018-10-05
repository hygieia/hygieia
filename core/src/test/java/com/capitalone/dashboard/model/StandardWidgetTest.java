package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class StandardWidgetTest {

    @Test
    public void getOptions() {
    }

    @Test
    public void testStandardwidget() {
        StandardWidget sw = new StandardWidget(CollectorType.Build, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("build"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "build0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("build"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "build0"));

        sw = new StandardWidget(CollectorType.SCM, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("repo"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "repo0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("repo"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "repo0"));

        sw = new StandardWidget(CollectorType.CodeQuality, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "codeanalysis0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "codeanalysis0"));

        sw = new StandardWidget(CollectorType.Test, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "codeanalysis0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "codeanalysis0"));

        sw = new StandardWidget(CollectorType.StaticSecurityScan, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "codeanalysis0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "codeanalysis0"));


        sw = new StandardWidget(CollectorType.LibraryPolicy, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "codeanalysis0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("codeanalysis"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "codeanalysis0"));

        sw = new StandardWidget(CollectorType.Deployment, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("deploy"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "deploy0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("deploy"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "deploy0"));

        sw = new StandardWidget(CollectorType.AgileTool, ObjectId.get());
        assertTrue(sw.getName().equalsIgnoreCase("feature"));
        assertTrue(Objects.equals(sw.getOptions().get("id"), "feature0"));
        assertTrue(sw.getWidget().getName().equalsIgnoreCase("feature"));
        assertTrue(Objects.equals(sw.getWidget().getOptions().get("id"), "feature0"));
    }
}