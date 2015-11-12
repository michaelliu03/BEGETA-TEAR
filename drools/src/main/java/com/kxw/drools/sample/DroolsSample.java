package com.kxw.drools.sample;

import java.util.Map;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * {<a href='http://www.cnblogs.com/bianwenlong/p/4081434.html'>@link</a>}
 */
public class DroolsSample {
    public static void main(String[] args) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession kSession = kc.newKieSession("session-rules");
        User user = new User();
        user.setUserId(123);
        user.setUserScore(100);
        user.setVipLevel(10);
        Item item = new Item(1001);
        item.setItemPrice(10);
        item.setItemCategoty("fruit");
        Purchase pur = new Purchase();
        pur.setUser(user);
        pur.addItem(item, 10);
        kSession.insert(pur);
        kSession.fireAllRules();
        Map<String, Float> map = pur.getFavour();
        Set<String> set = map.keySet();
        for (String s : set) {
            System.out.println(s + " " + map.get(s));
        }
    }
}