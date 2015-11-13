package os.nut.drools;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 * {<a href='http://www.tuicool.com/articles/InMjei'>@link</a>}
 */
public class DroolsTest extends DroolsBaseTest {

	@Test
	public void test() {
		KieSession kSession = kieContainer.newKieSession("ksession-rules");
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);
		kSession.insert(message);
		kSession.fireAllRules();
	}
}
