package org.axiom_tools.domain;

import java.util.Optional;
import org.axiom_tools.storage.PhoneStorage;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author nik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfiguration.class })
public class SampleTest {

    @BeforeClass
    public static void prepare() {
        LoggerFactory.getLogger(SampleTest.class).info("started SampleTest");
    }

    @Autowired(required = true)
    private PhoneStorage phoneStore;

    @Test
    public void sample() {
        int count = phoneStore.countAll();
        getLogger().info("count = " + count);

        PhoneNumber p = phoneStore.save(PhoneNumber.from("888-888-8888"));
        assertFalse(p == null);
        assertTrue(phoneStore.countAll() > count);
        getLogger().info("count = " + phoneStore.countAll());

        Optional<PhoneNumber> n = phoneStore.findID(p.getKey());
        assertTrue(n.isPresent());
        getLogger().info("found: " + n.get().formatNumber());

        phoneStore.delete(n.get());
        assertTrue(phoneStore.countAll() == count);
        getLogger().info("count = " + phoneStore.countAll());
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

}
