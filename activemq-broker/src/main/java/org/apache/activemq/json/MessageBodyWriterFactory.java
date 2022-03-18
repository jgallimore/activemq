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
package org.apache.activemq.json;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * This class provides the entry point for the abstraction between JSON
 * libraries. We default to Jackson, but if that is not on the classpath, and a JSONB
 * implementation is, we'll use that instead.
 */
public class MessageBodyWriterFactory {

    private static Class<?> implementationClass = setupImplementation();

    public static <T> MessageBodyWriter<T> get(Class<T> cls) {
        if (implementationClass == null) {
            // locate the implementation
            setupImplementation();
        }

        try {
            return (MessageBodyWriter<T>) implementationClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Uh oh");
        }
    }

    private static Class<?> setupImplementation() {
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            return Class.forName("org.apache.activemq.json.JacksonMessageBodyWriter");
        } catch (Exception e) {
        }

        try {
            Class.forName("javax.json.bind.Jsonb");
            return Class.forName("org.apache.activemq.json.JacksonMessageBodyWriter");
        } catch (Exception e) {
        }

        return null;
    }


}
