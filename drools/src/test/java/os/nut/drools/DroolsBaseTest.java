/**
 * Copyright © 2014 Xiong Zhijun, All Rights Reserved.
 * Email : hust.xzj@gmail.com
 */
package os.nut.drools;

import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

/**
 * @author Xiong Zhijun
 * @email hust.xzj@gmail.com
 * 
 */
public abstract class DroolsBaseTest {

	protected KieServices kieServices;
	protected KieContainer kieContainer;

	@Before
	public void setUp() {
		kieServices = KieServices.Factory.get();
		kieContainer = kieServices.getKieClasspathContainer();
	}

}

/**
 *
 * 利用kieContainer对象创建一个新的KieSession，创建session的时候我们传入了一个name：“ksession-rules”，这个字符串很眼熟吧，这个就是我们定义的kmodule.xml文件中定义的ksession的name。kieContainer根据kmodule.xml定义的ksession的名称找到KieSession的定义，然后创建一个KieSession的实例。
 KieSession就是一个到规则引擎的链接，通过它就可以跟规则引擎通讯，并且发起执行规则的操作。
 通过kSession.insert方法来将事实（Fact）插入到引擎中，也就是Working Memory中。
 然后通过kSession.fireAllRules方法来通知规则引擎执行规则。
 这样一个完整的Drools例子就完成了，包含了规则定义（DRL文件编写）、模块定义（kmodule.xml编写）、执行代码编写三个过程。
 */
//mvn -e exec:java -Dexec.mainClass="com.sample.DroolsTest"
//http://stackoverflow.com/questions/21466716/unknown-kiesession-name-in-drools-6-0-while-trying-to-add-drools-to-existing-ma
