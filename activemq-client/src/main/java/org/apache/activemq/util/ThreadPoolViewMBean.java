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

public interface ThreadPoolViewMBean {


    /**
     * @return number of active threads in the pool
     */
    int getActiveCount();

    /**
     * @return the core number of threads
     */
    int getCorePoolSize();

    /**
     * @return the size of the thread pool, at the largest it has been
     */
    int getLargestPoolSize();

    /**
     * @return the maximum size of the thread pool
     */
    int getMaximumPoolSize();

    /**
     * @return the current size of the thread pool
     */
    int getPoolSize();

    /**
     * @return number of items waiting to be executed in the queue
     */
    int getQueueLength();

    /**
     * @return number of tasks submitted for execution
     */
    long getTaskCount();

    /**
     * @return number of tasks completed
     */
    long getCompletedTaskCount();
}
