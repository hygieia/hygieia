package com.capitalone.dashboard.model.quality;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "report")
public class JacocoXmlReport implements CodeQualityVisitee{

    @XmlElement(name="counter")
    private List<Counter> counters;


    @Override
    public void accept(CodeQualityVisitor visitor) {
        visitor.visit(this);
    }

    public List<Counter> getCounters() {
        return counters;
    }

    public void setCounters(List<Counter> counters) {
        this.counters = counters;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Counter {
        @XmlAttribute(name="type")
        private CounterType type;

        @XmlAttribute(name="missed")
        private int missed;

        @XmlAttribute(name="covered")
        private int covered;

        public int getMissed() {
            return missed;
        }

        public void setMissed(int missed) {
            this.missed = missed;
        }

        public int getCovered() {
            return covered;
        }

        public void setCovered(int covered) {
            this.covered = covered;
        }

        public CounterType getType() {
            return type;
        }

        public void setType(CounterType type) {
            this.type = type;
        }
    }

    public enum CounterType {
        INSTRUCTION,BRANCH,LINE,COMPLEXITY,METHOD,CLASS;
    }
}
