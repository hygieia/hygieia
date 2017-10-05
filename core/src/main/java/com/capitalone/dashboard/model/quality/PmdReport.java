package com.capitalone.dashboard.model.quality;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name="pmd")
@XmlAccessorType(XmlAccessType.FIELD)
public class PmdReport implements CodeQualityVisitee {

    @XmlElement(name="file")
    private List<PmdFile> files;

    @Override
    public void accept(CodeQualityVisitor visitor) {
        visitor.visit(this);
    }

    public List<PmdFile> getFiles() {
        return files;
    }

    public void setFiles(List<PmdFile> files) {
        this.files = files;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PmdFile{

        @XmlElement(name="violation")
        private List<PmdViolation> violations;

        public List<PmdViolation> getViolations() {
            return violations;
        }

        public void setViolations(List<PmdViolation> violations) {
            this.violations = violations;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PmdViolation {

        @XmlAttribute(name="priority")
        private int priority;

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }
}
