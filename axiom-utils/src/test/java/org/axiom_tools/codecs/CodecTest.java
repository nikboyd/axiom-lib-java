/**
 * Copyright 2013,2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.axiom_tools.codecs;

import java.util.HashMap;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confirms proper operation of codec class.
 */
public class CodecTest {

    private static final String Indent = "\n  ";

    @Test
    public void sampleMessages() {
        getLogger().info(Indent + ValueMap.withMessages("a sample message").toJSON());
        getLogger().info(Indent + ValueMap.withID("76543210-76543210-76543210").toJSON());
    }

    @Test
    public void sampleMap() {
        HashMap m = new HashMap();
        m.put("ddd", "000");
        m.put("eee", "111");
        m.put("fff", "222");

        String[] texts = {"aaa", "bbb", "ccc"};
        ValueMap vm = new ValueMap();
        vm.with("xxx", "yyy");
        vm.withAll("sss", texts);
        vm.with("mmm", m);
        vm.with("nnn", 5);
        String json = vm.toJSON();
        getLogger().info(Indent + json);

        String ddd = vm.getValue("mmm.ddd");
        String sss = vm.getValue("sss[1]");
        Integer nnn = vm.getValue("nnn");
        getLogger().info("ddd: " + ddd);
        getLogger().info("sss[1]: " + sss);
        getLogger().info("nnn: " + nnn);

        ValueMap result = ValueMap.fromJSON(json);
        assertFalse(result == null);
        assertTrue(result.resembles(vm));
        result.reportDifferences(vm);
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

}
