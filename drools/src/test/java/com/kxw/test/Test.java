package com.kxw.test;

import com.kxw.begeta.model.Begeta;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Created by Kingson.wu on 2015/11/11.
 */
public class Test {

    private static KieContainer kc;

    static {
        KieServices ks = KieServices.Factory.get();
        kc = ks.getKieClasspathContainer();
    }


    public static void main(String[] args) {
        Begeta begeta = new Begeta();
        begeta.setName("kingson");
        begeta.setLevel("top");

        KieSession ksession = kc.newKieSession("begeta_session");
        ksession.insert(begeta);
        ksession.fireAllRules();
        ksession.dispose();

    }

}
