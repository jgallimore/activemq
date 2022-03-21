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
package org.apache.activemq.store.kahadb;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.PersistenceAdapterViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.IOHelper;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.management.ObjectName;
import java.io.File;
import java.util.Set;

public class PersistenceAdapterViewTest {

    private BrokerService brokerService;
    private KahaDBPersistenceAdapter kahaDBPersistenceAdapter;

    @Test
    public void setShouldExposePersistenceAdapterStats() throws Exception {

        brokerService = createBroker(true);
        brokerService.start();

        sendSomeMessages();

        final PersistenceAdapterViewMBean persistenceAdapterView = getPersistenceAdapterView();
        final String output = persistenceAdapterView.getStatistics();

        final String expected = "{\"slowReadTime\":{\"maxTime\":0,\"averageTime\":0.0,\"minTime\":0,\"totalTime\":0,\"count\":0,\"averagePerSecond\":0.0," +
                "\"averagePerSecondExMinMax\":0.0,\"averageTimeExMinMax\":0.0},\"writeTime\":{\"maxTime\":26,\"averageTime\":12.490196078431373,\"minTime\":2," +
                "\"totalTime\":637,\"count\":51,\"averagePerSecond\":80.06279434850863,\"averagePerSecondExMinMax\":80.45977011494253,\"averageTimeExMinMax\":12.428571428571429}," +
                "\"slowWriteTime\":{\"maxTime\":0,\"averageTime\":0.0,\"minTime\":0,\"totalTime\":0,\"count\":0,\"averagePerSecond\":0.0,\"averagePerSecondExMinMax\":0.0," +
                "\"averageTimeExMinMax\":0.0},\"slowCleanupTime\":{\"maxTime\":0,\"averageTime\":0.0,\"minTime\":0,\"totalTime\":0,\"count\":0,\"averagePerSecond\":0.0," +
                "\"averagePerSecondExMinMax\":0.0,\"averageTimeExMinMax\":0.0},\"readTime\":{\"maxTime\":0,\"averageTime\":0.0,\"minTime\":0,\"totalTime\":0,\"count\":0," +
                "\"averagePerSecond\":0.0,\"averagePerSecondExMinMax\":0.0,\"averageTimeExMinMax\":0.0}}";

        // These stats will vary between machines. We're looking to assert that the structure is correct.
        JSONAssert.assertEquals(expected, output, new CustomComparator(JSONCompareMode.STRICT, new Customization("***", (o1, o2) -> true)));

        System.out.println(output);

        brokerService.stop();
    }

    protected BrokerService createBroker(boolean doCleanupOnStop) throws Exception {
        File schedulerDirectory = new File("target/scheduler");
        File kahadbDir = new File("target/kahadb");

        for (File directory: new File[]{schedulerDirectory, kahadbDir}) {
            IOHelper.mkdirs(directory);
            IOHelper.deleteChildren(directory);
        }

        BrokerService broker = new BrokerService();
        broker.setUseJmx(true);

        kahaDBPersistenceAdapter = new KahaDBPersistenceAdapter();
        kahaDBPersistenceAdapter.setDirectory(kahadbDir);
        kahaDBPersistenceAdapter.setJournalMaxFileLength(16*1024);

        kahaDBPersistenceAdapter.setCleanupInterval(0);
        kahaDBPersistenceAdapter.setCleanupOnStop(doCleanupOnStop);

        broker.setPersistenceAdapter(kahaDBPersistenceAdapter);
        return broker;
    }

    public void sendSomeMessages() throws Exception {
        Connection connection = new ActiveMQConnectionFactory("vm://localhost").createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        connection.start();
        final ActiveMQQueue destination = new ActiveMQQueue("MYQUEUE");
        final int numMessages = 50;
        final long time = 1000l;
        final byte[] payload = new byte[1024];
        MessageProducer producer = session.createProducer(destination);
        for (int i = 0; i < numMessages; i++) {
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(payload);
            bytesMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
            producer.send(bytesMessage);
        }

        connection.close();
    }

    private PersistenceAdapterViewMBean getPersistenceAdapterView() throws Exception {
        final Set<ObjectName> objectNames = brokerService.getManagementContext().queryNames(new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,service=PersistenceAdapter,instanceName=KahaDBPersistenceAdapter*"), null);
        Assert.assertEquals(1, objectNames.size());

        final PersistenceAdapterViewMBean view = (PersistenceAdapterViewMBean) brokerService.getManagementContext().newProxyInstance(objectNames.iterator().next(), PersistenceAdapterViewMBean.class, true);


        return view;
    }
}
