package com.capitalone.dashboard.api.domain;

import com.google.common.collect.Iterables;

import java.net.URI;
import java.util.ArrayList;

/**
 * This class will get the details from Test Execution
 */
public class TestExecution extends VersionableIssue<TestExecution> {
    private Iterable<Test> tests;

    public TestExecution(URI self, String key, Long id) {
        super(self, key, id);
    }

    @Override
    public TestExecution cloneTest() throws CloneNotSupportedException {
        TestExecution myTestExec=new TestExecution(getSelf(),getKey(),getId());
        if(tests!=null && !Iterables.isEmpty(tests)){
            ArrayList<Test> myTests=new ArrayList<Test>();
            for(Test t:tests){
                myTests.add(t.cloneTest());
            }
            myTestExec.tests=myTests;
        }
    return myTestExec;
    }

    public Iterable<Test> getTests()
    {
        return tests;
    }
    public void setTests(Iterable<Test> tests) {
        try {
            this.setOldVersion(this.cloneTest());
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("CANNOT CLONE THIS OBJECT because of:"+e.getMessage());
        }
        this.tests = tests;
    }

    public static class Test extends VersionableIssue<Test> {
        private Integer rank;
        private TestRun.Status status;

        public Test(URI self, String key, Long id) {
            super(self, key, id);
        }

        public Test(URI self,String key,Long id,Integer rank,TestRun.Status status){
            super(self, key, id);
            this.status=status;
            this.rank=rank;
        }

        public Test cloneTest() throws CloneNotSupportedException {
            Test myTest=new Test(this.getSelf(),this.getKey(),this.getId());

            if(this.rank!=null){
                myTest.rank=rank;
            }
            //TODO: MANAGE ENUMS
            if(this.status!=null){
               myTest.status=this.status;
            }
        return myTest;}
    }

}
