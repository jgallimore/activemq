/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.partition;

import org.apache.activemq.partition.dto.Partitioning;
import org.apache.activemq.partition.dto.Target;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

public class PartitionBrokerPluginTest {

    @Test
    public void testShouldUnmarshallConfigFromJson() throws Exception {
        final PartitionBrokerPlugin plugin = new PartitionBrokerPlugin();
        plugin.setConfigAsJson("{\"by_client_id\":{\"client2\":{\"ids\":[\"broker2\"]},\"client1\":{\"ids\":[\"broker1\"]}}," +
                "\"by_user_name\":{\"user1\":{\"ids\":[\"broker1\"]},\"user2\":{\"ids\":[\"broker2\"]}}," +
                "\"by_source_ip\":{\"192.168.0.2\":{\"ids\":[\"broker2\"]},\"192.168.0.1\":{\"ids\":[\"broker1\"]}}," +
                "\"by_queue\":{\"A\":{\"ids\":[\"broker3\"]},\"B\":{\"ids\":[\"broker4\"]}}," +
                "\"by_topic\":{\"C\":{\"ids\":[\"broker5\"]},\"D\":{\"ids\":[\"broker6\"]}}}\n");

        final Partitioning partitioning = plugin.getConfig();

        Assert.assertEquals("broker1", getTarget(partitioning.byClientId.get("client1")));
        Assert.assertEquals("broker2", getTarget(partitioning.byClientId.get("client2")));
        Assert.assertEquals("broker3", getTarget(partitioning.byQueue.get("A")));
        Assert.assertEquals("broker4", getTarget(partitioning.byQueue.get("B")));
        Assert.assertEquals("broker5", getTarget(partitioning.byTopic.get("C")));
        Assert.assertEquals("broker6", getTarget(partitioning.byTopic.get("D")));
        Assert.assertEquals("broker1", getTarget(partitioning.byUserName.get("user1")));
        Assert.assertEquals("broker2", getTarget(partitioning.byUserName.get("user2")));
        Assert.assertEquals("broker1", getTarget(partitioning.bySourceIp.get("192.168.0.1")));
        Assert.assertEquals("broker2", getTarget(partitioning.bySourceIp.get("192.168.0.2")));
    }

    private String getTarget(final Target target) {
        Assert.assertNotNull(target);
        final HashSet<String> ids = target.getIds();
        Assert.assertNotNull(ids);
        Assert.assertEquals(1, ids.size());
        return ids.iterator().next();
    }


}