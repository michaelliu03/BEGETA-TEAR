/**
 * Copyright © 2014 Xiong Zhijun, All Rights Reserved.
 * Email : hust.xzj@gmail.com
 */
package os.nut.drools;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * @author Xiong Zhijun
 * @email hust.xzj@gmail.com
 * 
 */
public class KieFileSystemTest {

	@Test
	public void test() {
		KieServices kieServices = KieServices.Factory.get();
		KieResources resources = kieServices.getResources();
		KieModuleModel kieModuleModel = kieServices.newKieModuleModel();

		KieBaseModel baseModel = kieModuleModel.newKieBaseModel(
				"FileSystemKBase").addPackage("rules");
		baseModel.newKieSessionModel("FileSystemKSession");
		KieFileSystem fileSystem = kieServices.newKieFileSystem();

		String xml = kieModuleModel.toXML();
		System.out.println(xml);
		fileSystem.writeKModuleXML(xml);
		
		fileSystem.write("src/main/resources/rules/rule.drl", resources
				.newClassPathResource("kiefilesystem/KieFileSystemTest.drl"));

		KieBuilder kb = kieServices.newKieBuilder(fileSystem);
		kb.buildAll();
		if (kb.getResults().hasMessages(Level.ERROR)) {
			throw new RuntimeException("Build Errors:\n"
					+ kb.getResults().toString());
		}
		KieContainer kContainer = kieServices.newKieContainer(kieServices
				.getRepository().getDefaultReleaseId());

		assertNotNull(kContainer.getKieBase("FileSystemKBase"));
		KieSession kSession = kContainer.newKieSession("FileSystemKSession");

		kSession.fireAllRules();
	}
}
