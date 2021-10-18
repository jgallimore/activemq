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
package org.apache.activemq.partition.dto;



import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import java.util.HashMap;

/**
 * The main Configuration class for the PartitionBroker plugin
 */
public class Partitioning {

    /**
     * If a client connects with a clientId which is listed in the
     * map, then he will be immediately reconnected
     * to the partition target immediately.
     */
    @JsonbProperty("by_client_id")
    public HashMap<String, Target> byClientId;

    /**
     * If a client connects with a user priciple which is listed in the
     * map, then he will be immediately reconnected
     * to the partition target immediately.
     */
    @JsonbProperty("by_user_name")
    public HashMap<String, Target> byUserName;

    /**
     * If a client connects with source ip which is listed in the
     * map, then he will be immediately reconnected
     * to the partition target immediately.
     */
    @JsonbProperty("by_source_ip")
    public HashMap<String, Target> bySourceIp;

    /**
     * Used to map the preferred partitioning of queues across
     * a set of brokers.  Once a it is deemed that a connection mostly
     * works with a set of targets configured in this map, the client
     * will be reconnected to the appropriate target.
     */
    @JsonbProperty("by_queue")
    public HashMap<String, Target> byQueue;

    /**
     * Used to map the preferred partitioning of topics across
     * a set of brokers.  Once a it is deemed that a connection mostly
     * works with a set of targets configured in this map, the client
     * will be reconnected to the appropriate target.
     */
    @JsonbProperty("by_topic")
    public HashMap<String, Target> byTopic;

    /**
     * Maps broker names to broker URLs.
     */
    @JsonbProperty("brokers")
    public HashMap<String, String> brokers;


    @Override
    public String toString() {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(this);
    }

    public HashMap<String, String> getBrokers() {
        return brokers;
    }

    public void setBrokers(HashMap<String, String> brokers) {
        this.brokers = brokers;
    }

    public HashMap<String, Target> getByClientId() {
        return byClientId;
    }

    public void setByClientId(HashMap<String, Target> byClientId) {
        this.byClientId = byClientId;
    }

    public HashMap<String, Target> getByQueue() {
        return byQueue;
    }

    public void setByQueue(HashMap<String, Target> byQueue) {
        this.byQueue = byQueue;
    }

    public HashMap<String, Target> getBySourceIp() {
        return bySourceIp;
    }

    public void setBySourceIp(HashMap<String, Target> bySourceIp) {
        this.bySourceIp = bySourceIp;
    }

    public HashMap<String, Target> getByTopic() {
        return byTopic;
    }

    public void setByTopic(HashMap<String, Target> byTopic) {
        this.byTopic = byTopic;
    }

    public HashMap<String, Target> getByUserName() {
        return byUserName;
    }

    public void setByUserName(HashMap<String, Target> byUserName) {
        this.byUserName = byUserName;
    }
}
