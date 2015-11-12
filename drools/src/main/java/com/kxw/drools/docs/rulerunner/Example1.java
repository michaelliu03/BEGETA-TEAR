package com.kxw.drools.docs.rulerunner;

public class Example1 {
    public static void main(String[] args) throws Exception {
        new RuleRunner().runRules( new String[] {"docs/runner/Example1.drl"},
                new Object[0] );
    }
}