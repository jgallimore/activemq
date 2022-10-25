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
package org.apache.activemq.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class RegisterJmx {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterJmx.class);
    private static final Map<ThreadPoolExecutor, ObjectName> MANAGED_THREADPOOLS = new ConcurrentHashMap<>();

    public static void addJmx(ThreadPoolExecutor exec, final String name) {
        if (System.getProperties().getProperty("activemq.home") != null) {

            try {
                final ObjectName objectName = new ObjectName("org.apache.actievmq:type=ThreadPool,name=" + name);
                final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                server.registerMBean(new ThreadPoolView(exec), objectName);

                MANAGED_THREADPOOLS.put(exec, objectName);
            } catch (Exception e) {
                LOG.error("Error registering MBean for ThreadPoolExecutor " + name, e);
            }
        }
    }

    public static void removeJmx(ThreadPoolExecutor exec) {
        if (System.getProperties().getProperty("activemq.home") != null) {
            ObjectName objectName = MANAGED_THREADPOOLS.remove(exec);

            try {
                if (objectName == null) {
                    return;
                }

                final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                server.unregisterMBean(objectName);
            } catch (Exception e) {
                LOG.error("Error unregistering MBean for ThreadPoolExecutor " + objectName, e);
            }
        }
    }
}
