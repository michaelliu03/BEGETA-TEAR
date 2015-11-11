package com.kxw.drools.helloworld;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * {<a href='http://blog.csdn.net/luzhenyu111/article/details/44001837'>@link<a/>}
 */
public class HelloWorld {
    public static final void main(final String[] args) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("HelloWorldKS");
        final Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        ksession.insert(message);
        ksession.fireAllRules();
        ksession.dispose();
    }

    public static class Message {
        public static final int HELLO = 0;
        public static final int GOODBYE = 1;
        private String message;
        private int status;

        public void setMessage(String message) {
            this.message = message;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }
    }
}