package com.capitalone.dashboard.model.quality;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BugCollection")
public class FindBugsXmlReport implements CodeQualityVisitee {
    @Override
    public void accept(CodeQualityVisitor visitor) {
        visitor.visit(this);
    }

    @XmlElement(name = "file")
    private List<BugFile> files;

    public List<BugFile> getFiles() {
        return files;
    }

    public void setFiles(List<BugFile> files) {
        this.files = files;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BugFile {
        @XmlElement(name = "BugInstance")
        private List<BugInstance> bugCollection;

        public List<BugInstance> getBugCollection() {
            return bugCollection;
        }

        public void setBugCollection(List<BugInstance> bugCollection) {
            this.bugCollection = bugCollection;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BugInstance {

        @XmlAttribute
        private String type;

        @XmlAttribute
        private BugPriority priority;

        @XmlAttribute
        private BugCategory category;

        @XmlAttribute
        private String message;

        @XmlAttribute
        private int lineNumber;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public BugPriority getPriority() {
            return priority;
        }

        public void setPriority(BugPriority priority) {
            this.priority = priority;
        }

        public BugCategory getCategory() {
            return category;
        }

        public void setCategory(BugCategory category) {
            this.category = category;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }


    public enum BugPriority {
        Blocker, Low, Critical, Normal
    }

    public enum BugCategory {
        PERFORMANCE, BAD_PRACTICE
    }
}
