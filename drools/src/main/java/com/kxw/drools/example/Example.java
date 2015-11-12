package com.kxw.drools.example;

import com.kxw.begeta.model.Begeta;
import com.kxw.begeta.model.DragonBall;
import com.kxw.drools.docs.rulerunner.RuleRunner;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Created by Kingson.wu on 2015/11/12.
 * {<a href='http://www.skills421.com/tutorials-drools-components.jsp'>@link</a>}
 */
public class Example {

    public static void main(String[] args) {

        /** Create a KieServices object first */
        KieServices  kieServices = KieServices.Factory.get();

        /** from the kieServices object create the following */
        KieResources kieResources = kieServices.getResources();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        KieRepository kieRepository = kieServices.getRepository();

        /** Create a Resource object for every rule file that you want to add to the KnowledgeBase */
        String ruleFile = "helloWorld.drl";
        Resource resource = kieResources.newClassPathResource(ruleFile, RuleRunner.class);

        /** create a resourcepath = "src/main/resources/"+packageName+"/"+ruleFile; */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String resourcepath = "F:\\IdeaProject13\\BEGETA-TEAR\\drools\\target\\classes\\com\\kxw\\drools\\helloworld\\helloWorld.drl";
        /** write the resource to the kieFilesystem */
        kieFileSystem.write(resourcepath, resource);

        /** Once all the Rules are added, create a KieBuilder and build the rules */
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        /** Now create a KieContainer */
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());

        /** Finally create a KieSession from the KieContainer */
        KieSession kieSession = kieContainer.newKieSession();

        /** Insert all facts and globals into the KieSession and add any EventListeners */

        final Begeta fact = new Begeta();
        fact.setName("kingson");
        fact.setLevel("Top");

        kieSession.insert(fact);
        kieSession.setGlobal("Dragon Ball", new DragonBall());
        kieSession.addEventListener(new DefaultAgendaEventListener());
        kieSession.addEventListener(new DefaultProcessEventListener());
        //kieSession.addEventListener(new DefaultWorkingMemoryEventListener());

        /** Fire the Rules */
        kieSession.fireAllRules();

        /** Don't forget to Dispose of the kieSession once you've finished with it */
        kieSession.dispose();
    }
}
