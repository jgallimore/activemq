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

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolView implements ThreadPoolViewMBean {

    private final ThreadPoolExecutor executor;

    public ThreadPoolView(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    @Override
    public int getCorePoolSize() {
        return executor.getCorePoolSize();
    }

    @Override
    public int getLargestPoolSize() {
        return executor.getLargestPoolSize();
    }

    @Override
    public int getMaximumPoolSize() {
        return executor.getMaximumPoolSize();
    }

    @Override
    public int getPoolSize() {
        return executor.getPoolSize();
    }

    @Override
    public int getQueueLength() {
        return executor.getQueue().size();
    }

    @Override
    public long getTaskCount() {
        return executor.getTaskCount();
    }

    @Override
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }
}
